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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class SummonedEntity extends GolemEntity implements SpellSummoned, Tameable {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final TrackedData<Optional<UUID>> OWNER_UUID =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<Byte> PHASE =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Byte> COLLISION_MODE =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Float> BOUNDING_BOX_WIDTH =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> BOUNDING_BOX_HEIGHT =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> END_OF_PHASE_AGE =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.INTEGER);
    // One packed tracker per action type — kept separate so a spell cast and a melee swing
    // can animate in parallel without one stomping the other's state.
    //
    // Packed layout (same for all three):
    //   bits  0..7   variant   (0..255)
    //   bits  8..23  duration  (ticks; 0 = inactive)
    //   bits 24..55  startAge  (entity age when the action began)
    //
    // Why packed (instead of three primitive trackers per descriptor): DataTracker.set()
    // silently drops no-op writes (value equals current → not dirty → not synced → client
    // onTrackedDataSet never fires). When swings chain (target dies mid-swing, new target
    // acquired the same tick), a plain action/duration tracker could re-set to the same
    // value and skip the packet, leaving the animation desynced. Including the monotonic
    // startAge in the same long guarantees every action start changes the value, forcing
    // a sync.
    public static final TrackedData<Long> ATTACK_ANIMATION =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.LONG);
    public static final TrackedData<Long> SPELL_CAST_ANIMATION =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.LONG);
    public static final TrackedData<Long> SPELL_RELEASE_ANIMATION =
            DataTracker.registerData(SummonedEntity.class, TrackedDataHandlerRegistry.LONG);

    private static long packAnim(int variant, int duration, int startAge) {
        return ((long)(variant  & 0xFF))
             | (((long)(duration & 0xFFFF)) << 8)
             | ((((long) startAge) & 0xFFFFFFFFL) << 24);
    }
    private static int animVariant(long v)  { return (int)  (v        & 0xFF); }
    private static int animDuration(long v) { return (int) ((v >>> 8)  & 0xFFFF); }
    private static int animStartAge(long v) { return (int)  (v >>> 24); }

    // Sentinel duration meaning "runs until an explicit stop is sent" (e.g., a spell cast,
    // whose duration isn't known up front). Max value of the 16-bit duration field.
    private static final int DURATION_ENDLESS = 0xFFFF;

    private static final byte PHASE_SPAWNING   = 0;
    private static final byte PHASE_ACTIVE     = 1;
    private static final byte PHASE_DESPAWNING = 2;

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
    public void playConfiguredSound(@Nullable SoundEvent sound) {
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
        if (data.equals(ATTACK_ANIMATION)) {
            syncActionAnimationState(attackAnimationState, ATTACK_ANIMATION);
        } else if (data.equals(SPELL_CAST_ANIMATION)) {
            syncActionAnimationState(spellCastAnimationState, SPELL_CAST_ANIMATION);
        } else if (data.equals(SPELL_RELEASE_ANIMATION)) {
            syncActionAnimationState(spellReleaseAnimationState, SPELL_RELEASE_ANIMATION);
        }
    }

    private void syncActionAnimationState(AnimationState state, TrackedData<Long> descriptor) {
        if (animDuration(getDataTracker().get(descriptor)) > 0) {
            state.start(age);
        } else {
            state.stop();
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
                case MELEE_ATTACK -> goalSelector.add(actionPriority, new DynamicMeleeAttackGoal(this, action.melee_attack));
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
        // Friendly goal added first (lower priority number) so wounded-ally healing takes
        // precedence over hostile acquisition when BOTH is configured.
        switch (behaviour.targeting.automatic_targeting) {
            case FRIENDLY -> targetSelector.add(4, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, this::shouldHealTarget));
            case HOSTILE  -> targetSelector.add(4, new ActiveTargetGoal<>(this, MobEntity.class,    10, true, false, this::shouldTarget));
            case BOTH     -> {
                targetSelector.add(4, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, this::shouldHealTarget));
                targetSelector.add(5, new ActiveTargetGoal<>(this, MobEntity.class,    10, true, false, this::shouldTarget));
            }
            case NONE     -> { /* no auto-acquisition */ }
        }
    }

    private boolean shouldTarget(LivingEntity candidate) {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        if (candidate == owner) return false;
        if (candidate instanceof Tameable t && owner.getUuid().equals(t.getOwnerUuid())) return false;
        return EntityRelations.getRelation(owner, candidate) == EntityRelation.HOSTILE;
    }

    private boolean shouldHealTarget(LivingEntity candidate) {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        if (candidate == this) return false;
        // Only wounded entities are heal targets — otherwise the goal would lock onto
        // a full-health ally and the heal action would burn cooldowns on no-ops.
        if (candidate.getHealth() >= candidate.getMaxHealth()) return false;
        if (candidate == owner) return true;
        if (candidate instanceof Tameable t && owner.getUuid().equals(t.getOwnerUuid())) return true;
        return EntityRelations.getRelation(owner, candidate) == EntityRelation.FRIENDLY;
    }

    public boolean canAttackTarget(@Nullable LivingEntity target, LivingEntity owner) {
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
        // duration = 0 → all action animations start inactive.
        long inactive = packAnim(0, 0, 0);
        builder.add(ATTACK_ANIMATION, inactive);
        builder.add(SPELL_CAST_ANIMATION, inactive);
        builder.add(SPELL_RELEASE_ANIMATION, inactive);
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

        // Auto-stop fixed-duration action animations once their duration has elapsed.
        autoStopActionAnimation(attackAnimationState,       ATTACK_ANIMATION);
        autoStopActionAnimation(spellCastAnimationState,    SPELL_CAST_ANIMATION);
        autoStopActionAnimation(spellReleaseAnimationState, SPELL_RELEASE_ANIMATION);
    }

    private void autoStopActionAnimation(AnimationState state, TrackedData<Long> descriptor) {
        if (!state.isRunning()) return;
        long d = getDataTracker().get(descriptor);
        int duration = animDuration(d);
        if (duration <= 0) {
            state.stop();
            return;
        }
        if (duration == DURATION_ENDLESS) return; // runs until an explicit stop arrives
        int startAge = animStartAge(d);
        if (age - startAge >= duration) state.stop();
    }

    /**
     * Called when a spell cast begins. The cast animation runs until `onSpellCastEnded()`
     * is invoked (no time-based auto-stop) — cast length isn't always known when the cast
     * starts, and short, premature stops felt choppy.
     */
    protected void onSpellCastStarted(int variant) {
        getDataTracker().set(SPELL_CAST_ANIMATION, packAnim(variant, DURATION_ENDLESS, age));
    }

    /** Stops the cast animation (e.g., on cancel or release). */
    protected void onSpellCastEnded() {
        // duration=0 = inactive; age in the payload guarantees a dirty write so the client
        // gets the stop transition even when the previous value was already "stopped".
        getDataTracker().set(SPELL_CAST_ANIMATION, packAnim(0, 0, age));
    }

    /** Called when a spell is released. Animation plays for `durationTicks`. */
    protected void onSpellReleased(int variant, int durationTicks) {
        getDataTracker().set(SPELL_RELEASE_ANIMATION, packAnim(variant, durationTicks, age));
    }

    /** Called when a melee swing begins. The animation plays for `durationTicks`. */
    protected void onAttackAnimated(int durationTicks, int variant) {
        getDataTracker().set(ATTACK_ANIMATION, packAnim(variant, durationTicks, age));
    }

    public int getAttackVariant()        { return animVariant(getDataTracker().get(ATTACK_ANIMATION)); }
    public int getSpellCastVariant()     { return animVariant(getDataTracker().get(SPELL_CAST_ANIMATION)); }
    public int getSpellReleaseVariant()  { return animVariant(getDataTracker().get(SPELL_RELEASE_ANIMATION)); }

    // Empty/null pool → variant 1 (the always-present default).
    public int pickVariant(@Nullable List<Integer> pool) {
        if (pool == null || pool.isEmpty()) return 1;
        return pool.get(random.nextInt(pool.size()));
    }

    /**
     * Playback-speed multiplier the model should pass to `updateAnimation` for the swing,
     * so a keyframed animation of `animationLengthTicks` is compressed/stretched to fit the
     * current swing's configured duration.
     */
    public float getAttackAnimationSpeed(float animationLengthTicks) {
        int duration = animDuration(getDataTracker().get(ATTACK_ANIMATION));
        return duration > 0 ? animationLengthTicks / duration : 1F;
    }

    /** Same as `getAttackAnimationSpeed` but for the spell-release animation. */
    public float getSpellReleaseAnimationSpeed(float animationLengthTicks) {
        int duration = animDuration(getDataTracker().get(SPELL_RELEASE_ANIMATION));
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
            // Action animations are self-terminating now:
            //   - ATTACK_ANIMATION / SPELL_RELEASE_ANIMATION carry a fixed duration; the
            //     client stops their AnimationState once `age - startAge >= duration`.
            //   - SPELL_CAST_ANIMATION is ended explicitly by SpellCastGoal via
            //     onSpellCastEnded() on release or cancellation.
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
            onSpellCastStarted(pickVariant(config.cast_animation_variants));
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
            // Covers both the natural-end path (release ran, goal then stopped next tick)
            // and early cancellation (target lost mid-cast, etc.). Idempotent.
            onSpellCastEnded();
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
            onSpellCastEnded();
            onSpellReleased(pickVariant(config.release_animation_variants), config.release_animation_duration);

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
