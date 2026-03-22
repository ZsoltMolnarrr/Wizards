package net.wizards.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.spell_engine.internals.target.EntityRelation;
import net.spell_engine.internals.target.EntityRelations;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class FrostElementalEntity extends GolemEntity implements SpellSummoned {
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
    public void onSummonedBySpell(SpellSummoned.Args args) {
        setOwnerUuid(args.owner.getUuid());
        this.behaviour = args.behaviour;
        this.timeToLive = args.behaviour.timeToLive * 20;
        initGoals();
    }

    @Override
    protected void initGoals() {
        if (behaviour == null) return;

        goalSelector.add(0, new SwimGoal(this));


        if (behaviour.targeting.revenge) {
            targetSelector.add(1, new RevengeGoal(this));
        }

        goalSelector.add(2, new MeleeAttackGoal(this, 1.2, false));
        goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        goalSelector.add(9, new LookAroundGoal(this));

        if (behaviour.movement == SummonBehaviour.Movement.FOLLOW) {
            goalSelector.add(3, new FollowSummonerGoal());
            goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        }

        if (behaviour.targeting.revenge) {
            targetSelector.add(2, new FollowOwnerTargetGoal());
        }

        if (behaviour.targeting.automatic == SummonBehaviour.Targeting.AutomaticMode.HOSTILE) {
            targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 10, true, false, this::shouldTarget));
        }
    }

    private boolean shouldTarget(LivingEntity candidate) {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        if (candidate == owner) return false;
        EntityRelation relation = EntityRelations.getRelation(owner, candidate);
        return relation == EntityRelation.HOSTILE;
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

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && timeToLive > 0 && this.age >= timeToLive) {
            this.discard();
        }
    }

    private static final String NBT_OWNER_UUID = "OwnerUUID";
    private static final String NBT_TTL = "TTL";

//    @Override
//    protected void readCustomDataFromNbt(NbtCompound nbt) {
//        if (nbt.containsUuid(NBT_OWNER_UUID)) {
//            setOwnerUuid(nbt.getUuid(NBT_OWNER_UUID));
//        }
//        this.timeToLive = nbt.getInt(NBT_TTL);
//    }
//
//    @Override
//    protected void writeCustomDataToNbt(NbtCompound nbt) {
//        UUID uuid = getOwnerUuid();
//        if (uuid != null) {
//            nbt.putUuid(NBT_OWNER_UUID, uuid);
//        }
//        nbt.putInt(NBT_TTL, this.timeToLive);
//    }

    private class FollowOwnerTargetGoal extends Goal {
        public FollowOwnerTargetGoal() {
            setControls(EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = getOwner();
            if (owner == null) return false;
            LivingEntity ownerTarget = owner.getAttacking();
            if (ownerTarget == null) return false;
            setTarget(ownerTarget);
            return true;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity ownerTarget = getOwner() != null ? getOwner().getAttacking() : null;
            return ownerTarget != null && getTarget() == ownerTarget;
        }
    }

    private class FollowSummonerGoal extends Goal {
        private static final float START_DISTANCE = 10F;
        private static final float STOP_DISTANCE = 4F;

        public FollowSummonerGoal() {
            setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = getOwner();
            if (owner == null) return false;
            return squaredDistanceTo(owner) > START_DISTANCE * START_DISTANCE;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity owner = getOwner();
            if (owner == null) return false;
            return squaredDistanceTo(owner) > STOP_DISTANCE * STOP_DISTANCE;
        }

        @Override
        public void start() {
            LivingEntity owner = getOwner();
            if (owner != null) {
                getNavigation().startMovingTo(owner, 1.0);
            }
        }

        @Override
        public void tick() {
            LivingEntity owner = getOwner();
            if (owner != null) {
                getNavigation().startMovingTo(owner, 1.0);
            }
        }
    }
}
