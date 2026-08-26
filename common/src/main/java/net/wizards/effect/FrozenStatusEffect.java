package net.wizards.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.spell_power.api.statuseffects.SpellVulnerabilityStatusEffect;

public class FrozenStatusEffect extends SpellVulnerabilityStatusEffect {
    public FrozenStatusEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }
}
