package net.wizards.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

public class FrostShieldStatusEffect extends StatusEffect {
    public FrostShieldStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public static final Identifier soundId = new Identifier(WizardsMod.ID, "frost_shield_impact");
    public static final SoundEvent sound = SoundEvent.of(soundId);
}
