package net.wizards.effect;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.spell_power.api.statuseffects.SpellVulnerabilityStatusEffect;

public class FrozenStatusEffect extends SpellVulnerabilityStatusEffect {
    public FrozenStatusEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }
}
