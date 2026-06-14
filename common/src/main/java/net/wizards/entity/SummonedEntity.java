package net.wizards.entity;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.spell_engine.api.entity.TwoWayCollisionChecker;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellCooldownManager;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.target.EntityRelation;
import net.spell_engine.internals.target.EntityRelations;
import net.wizards.WizardsMod;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public abstract class SummonedEntity extends GolemEntity implements SpellSummoned, Tameable {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final TrackedData<Optional<UUID>> OWNER_UUID =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Byte> PHASE =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> COLLISION_MODE =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Float> BOUNDING_BOX_WIDTH =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> BOUNDING_BOX_HEIGHT =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> END_OF_PHASE_AGE =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> ANIMATION_ACTION =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> ATTACK_DURATION =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final byte PHASE_SPAWNING   = 0;
    private static final byte PHASE_ACTIVE     = 1;
    private static final byte PHASE_DESPAWNING = 2;

    protected static final byte ACTION_NONE          = 0;
    protected static final byte ACTION_MELEE         = 1;
    protected static final byte ACTION_SPELL_CAST    = 2;
    protected static final byte ACTION_SPELL_RELEASE = 3;

    protected static final int SPELL_RELEASE_DURATION_TICKS = 26;

    private int timeToLive = 0;
    private int spawnEndAge = 0;
    private int despawnStartAge = 0;
    @Nullable protected SummonBehaviour behaviour = null;

    public SummonedEntity(EntityType<? extends SummonedEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityDimensions getBaseDimensions(EntityPose pose) {
        float w = getDataTracker().get(BOUNDING_BOX_WIDTH);
        float h = getDataTracker().get(BOUNDING_BOX_HEIGHT);
        // 0 (or anything <= 0) = "no override is configured" — defer to vanilla, which
        // returns type.getDimensions().scaled(getScaleFactor()) (handles baby scale etc.).
        if (w <= 0 || h <= 0) return super.getBaseDimensions(pose);
        // `changing` (fixed=false) is required: `EntityDimensions.scaled()` short-circuits
        // and returns `this` unchanged when `fixed=true`, which would silently swallow the
        // GENERIC_SCALE attribute multiplier vanilla applies in LivingEntity.getDimensions.
        return EntityDimensions.changing(w, h);
    }

    @Override
    public boolean isPushable() {
        return collisionMode() != SummonBehaviour.Movement.CollisionMode.NONE;
        // return (behaviour == null || behaviour.movement.is_pushable) && super.isPushable();
    }

    @Override
    public boolean isAttackable() {
        return (behaviour == null || behaviour.is_attackable) && super.isAttackable();
    }

    @Override
    public boolean canHit() {
        return (behaviour == null || behaviour.is_attackable) && super.canHit();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return (behaviour == null || behaviour.is_attackable) && super.damage(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return (behaviour == null || !behaviour.is_attackable) && super.isInvulnerableTo(damageSource);
    }

    @Override
    public boolean isImmuneToExplosion(Explosion explosion) {
        return (behaviour == null || !behaviour.is_attackable) && super.isImmuneToExplosion(explosion);
    }

    public void takeKnockback(double strength, double x, double z) {
        if (behaviour != null && !behaviour.is_attackable) {
            return;
        }
        super.takeKnockback(strength, x, z);
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        if (behaviour != null) {
            SoundEvent custom = behaviour.sounds.hurtEvent.get();
            if (custom != null) return custom;
        }
        return super.getHurtSound(source);
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        if (behaviour != null) {
            SoundEvent custom = behaviour.sounds.deathEvent.get();
            if (custom != null) return custom;
        }
        return super.getDeathSound();
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (behaviour != null) {
            SoundEvent custom = behaviour.sounds.ambientEvent.get();
            if (custom != null) return custom;
        }
        return super.getAmbientSound();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (behaviour != null) {
            SoundEvent custom = behaviour.sounds.stepEvent.get();
            if (custom != null) {
                // Mirrors the vanilla footstep volume scaling (15% of normal) so the sound
                // doesn't dominate while the entity walks.
                this.playSound(custom, 0.15F, 1.0F);
                return;
            }
        }
        super.playStepSound(pos, state);
    }

    /// Broadcasts a configured sound from this entity's position. Silent when `sound` is null.
    private void playConfiguredSound(@Nullable SoundEvent sound) {
        if (sound == null) return;
        getWorld().playSound(null, getX(), getY(), getZ(),
                sound, getSoundCategory(), 1.0F, 1.0F);
    }

    private SummonBehaviour.Movement.CollisionMode collisionMode() {
        return SummonBehaviour.Movement.CollisionMode.values()[getDataTracker().get(COLLISION_MODE)];
    }

    @Override
    public boolean isCollidable() {
        if (collisionMode() == SummonBehaviour.Movement.CollisionMode.NONE) return false;
        return super.isCollidable();
    }

    @Override
    public boolean collidesWith(Entity other) {
        switch (collisionMode()) {
            case NONE -> { return false; }
            case ALL  -> { return super.collidesWith(other); }
            case ENEMIES -> {
                var collidesAccordingToRelation = false;
                if (getOwner() != null) {
                    var relation = EntityRelations.getRelation(getOwner(), other);
                    collidesAccordingToRelation = relation == EntityRelation.HOSTILE || relation == EntityRelation.NEUTRAL;
                }
                return super.collidesWith(other) && collidesAccordingToRelation;
            }
        }
        return super.collidesWith(other);
    }

    // Stops tickCramming() from pushing other entities through player.pushAwayFrom(this).
    // Both client and server call pushAway(), so the DataTracker-synced collisionMode() is enough.
    @Override
    protected void pushAway(Entity entity) {
        if (collisionMode() != SummonBehaviour.Movement.CollisionMode.NONE) {
            super.pushAway(entity);
        }
    }

    // Mirrors BarrierEntity's constructor pattern: install a TwoWayCollisionChecker reverseCollisionChecker
    // so SpellEngine's EntityCollision mixin also lets everything pass through this entity.
    // Runs on both sides: server when setBehaviour sets the value, client when the DataTracker update arrives.
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (data.equals(BOUNDING_BOX_WIDTH) || data.equals(BOUNDING_BOX_HEIGHT)) {
            calculateDimensions();
        }
        if (data.equals(COLLISION_MODE)) {
            if (collisionMode() == SummonBehaviour.Movement.CollisionMode.NONE) {
                ((TwoWayCollisionChecker) this).setReverseCollisionChecker(
                        entity -> TwoWayCollisionChecker.CollisionResult.PASS
                );
            } else {
                ((TwoWayCollisionChecker) this).setReverseCollisionChecker(null);
            }
        }
        if (data.equals(ANIMATION_ACTION)) {
            byte action = getDataTracker().get(ANIMATION_ACTION);
            attackAnimationState.stop();
            spellCastAnimationState.stop();
            spellReleaseAnimationState.stop();
            switch (action) {
                case ACTION_MELEE         -> attackAnimationState.start(age);
                case ACTION_SPELL_CAST    -> spellCastAnimationState.start(age);
                case ACTION_SPELL_RELEASE -> spellReleaseAnimationState.start(age);
            }
        }
    }

    public boolean isSpawning()   { return getDataTracker().get(PHASE) == PHASE_SPAWNING; }
    public boolean isDespawning() { return getDataTracker().get(PHASE) == PHASE_DESPAWNING; }
    public boolean isActive()     { return getDataTracker().get(PHASE) == PHASE_ACTIVE; }
    private void setPhase(byte phase) {
        byte previous = getDataTracker().get(PHASE);
        if (previous != phase && phase == PHASE_DESPAWNING && behaviour != null) {
            playConfiguredSound(behaviour.sounds.despawnEvent.get());
        }
        getDataTracker().set(PHASE, phase);
        int endAge = switch (phase) {
            case PHASE_SPAWNING   -> spawnEndAge;
            case PHASE_ACTIVE     -> despawnStartAge;
            case PHASE_DESPAWNING -> timeToLive;
            default               -> 0;
        };
        getDataTracker().set(END_OF_PHASE_AGE, endAge);
    }

    @Override
    public void onSummonedBySpell(SpellSummoned.Args args) {
        var sd = args.behaviour.spawn_despawn;
        this.spawnEndAge     = sd.spawn_ticks;
        this.timeToLive      = args.behaviour.timeToLive * 20 + sd.spawn_ticks + sd.despawn_ticks;
        this.despawnStartAge = this.timeToLive - sd.despawn_ticks;
        setOwnerUuid(args.owner.getUuid());
        setBehaviour(args.behaviour);
        // Defer to the first server tick: callers like WizardEntities run
        //   onSummonedBySpell() → setPos() → spawnEntity()
        // so playing here broadcasts from the entity's default (0,0,0) position. Setting
        // this flag fires the sound on the next tick(), by which point setPos has run and
        // the entity is in the world. NBT-loaded entities go through readCustomDataFromNbt
        // and skip this path, so they don't re-play the spawn sound on chunk reload.
        pendingSpawnSound = true;
    }

    private boolean pendingSpawnSound = false;

    private void setBehaviour(SummonBehaviour behaviour) {
        if (this.behaviour != null) return;
        this.behaviour = behaviour;
        this.initGoals();
        LivingEntity owner = getOwner();
        if (owner != null) {
            this.applyAttributeScaling(owner);
        }
        getDataTracker().set(COLLISION_MODE, (byte) behaviour.movement.collision.ordinal());
        // Dimensions are EntityType-seeded in initDataTracker. Only override when the
        // behaviour explicitly carries a non-null Dimensions block.
        if (behaviour.dimensions != null) {
            getDataTracker().set(BOUNDING_BOX_WIDTH,  behaviour.dimensions.width);
            getDataTracker().set(BOUNDING_BOX_HEIGHT, behaviour.dimensions.height);
        }
        calculateDimensions();
        if (!behaviour.movement.affected_by_gravity) {
            this.setNoGravity(true);
        }
        if (!behaviour.movement.is_pushable) {
            this.getAttributeInstance(EntityAttributes.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of("unpushable"), 9999, EntityAttributeModifier.Operation.ADD_VALUE));
        }
    }

    private void applyAttributeScaling(LivingEntity owner) {
        if (behaviour == null) return;
        var healthRatio = this.getHealth() / this.getMaxHealth();
        for (var entry : behaviour.attribute_scaling.entries) {
            var targetAttrOpt = Registries.ATTRIBUTE.getEntry(Identifier.of(entry.attribute_id));
            if (targetAttrOpt.isEmpty()) continue;
            var instance = this.getAttributeInstance(targetAttrOpt.get());
            if (instance == null) continue;

            double bonus = 0;
            for (var modifier : entry.modifiers) {
                var ownerAttrOpt = Registries.ATTRIBUTE.getEntry(Identifier.of(modifier.attribute_id));
                if (ownerAttrOpt.isEmpty()) continue;
                var ownerInstance = owner.getAttributeInstance(ownerAttrOpt.get());
                if (ownerInstance == null) continue;
                bonus += modifier.base + ownerInstance.getValue() * modifier.coefficient;
            }

            var modifierId = Identifier.of(WizardsMod.ID, "summon_scaling/" + entry.attribute_id.replace(":", "/"));
            instance.removeModifier(modifierId);
            instance.addTemporaryModifier(new EntityAttributeModifier(modifierId, bonus, EntityAttributeModifier.Operation.ADD_VALUE));
        }
        this.setHealth(this.getMaxHealth() * healthRatio);
    }

    @Override
    protected void initGoals() {
        if (behaviour == null) return;

        // --- Goal selector ---

        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(1, new PhaseBlockGoal());
        int actionPriority = 10;
        for (var action : behaviour.actions) {
            switch (action.type) {
                case MELEE_ATTACK -> goalSelector.add(actionPriority, new WindupMeleeAttackGoal(action.melee_attack));
                case SPELL_CAST -> goalSelector.add(actionPriority, new SpellCastGoal(action.spell_cast));
            }
            actionPriority++;
        }
        int priority = actionPriority;
        goalSelector.add(priority++, new FaceTargetGoal());
        var movement = behaviour.movement;
        if (movement.can_move) {
            if (movement.follow != null) {
                goalSelector.add(priority++, new FollowSummonerGoal());
            }
            goalSelector.add(priority++, new WanderAroundFarGoal(this, movement.wander.speed, movement.wander.probability));
        }
        if (behaviour.targeting.look_around) {
            goalSelector.add(priority++, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
            goalSelector.add(priority, new LookAroundGoal(this));
        }

        // --- Target selector ---

        if (behaviour.targeting.attack_with_owner) {
            targetSelector.add(1, new DefendOwnerGoal());
            targetSelector.add(3, new MirrorOwnerAttackGoal());
        }
        if (behaviour.targeting.revenge) {
            targetSelector.add(2, new RevengeGoal(this));
        }
        if (behaviour.targeting.automatic_targeting) {
            targetSelector.add(4, new ActiveTargetGoal<>(this, MobEntity.class, 10, true, false, this::shouldTarget));
        }
    }

    private boolean shouldTarget(LivingEntity candidate) {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        if (candidate == owner) return false;
        if (candidate instanceof Tameable t && owner.getUuid().equals(t.getOwnerUuid())) return false;
        return EntityRelations.getRelation(owner, candidate) == EntityRelation.HOSTILE;
    }

    private boolean canAttackTarget(@Nullable LivingEntity target, LivingEntity owner) {
        if (target == null) return false;
        EntityRelation relation = EntityRelations.getRelation(owner, target);
        return relation == EntityRelation.HOSTILE || relation == EntityRelation.NEUTRAL;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(PHASE, PHASE_SPAWNING);
        builder.add(COLLISION_MODE, (byte) SummonBehaviour.Movement.CollisionMode.ALL.ordinal());
        // 0 = sentinel for "no override". When the behaviour later sets a non-null
        // Dimensions, setBehaviour replaces these with the override values and
        // getBaseDimensions starts returning them; otherwise it falls through to
        // super.getBaseDimensions (the EntityType-declared size).
        builder.add(BOUNDING_BOX_WIDTH,  0F);
        builder.add(BOUNDING_BOX_HEIGHT, 0F);
        builder.add(END_OF_PHASE_AGE, 0);
        builder.add(ANIMATION_ACTION, ACTION_NONE);
        builder.add(ATTACK_DURATION, 10);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.getDataTracker().set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.getDataTracker().get(OWNER_UUID).orElse(null);
    }

    @Nullable
    public LivingEntity getOwner() {
        UUID uuid = getOwnerUuid();
        if (uuid == null) return null;
        return this.getWorld().getPlayerByUuid(uuid);
    }

    // --- Animation states ---
    // Standard set shared by all summoned entities. Subclasses may override the hook methods
    // below if they need non-standard animation behaviour.

    public final AnimationState spawnAnimationState        = new AnimationState();
    public final AnimationState despawnAnimationState      = new AnimationState();
    public final AnimationState idleAnimationState         = new AnimationState();
    public final AnimationState moveAnimationState         = new AnimationState();
    public final AnimationState attackAnimationState       = new AnimationState();
    public final AnimationState spellCastAnimationState    = new AnimationState();
    public final AnimationState spellReleaseAnimationState = new AnimationState();

    private int actionEndAge;

    /** Called each client tick. Default drives the five standard states from lifecycle phase. */
    protected void setupAnimationStates() {
        spawnAnimationState.setRunning(isSpawning(), this.age);
        boolean despawning = isDespawning();
        if (despawning && !despawnAnimationState.isRunning()) {
            // END_OF_PHASE_AGE = timeToLive during DESPAWNING (a future absolute age).
            // getTimeRunning() starts negative; with speedMultiplier=-1F it maps to
            // the end of the spawn animation and counts down to 0 as despawn progresses.
            despawnAnimationState.start(getDataTracker().get(END_OF_PHASE_AGE));
        } else if (!despawning) {
            despawnAnimationState.stop();
        }
        idleAnimationState.setRunning(isActive(), this.age);
        moveAnimationState.setRunning(isActive() && this.getVelocity().horizontalLength() > 0.01, this.age);
    }

    /** Called when a spell cast begins. */
    protected void onSpellCastStarted() {
        getDataTracker().set(ANIMATION_ACTION, ACTION_SPELL_CAST);
    }

    /** Called when a spell is released. */
    protected void onSpellReleased() {
        getDataTracker().set(ANIMATION_ACTION, ACTION_SPELL_RELEASE);
        actionEndAge = age + SPELL_RELEASE_DURATION_TICKS;
    }

    /** Called when a melee swing begins. The animation plays for `durationTicks`. */
    protected void onAttackAnimated(int durationTicks) {
        getDataTracker().set(ATTACK_DURATION, durationTicks);
        getDataTracker().set(ANIMATION_ACTION, ACTION_MELEE);
        actionEndAge = age + durationTicks;
    }

    /**
     * Playback-speed multiplier the model should pass to `updateAnimation` for the swing,
     * so a keyframed animation of `animationLengthTicks` is compressed/stretched to fit the
     * current swing's configured duration.
     */
    public float getAttackAnimationSpeed(float animationLengthTicks) {
        int duration = getDataTracker().get(ATTACK_DURATION);
        return duration > 0 ? animationLengthTicks / duration : 1F;
    }

    // --- Spell cooldowns ---

    private final SpellCooldownManager cooldownManager = new SpellCooldownManager(this);

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        } else {
            if (pendingSpawnSound) {
                pendingSpawnSound = false;
                if (behaviour != null) {
                    playConfiguredSound(behaviour.sounds.spawnEvent.get());
                }
            }
            cooldownManager.tickUpdate();
            if (timeToLive > 0 && this.age >= timeToLive) {
                this.discard();
            } else if (this.age < spawnEndAge) {
                setPhase(PHASE_SPAWNING);
            } else if (timeToLive > 0 && this.age >= despawnStartAge) {
                setPhase(PHASE_DESPAWNING);
            } else {
                setPhase(PHASE_ACTIVE);
            }
            byte action = getDataTracker().get(ANIMATION_ACTION);
            if ((action == ACTION_MELEE || action == ACTION_SPELL_RELEASE) && age >= actionEndAge) {
                getDataTracker().set(ANIMATION_ACTION, ACTION_NONE);
            } else if (action == ACTION_SPELL_CAST && !isAttacking()) {
                getDataTracker().set(ANIMATION_ACTION, ACTION_NONE);
            }
        }
    }

    // --- NBT ---

    private static final Gson GSON = new Gson();
    private static final String NBT_OWNER_UUID        = "OwnerUUID";
    private static final String NBT_TTL               = "TTL";
    private static final String NBT_SPAWN_END_AGE     = "SpawnEndAge";
    private static final String NBT_DESPAWN_START_AGE = "DespawnStartAge";
    private static final String NBT_BEHAVIOUR         = "Behaviour";

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid(NBT_OWNER_UUID)) {
            setOwnerUuid(nbt.getUuid(NBT_OWNER_UUID));
        }
        this.timeToLive      = nbt.getInt(NBT_TTL);
        this.spawnEndAge     = nbt.getInt(NBT_SPAWN_END_AGE);
        this.despawnStartAge = nbt.getInt(NBT_DESPAWN_START_AGE);
        if (nbt.contains(NBT_BEHAVIOUR)) {
            var behaviour = GSON.fromJson(nbt.getString(NBT_BEHAVIOUR), SummonBehaviour.class);
            setBehaviour(behaviour);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        UUID uuid = getOwnerUuid();
        if (uuid != null) {
            nbt.putUuid(NBT_OWNER_UUID, uuid);
        }
        nbt.putInt(NBT_TTL, this.timeToLive);
        nbt.putInt(NBT_SPAWN_END_AGE, this.spawnEndAge);
        nbt.putInt(NBT_DESPAWN_START_AGE, this.despawnStartAge);
        if (this.behaviour != null) {
            nbt.putString(NBT_BEHAVIOUR, GSON.toJson(this.behaviour));
        }
    }

    // --- Inner goal classes ---

    // Targets whoever attacked the owner (mirrors TrackOwnerAttackerGoal)
    private class DefendOwnerGoal extends TrackTargetGoal {
        private LivingEntity attacker;
        private int lastAttackedTime;

        public DefendOwnerGoal() {
            super(SummonedEntity.this, false);
            setControls(EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = getOwner();
            if (owner == null) return false;
            attacker = owner.getAttacker();
            int time = owner.getLastAttackedTime();
            return time != lastAttackedTime
                    && canTrack(attacker, TargetPredicate.DEFAULT)
                    && canAttackTarget(attacker, owner);
        }

        @Override
        public void start() {
            SummonedEntity.this.setTarget(attacker);
            LivingEntity owner = getOwner();
            if (owner != null) lastAttackedTime = owner.getLastAttackedTime();
            super.start();
        }
    }

    // Joins the owner's current attack target (mirrors AttackWithOwnerGoal)
    private class MirrorOwnerAttackGoal extends TrackTargetGoal {
        private LivingEntity attacking;
        private int lastAttackTime;

        public MirrorOwnerAttackGoal() {
            super(SummonedEntity.this, false);
            setControls(EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = getOwner();
            if (owner == null) return false;
            attacking = owner.getAttacking();
            int time = owner.getLastAttackTime();
            return time != lastAttackTime
                    && canTrack(attacking, TargetPredicate.DEFAULT)
                    && canAttackTarget(attacking, owner);
        }

        @Override
        public void start() {
            SummonedEntity.this.setTarget(attacking);
            LivingEntity owner = getOwner();
            if (owner != null) lastAttackTime = owner.getLastAttackTime();
            super.start();
        }
    }

    private class FollowSummonerGoal extends Goal {
        // Last horizontal velocity of the owner that was significant enough to determine orientation.
        // Null until the owner is seen moving; falls back to north when still null.
        @Nullable private Vec3d lastOwnerForward = null;

        public FollowSummonerGoal() {
            setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        private SummonBehaviour.Movement.Follow follow() {
            return behaviour.movement.follow;
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = getOwner();
            if (owner == null) return false;
            float start = follow().start_distance;
            return squaredDistanceTo(owner) > start * start;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity owner = getOwner();
            if (owner == null) return false;
            float stop = follow().stop_distance;
            return squaredDistanceTo(owner) > stop * stop;
        }

        @Override
        public void start() {
            LivingEntity owner = getOwner();
            if (owner == null) return;
            Vec3d target = computeFollowTarget(owner);
            getNavigation().startMovingTo(target.x, target.y, target.z, 1.0);
        }

        @Override
        public void tick() {
            LivingEntity owner = getOwner();
            if (owner == null) return;
            Vec3d target = computeFollowTarget(owner);
            float teleportDist = follow().teleport_after_distance;
            if (teleportDist > 0 && squaredDistanceTo(owner) > teleportDist * teleportDist) {
                teleport(target.x, target.y, target.z, false);
            } else {
                getNavigation().startMovingTo(target.x, target.y, target.z, 1.0);
            }
        }

        // Returns a position 2 blocks to the owner's right side.
        // Forward is taken from the owner's current velocity when significant; otherwise the last
        // cached forward is reused. If no valid forward has ever been observed, falls back to north.
        private Vec3d computeFollowTarget(LivingEntity owner) {
            Vec3d vel = owner.getVelocity();
            double horizSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
            if (horizSpeed > 0.02) {
                lastOwnerForward = new Vec3d(vel.x / horizSpeed, 0, vel.z / horizSpeed);
            }
            // North (-Z) as the last-resort default before the owner has ever moved
            Vec3d forward = lastOwnerForward != null ? lastOwnerForward : new Vec3d(0, 0, -1);
            // Right = forward rotated 90° clockwise (viewed from above) in Minecraft's coordinate system
            Vec3d right = new Vec3d(-forward.z, 0, forward.x);
            return owner.getPos().add(right.multiply(2.0));
        }
    }

    // Windup-based melee attack. Each swing has a fixed duration; impact lands at `windup`
    // ticks into the swing. Movement speed is multiplied by `movement_modifier` while
    // swinging. If the target leaves attack reach before the impact tick, the swing fails
    // silently (no damage). Optional AoE: on a successful impact, all valid hostiles within
    // `radius` of the primary target are also struck.
    private class WindupMeleeAttackGoal extends Goal {
        // Once a swing has begun, the target can drift up to this multiplier of the normal
        // attack reach (and configured max_range) before the impact-tick check whiffs.
        // Keeps the swing from being instantly cancelled by sub-block target jitter, while
        // the strict (1.0×) range still gates whether a new swing can start.
        private static final float IN_PROGRESS_RANGE_TOLERANCE = 1.10F;

        private final SummonBehaviour.Action.MeleeAttack config;
        private int swingTick = -1; // -1 = not swinging
        private int navUpdateCountdown = 0;

        public WindupMeleeAttackGoal(SummonBehaviour.Action.MeleeAttack config) {
            this.config = config;
            setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        // Approximation of vanilla MeleeAttackGoal's reach (entity width + target width).
        private double squaredAttackReach(LivingEntity target) {
            float reach = getWidth() * 2.0F + target.getWidth();
            return reach * reach;
        }

        private boolean isTargetInRange(LivingEntity target) {
            return isTargetInRange(target, 1.0F);
        }

        private boolean isTargetInRange(LivingEntity target, float toleranceMultiplier) {
            double sq = squaredDistanceTo(target);
            double tolSq = toleranceMultiplier * toleranceMultiplier;
            if (sq > squaredAttackReach(target) * tolSq) return false;
            if (config.max_range > 0) {
                float r = effectiveMaxRange();
                if (sq > r * r * tolSq) return false;
            }
            return true;
        }

        // max_range expanded by the entity's scale. At default scale 1.0 and default
        // attack_range_scaling 0.5, this is 1.5 × max_range.
        private float effectiveMaxRange() {
            return (float) (config.max_range * (1 + SummonedEntity.this.getScaleFactor() * config.attack_range_scaling));
        }

        // Cooldown between consecutive swings, in ticks (derived from attack speed alone).
        // Overlap with an in-progress swing is prevented separately by the `swingTick < 0` gate.
        private int swingInterval() {
            if (config.speed <= 0) return 1;
            return Math.max(1, Math.round(20F / config.speed));
        }

        // Tick within the swing at which the impact lands.
        // `windup` is a 0..1 fraction of `duration`; clamped so the impact never
        // falls outside the swing window.
        private int windupTick() {
            float f = Math.max(0F, Math.min(1F, config.windup));
            return Math.min(config.duration - 1, Math.round(config.duration * f));
        }

        // Mirrors PlayerEntity.getAttackCooldownProgress: 1.0F means fully recovered.
        private float attackCooldownProgress() {
            int interval = swingInterval();
            int ticksSince = age - SummonedEntity.this.getLastAttackTime();
            return Math.min(1F, ticksSince / (float) interval);
        }

        @Override
        public boolean canStart() {
            if (!isActive()) return false;
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive()) return false;
            // If a max_range cap is set, don't engage targets outside it (no chase).
            if (config.max_range > 0) {
                float r = effectiveMaxRange();
                if (squaredDistanceTo(target) > r * r) return false;
            }
            return true;
        }

        @Override
        public boolean shouldContinue() {
            if (swingTick >= 0) return true; // finish in-progress swing
            return canStart();
        }

        @Override
        public boolean shouldRunEveryTick() { return true; }

        @Override
        public void start() {
            swingTick = -1;
            navUpdateCountdown = 0;
        }

        @Override
        public void stop() {
            swingTick = -1;
            getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = getTarget();
            // If the target vanished mid-swing, abort the swing so shouldContinue() returns false
            // next tick and MOVE/LOOK get released — otherwise FollowSummonerGoal can never start.
            if (target == null || !target.isAlive()) {
                swingTick = -1;
                return;
            }
            getLookControl().lookAt(target, 30F, 30F);

            boolean inRange = isTargetInRange(target);

            // Begin a new swing only when not currently swinging, in range, and fully recovered.
            // Recovery uses vanilla LivingEntity.lastAttackTime: onAttacking(target) below sets it
            // to the current age, mirroring how PlayerEntity.getAttackCooldownProgress works.
            if (swingTick < 0 && inRange && attackCooldownProgress() >= 1F) {
                swingTick = 0;
                SummonedEntity.this.onAttacking(target); // saves vanilla lastAttackTime = age
                onAttackAnimated(config.duration);
                playConfiguredSound(config.swingEvent.get());
                LOGGER.info("[WindupMelee] attack-start entity={} target={} duration={} windup={}->tick{} radius={} interval={}",
                        SummonedEntity.this.getId(), target.getId(),
                        config.duration, config.windup, windupTick(), config.radius, swingInterval());
            }

            // Navigation:
            //   - already in range, between swings → hold position
            //   - otherwise → pursue, slowing to movement_modifier × movement_speed during the swing
            double moveSpeed = (swingTick >= 0)
                    ? config.movement_speed * config.movement_modifier
                    : config.movement_speed;
            navUpdateCountdown--;
            if (swingTick < 0 && inRange) {
                getNavigation().stop();
            } else if (moveSpeed > 0) {
                if (navUpdateCountdown <= 0) {
                    getNavigation().startMovingTo(target, moveSpeed);
                    navUpdateCountdown = 5;
                }
            } else {
                getNavigation().stop();
            }

            // Drive swing progression.
            if (swingTick >= 0) {
                if (swingTick == windupTick()) {
                    // Re-check reach with in-progress tolerance: if the target drifted only
                    // slightly out during the windup the swing still connects; only a clear
                    // dodge whiffs it.
                    boolean hit = isTargetInRange(target, IN_PROGRESS_RANGE_TOLERANCE);
                    LOGGER.info("[WindupMelee] attack-impact entity={} target={} tick={} hit={}",
                            SummonedEntity.this.getId(), target.getId(), swingTick, hit);
                    if (hit) {
                        performAttackImpact(target);
                    }
                }
                swingTick++;
                if (swingTick >= config.duration) {
                    LOGGER.info("[WindupMelee] attack-end entity={} target={}",
                            SummonedEntity.this.getId(), target.getId());
                    swingTick = -1;
                    // Force a NONE tick so the next swing's MELEE transition triggers a
                    // fresh AnimationState.start() on the client. Without this, back-to-back
                    // swings (interval <= duration) keep ANIMATION_ACTION at MELEE forever
                    // and only the first swing animates.
                    getDataTracker().set(ANIMATION_ACTION, ACTION_NONE);
                }
            }
        }

        private void performAttackImpact(LivingEntity primary) {
            // Played once per swing, before any tryAttack calls — AoE hits do not retrigger it.
            playConfiguredSound(config.impactEvent.get());
            tryAttack(primary);
            if (config.radius <= 0) return;
            LivingEntity owner = getOwner();
            Box box = primary.getBoundingBox().expand(config.radius);
            double radiusSq = (double) config.radius * config.radius;
            for (LivingEntity nearby : getWorld().getEntitiesByClass(LivingEntity.class, box, e -> true)) {
                if (nearby == primary) continue;
                if (nearby == SummonedEntity.this) continue;
                if (nearby == owner) continue;
                if (nearby instanceof Tameable t && owner != null && owner.getUuid().equals(t.getOwnerUuid())) continue;
                if (owner != null && !canAttackTarget(nearby, owner)) continue;
                if (nearby.squaredDistanceTo(primary) > radiusSq) continue;
                tryAttack(nearby);
            }
        }

    }

    // Holds MOVE/LOOK/JUMP controls during spawn and despawn phases, making the entity inactionable.
    private class PhaseBlockGoal extends Goal {
        public PhaseBlockGoal() {
            setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
        }

        @Override
        public boolean canStart() { return !isActive(); }

        @Override
        public boolean shouldContinue() { return !isActive(); }
    }

    // Holds LOOK control whenever the entity has an attack target, keeping it facing that target
    // between spell casts and melee attacks. Sits just below action goals so it is displaced
    // when any combat goal is active but takes over as soon as they release controls.
    private class FaceTargetGoal extends Goal {
        public FaceTargetGoal() {
            setControls(EnumSet.of(Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity target = getTarget();
            return target != null && target.isAlive() && isActive();
        }

        @Override
        public boolean shouldContinue() { return canStart(); }

        @Override
        public boolean shouldRunEveryTick() { return true; }

        @Override
        public void tick() {
            LivingEntity target = getTarget();
            if (target == null) return;
            getLookControl().lookAt(target, 30F, 30F);
            setBodyYaw(getHeadYaw());
        }
    }

    private class SpellCastGoal extends Goal {
        private final SummonBehaviour.Action.SpellCast config;

        // Spell registry entry — resolved lazily and cached (registry is stable at runtime)
        @Nullable private RegistryEntry<Spell> spellEntry = null;
        private boolean spellLookupAttempted = false;

        // Per-activation state
        private int castTick = 0;
        private int castDuration = 1;
        private boolean released = false;
        private int targetSeeingTicker = 0;

        public SpellCastGoal(SummonBehaviour.Action.SpellCast config) {
            this.config = config;
            setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Nullable
        private RegistryEntry<Spell> resolveSpell() {
            if (spellLookupAttempted) return spellEntry;
            spellLookupAttempted = true;
            var id = Identifier.of(config.spell_id);
            spellEntry = SpellRegistry.from(getWorld()).getEntry(id).orElse(null);
            return spellEntry;
        }

        @Override
        public boolean shouldRunEveryTick() { return true; }

        @Override
        public boolean canStart() {
            var entry = resolveSpell();
            if (entry == null) return false;
            var spell = entry.value();
            if (spell.active == null) return false;           // ACTIVE spells only
            if (SpellHelper.isChanneled(spell)) return false; // INSTANT or CHARGE only
            if (!isActive()) return false;                    // not in spawn/despawn phase
            var target = getTarget();
            if (target == null || !target.isAlive()) return false;
            return !cooldownManager.isCoolingDown(entry);
        }

        @Override
        public void start() {
            castTick = 0;
            released = false;
            targetSeeingTicker = 0;
            var entry = spellEntry; // already resolved by canStart()
            if (entry != null) {
                var spell = entry.value();
                castDuration = SpellHelper.isInstant(spell)
                        ? 1
                        : SpellHelper.getCastTimeDetails(SummonedEntity.this, spell).length();
                if (castDuration <= 0) castDuration = 1;
            }
            onSpellCastStarted();
            setAttacking(true);
        }

        @Override
        public boolean shouldContinue() {
            if (released) return false;
            if (!isActive()) return false;
            var target = getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void stop() {
            setAttacking(false);
            getNavigation().stop();
        }

        @Override
        public void tick() {
            if (released) return;
            var target = getTarget();
            if (target == null) return;
            var entry = spellEntry;
            if (entry == null) return;
            var spell = entry.value();

            // Desired range based on target movement direction
            Vec3d toTarget = target.getPos().subtract(SummonedEntity.this.getPos()).normalize();
            double dot = target.getVelocity().dotProduct(toTarget);
            // dot > 0  → target fleeing    → close in (50% range)
            // dot < 0  → target approaching → hold back (90% range)
            float rangeFraction = (dot > 0.01) ? 0.5f : (dot < -0.01) ? 0.9f : 0.7f;
            float desiredRange = spell.range * rangeFraction;
            double desiredRangeSq = (double) desiredRange * desiredRange;

            // Line-of-sight tracking
            boolean canSee = getVisibilityCache().canSee(target);
            if (canSee) { if (targetSeeingTicker < 10) targetSeeingTicker++; }
            else         { if (targetSeeingTicker > 0)  targetSeeingTicker--; }

            // Navigation
            double distSq = squaredDistanceTo(target);
            if (distSq > desiredRangeSq || targetSeeingTicker <= 0) {
                getNavigation().startMovingTo(target, 1.0);
            } else {
                getNavigation().stop();
            }

            // Compute exact yaw/pitch to face the target's eyes
            Vec3d toTargetEye = target.getEyePos().subtract(getEyePos()).normalize();
            float faceYaw   = (float)  Math.toDegrees(Math.atan2(-toTargetEye.x, toTargetEye.z));
            float facePitch = (float) -Math.toDegrees(Math.asin(Math.max(-1.0, Math.min(1.0, toTargetEye.y))));

            boolean isFacing;
            if (castTick > 0) {
                // Casting in progress — lock rotation directly onto target every tick
                setYaw(faceYaw);
                setHeadYaw(faceYaw);
                setBodyYaw(faceYaw);
                setPitch(facePitch);
                isFacing = true;
            } else {
                // Not yet casting — turn gradually via look control
                getLookControl().lookAt(target, 30F, 30F);
                setBodyYaw(getHeadYaw());
                Vec3d lookDir = getRotationVector(getPitch(), getHeadYaw()).normalize();
                isFacing = lookDir.dotProduct(toTargetEye) > 0.95; // ~18° threshold
            }

            // Cast progress — only while in range, target visible, and facing the target
            if (distSq <= desiredRangeSq && targetSeeingTicker > 0 && isFacing) {
                castTick++;
            }
            if (castTick >= castDuration) {
                releaseSpell(target, entry, spell);
                released = true;
            }
        }

        private void releaseSpell(LivingEntity target, RegistryEntry<Spell> entry, Spell spell) {
            if (getWorld().isClient()) return;
            // Snap rotation to face the target exactly — targetAndPerformSpell uses the caster's
            // look vector for raycasting (AIM/BEAM) and projectile direction.
            Vec3d toTarget = target.getEyePos().subtract(getEyePos()).normalize();
            float releaseYaw   = (float)  Math.toDegrees(Math.atan2(-toTarget.x, toTarget.z));
            float releasePitch = (float) -Math.toDegrees(Math.asin(Math.max(-1.0, Math.min(1.0, toTarget.y))));
            setYaw(releaseYaw);
            setHeadYaw(releaseYaw);
            setBodyYaw(releaseYaw);
            setPitch(releasePitch);
            SpellHelper.targetAndPerformSpell(getWorld(), SummonedEntity.this, entry);
            onSpellReleased();

            // Cooldown: use spell's own duration if set, else fall back to config override (ticks)
            int cooldownTicks;
            if (spell.cost.cooldown != null && spell.cost.cooldown.duration > 0) {
                cooldownTicks = Math.round(
                        SpellHelper.getCooldownDuration(SummonedEntity.this, entry) * 20F);
            } else {
                cooldownTicks = config.cooldown; // already in ticks; default = 20
            }
            if (cooldownTicks > 0) {
                cooldownManager.set(entry, cooldownTicks);
            }
        }
    }
}
