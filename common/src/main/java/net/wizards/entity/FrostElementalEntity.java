package net.wizards.entity;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.wizards.WizardsMod;

public class FrostElementalEntity extends SummonedEntity {
    public static final Identifier ID = Identifier.of(WizardsMod.ID, "frost_elemental");
    public static EntityType<FrostElementalEntity> TYPE;

    private static final TrackedData<Byte> ANIMATION_ACTION =
            DataTracker.registerData(FrostElementalEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final byte ACTION_NONE          = 0;
    private static final byte ACTION_MELEE         = 1;
    private static final byte ACTION_SPELL_CAST    = 2;
    private static final byte ACTION_SPELL_RELEASE = 3;

    // attack (2.0s) and shoot (1.3s) durations in ticks
    private static final int ATTACK_DURATION_TICKS        = 40;
    private static final int SPELL_RELEASE_DURATION_TICKS = 26;

    public final AnimationState spellCastAnimationState    = new AnimationState();
    public final AnimationState spellReleaseAnimationState = new AnimationState();

    private int actionEndAge;

    public FrostElementalEntity(EntityType<? extends FrostElementalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ANIMATION_ACTION, ACTION_NONE);
    }

    // Runs on both sides when tracked data arrives from the server.
    // Starts and stops the appropriate AnimationState so the model reacts immediately.
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
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

    @Override
    protected void onAttackAnimated() {
        getDataTracker().set(ANIMATION_ACTION, ACTION_MELEE);
        actionEndAge = age + ATTACK_DURATION_TICKS;
    }

    @Override
    protected void onSpellCastStarted() {
        getDataTracker().set(ANIMATION_ACTION, ACTION_SPELL_CAST);
    }

    @Override
    protected void onSpellReleased() {
        getDataTracker().set(ANIMATION_ACTION, ACTION_SPELL_RELEASE);
        actionEndAge = age + SPELL_RELEASE_DURATION_TICKS;
    }

    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient()) {
            byte action = getDataTracker().get(ANIMATION_ACTION);
            if ((action == ACTION_MELEE || action == ACTION_SPELL_RELEASE) && age >= actionEndAge) {
                getDataTracker().set(ANIMATION_ACTION, ACTION_NONE);
            } else if (action == ACTION_SPELL_CAST && !isAttacking()) {
                // SpellCastGoal calls setAttacking(false) when it stops; clear the cast state.
                getDataTracker().set(ANIMATION_ACTION, ACTION_NONE);
            }
        }
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
}
