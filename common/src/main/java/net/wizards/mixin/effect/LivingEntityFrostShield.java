package net.wizards.mixin.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.wizards.effect.Effects;
import net.wizards.effect.FrostShielded;
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

    @Inject(method = "blockedByShield", at = @At("HEAD"), cancellable = true)
    private void blockedByShield_HEAD_FrostShield(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (hasFrostShield && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    // Replaced with a workaround mixin for `ServerWorld`
    // Server launch with Wilder Wild was crashing, due to vanilla method signature altered somehow?
//    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"), index = 1)
//    private byte damage_sendEntityStatus_NoSendShieldEvent(byte status) {
//        if (status == EntityStatuses.BLOCK_WITH_SHIELD) {
//            var entity = (LivingEntity) ((Object)this);
//            if (hasFrostShield) {
//                SoundHelper.playSoundEvent(entity.world, entity, FrostShieldStatusEffect.sound);
//                return 0; // `0` is unused, but make sure to check in `EntityStatuses`, when updating
//            }
//        }
//        return status;
//    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage_HEAD_FrostShieldFireImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (hasFrostShield && source.isIn(DamageTypeTags.IS_FIRE)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void baseTick_TAIL_FrostShield(CallbackInfo ci) {
        var entity = (LivingEntity) ((Object)this);
        hasFrostShield = entity.hasStatusEffect(Effects.frostShield);
        if (hasFrostShield && entity.isOnFire()) {
            entity.extinguish();
        }
    }

    public boolean hasFrostShield() {
        return hasFrostShield;
    }
}
