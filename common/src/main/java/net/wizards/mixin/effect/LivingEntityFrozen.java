package net.wizards.mixin.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.wizards.effect.WizardsEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFrozen {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Inject(method = "baseTick", at = @At("TAIL"))
    public void baseTick_TAIL_FrozenByStatusEffect(CallbackInfo ci) {
        var entity = (LivingEntity) ((Object)this);
        entity.isInPowderSnow = entity.isInPowderSnow || hasEffect(WizardsEffects.frozen.entry);
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void jump_HEAD_NoJumpingWhileFrozen(CallbackInfo ci) {
        if (hasEffect(WizardsEffects.frozen.entry)) {
            ci.cancel();
        }
    }
}
