package net.wizards.client.entity;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;
import net.spell_engine.entity.SummonedEntity;

/// Render state for Spell Engine summons (1.21.2+ split rendering: the entity is read on the
/// tick thread into this snapshot, the model only ever sees the snapshot).
///
/// Mirrors the animation surface of {@link SummonedEntity}: the seven animation states, the
/// action variants, and the two playback-speed multipliers. The speeds are resolved by the
/// renderer, because {@code SummonedEntity#getAttackAnimationSpeed} needs the length of the
/// clip the model picked for that variant.
public class SummonedEntityRenderState extends LivingEntityRenderState {
    public final AnimationState spawnAnimationState = new AnimationState();
    public final AnimationState despawnAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState moveAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState spellCastAnimationState = new AnimationState();
    public final AnimationState spellReleaseAnimationState = new AnimationState();

    public int attackVariant = 1;
    public int spellReleaseVariant = 1;
    public float attackAnimationSpeed = 1F;
    public float spellReleaseAnimationSpeed = 1F;

    /// Copies everything the models read. Called from each renderer's `updateRenderState`.
    public void copyFrom(SummonedEntity entity) {
        this.spawnAnimationState.copyFrom(entity.spawnAnimationState);
        this.despawnAnimationState.copyFrom(entity.despawnAnimationState);
        this.idleAnimationState.copyFrom(entity.idleAnimationState);
        this.moveAnimationState.copyFrom(entity.moveAnimationState);
        this.attackAnimationState.copyFrom(entity.attackAnimationState);
        this.spellCastAnimationState.copyFrom(entity.spellCastAnimationState);
        this.spellReleaseAnimationState.copyFrom(entity.spellReleaseAnimationState);
        this.attackVariant = entity.getAttackVariant();
        this.spellReleaseVariant = entity.getSpellReleaseVariant();
    }
}
