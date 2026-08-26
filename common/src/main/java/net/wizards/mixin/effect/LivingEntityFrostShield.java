package net.wizards.mixin.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.wizards.content.WizardsSounds;
import net.wizards.effect.WizardsEffects;
import net.wizards.effect.FrostShielded;
import net.wizards.util.SoundHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFrostShield implements FrostShielded {
    private boolean hasFrostShield = false;

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void isBlocking_HEAD_FrostShield(CallbackInfoReturnable<Boolean> cir) {
        if (hasFrostShield) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    /// 1.21.11 replaced the boolean `blockedByShield(DamageSource)` with a float
    /// "how much of this hit was blocked" query driven by the `BLOCKS_ATTACKS` component.
    /// A frost shield has no item behind it, so it answers "all of it" directly, and plays
    /// its own impact sound (vanilla's block sound comes from the blocking item's component,
    /// which is absent here).
    @Inject(method = "applyItemBlocking", at = @At("HEAD"), cancellable = true)
    private void getDamageBlockedAmount_HEAD_FrostShield(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (hasFrostShield && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            var entity = (LivingEntity) ((Object)this);
            SoundHelper.playSoundEvent(entity.level(), entity, WizardsSounds.FROST_SHIELD_IMPACT.soundEvent());
            cir.setReturnValue(amount);
            cir.cancel();
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void damage_HEAD_FrostShieldFireImmunity(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (hasFrostShield && source.is(DamageTypeTags.IS_FIRE)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void baseTick_TAIL_FrostShield(CallbackInfo ci) {
        var entity = (LivingEntity) ((Object)this);
        hasFrostShield = entity.hasEffect(WizardsEffects.frostShield.entry);
        if (hasFrostShield && entity.isOnFire()) {
            entity.clearFire();
        }
    }

    public boolean hasFrostShield() {
        return hasFrostShield;
    }
}
