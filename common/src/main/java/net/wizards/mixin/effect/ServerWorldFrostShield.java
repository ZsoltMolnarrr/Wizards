package net.wizards.mixin.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.wizards.effect.FrostShielded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Suppresses the vanilla "took damage" feedback (hurt sound + red flash) for hits a frost
/// shield swallowed whole. On 1.21.1 this cancelled the `BLOCK_WITH_SHIELD` entity status;
/// since 1.21.11 the shield-block sound comes from the blocking item's `BLOCKS_ATTACKS`
/// component and a shieldless full block falls through to `sendEntityDamage`, so that is
/// what has to be cancelled instead. The frost impact sound is played by
/// {@link LivingEntityFrostShield}.
@Mixin(ServerLevel.class)
public class ServerWorldFrostShield {
    @Inject(method = "broadcastDamageEvent", at = @At("HEAD"), cancellable = true)
    private void sendEntityDamage_HEAD_FrostShield(Entity entity, DamageSource damageSource, CallbackInfo ci) {
        if (entity instanceof FrostShielded shielded
                && shielded.hasFrostShield()
                && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            ci.cancel();
        }
    }
}
