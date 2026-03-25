package net.wizards.entity;

import com.google.gson.Gson;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spell_engine.internals.target.EntityRelation;
import net.spell_engine.internals.target.EntityRelations;
import net.wizards.WizardsMod;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class FrostElementalEntity extends GolemEntity implements SpellSummoned, Tameable {
    public static final Identifier ID = Identifier.of(WizardsMod.ID, "frost_elemental");
    public static EntityType<FrostElementalEntity> TYPE;

    private static final TrackedData<Optional<UUID>> OWNER_UUID =
            DataTracker.registerData(FrostElementalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private int timeToLive = 0;
    @Nullable private SummonBehaviour behaviour = null;

    public FrostElementalEntity(EntityType<? extends FrostElementalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        var cfg = WizardsMod.entityConfig.value.entries.get(ID.getPath());
        var builder = LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, cfg.common.follow_range)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, cfg.common.max_health)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, cfg.common.movement_speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, cfg.common.attack_damage);
        for (var custom : cfg.custom) {
            Registries.ATTRIBUTE.getEntry(Identifier.of(custom.id))
                    .ifPresent(entry -> builder.add(entry, custom.value));
        }
        return builder;
    }

    @Override
    public boolean isPushable() {
        return behaviour != null && behaviour.movement.is_pushable;
    }

    @Override
    public boolean collidesWith(Entity other) {
        if (behaviour != null) {
            switch (behaviour.movement.collision) {
                case NONE -> {
                    return false;
                }
                case ALL -> {
                    return super.collidesWith(other);
                }
                case ENEMIES -> {
                    var collidesAccordingToRelation = false;
                    if (getOwner() != null) {
                         var relation = EntityRelations.getRelation(getOwner(), other);
                        collidesAccordingToRelation = relation == EntityRelation.HOSTILE || relation == EntityRelation.NEUTRAL;
                    }
                    return super.collidesWith(other) && collidesAccordingToRelation;
                }
            }
        }
        return super.collidesWith(other);
    }

    @Override
    public void onSummonedBySpell(SpellSummoned.Args args) {
        this.timeToLive = args.behaviour.timeToLive * 20;
        setOwnerUuid(args.owner.getUuid());
        setBehaviour(args.behaviour);
    }

    private void setBehaviour(SummonBehaviour behaviour) {
        if (this.behaviour != null) {
            return;
        }
        this.behaviour = behaviour;
        this.initGoals();
        LivingEntity owner = getOwner();
        if (owner != null) {
            this.applyAttributeScaling(owner);
        }
    }

    private void applyAttributeScaling(LivingEntity owner) {
        if (behaviour == null) return;
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
                bonus += ownerInstance.getValue() * modifier.coefficient;
            }

            var modifierId = Identifier.of(WizardsMod.ID, "summon_scaling/" + entry.attribute_id.replace(":", "/"));
            instance.removeModifier(modifierId);
            instance.addTemporaryModifier(new EntityAttributeModifier(modifierId, bonus, EntityAttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    protected void initGoals() {
        if (behaviour == null) return;

        // --- Goal selector ---

        goalSelector.add(0, new SwimGoal(this));
        int actionPriority = 2;
        for (var action : behaviour.actions) {
            switch (action.type) {
                case MELEE_ATTACK -> {
                    var cfg = action.melee_attack;
                    if (cfg.max_range > 0) {
                        goalSelector.add(actionPriority, new RangedMeleeAttackGoal(cfg.speed, cfg.max_range));
                    } else {
                        goalSelector.add(actionPriority, new MeleeAttackGoal(this, cfg.speed, false));
                    }
                }
                case SPELL_CAST -> goalSelector.add(actionPriority, new SpellCastGoal(action.spell_cast));
            }
            actionPriority++;
        }
        int priority = actionPriority;
        var movement = behaviour.movement;
        if (movement.can_move) {
            if (movement.follow != null) {
                goalSelector.add(priority++, new FollowSummonerGoal());
            }
            goalSelector.add(priority++, new WanderAroundFarGoal(this, movement.wander.speed, movement.wander.probability));
        }
        goalSelector.add(priority++, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        goalSelector.add(priority, new LookAroundGoal(this));

        // --- Target selector ---

        if (behaviour.targeting.attack_with_owner) {
            targetSelector.add(1, new DefendOwnerGoal());
            targetSelector.add(2, new MirrorOwnerAttackGoal());
        }
        if (behaviour.targeting.revenge) {
            targetSelector.add(3, new RevengeGoal(this));
        }
        if (behaviour.targeting.automatic_targeting) {
            targetSelector.add(4, new ActiveTargetGoal<>(this, MobEntity.class, 10, true, false, this::shouldTarget));
        }
    }

    private boolean shouldTarget(LivingEntity candidate) {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        if (candidate == owner) return false;
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


    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        } else if (timeToLive > 0 && this.age >= timeToLive) {
            this.discard();
        }
    }

    private void setupAnimationStates() {
        idleAnimationState.setRunning(true, this.age);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);
        if (success) attackAnimationState.start(this.age);
        return success;
    }

    private static final Gson GSON = new Gson();
    private static final String NBT_OWNER_UUID = "OwnerUUID";
    private static final String NBT_TTL = "TTL";
    private static final String NBT_BEHAVIOUR = "Behaviour";

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid(NBT_OWNER_UUID)) {
            setOwnerUuid(nbt.getUuid(NBT_OWNER_UUID));
        }
        this.timeToLive = nbt.getInt(NBT_TTL);
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
        if (this.behaviour != null) {
            nbt.putString(NBT_BEHAVIOUR, GSON.toJson(this.behaviour));
        }
    }

    // Targets whoever attacked the owner (mirrors TrackOwnerAttackerGoal)
    private class DefendOwnerGoal extends TrackTargetGoal {
        private LivingEntity attacker;
        private int lastAttackedTime;

        public DefendOwnerGoal() {
            super(FrostElementalEntity.this, false);
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
            FrostElementalEntity.this.setTarget(attacker);
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
            super(FrostElementalEntity.this, false);
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
            FrostElementalEntity.this.setTarget(attacking);
            LivingEntity owner = getOwner();
            if (owner != null) lastAttackTime = owner.getLastAttackTime();
            super.start();
        }
    }

    private class FollowSummonerGoal extends Goal {
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
            tryNavigateToOwner();
        }

        @Override
        public void tick() {
            LivingEntity owner = getOwner();
            if (owner == null) return;
            float teleportDist = follow().teleport_after_distance;
            if (teleportDist > 0 && squaredDistanceTo(owner) > teleportDist * teleportDist) {
                teleport(owner.getX(), owner.getY(), owner.getZ(), false);
            } else {
                tryNavigateToOwner();
            }
        }

        private void tryNavigateToOwner() {
            LivingEntity owner = getOwner();
            if (owner != null) getNavigation().startMovingTo(owner, 1.0);
        }
    }

    // Melee attack that only activates when the target is within a configured range.
    // Unlike the default MeleeAttackGoal, the entity will not chase a distant target to engage.
    private class RangedMeleeAttackGoal extends MeleeAttackGoal {
        private final float maxRange;

        public RangedMeleeAttackGoal(float speed, float maxRange) {
            super(FrostElementalEntity.this, speed, false);
            this.maxRange = maxRange;
        }

        @Override
        public boolean canStart() {
            LivingEntity target = getTarget();
            if (target == null) return false;
            if (squaredDistanceTo(target) > maxRange * maxRange) return false;
            return super.canStart();
        }
    }

    private class SpellCastGoal extends Goal {
        private final SummonBehaviour.Action.SpellCast config;

        public SpellCastGoal(SummonBehaviour.Action.SpellCast config) {
            this.config = config;
            setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() { return false; }
    }
}
