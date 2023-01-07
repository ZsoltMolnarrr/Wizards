package net.wizards.util;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.wizards.WizardsMod;

import java.util.List;
import java.util.Map;

public class SoundHelper {
    public static List<String> soundKeys = List.of(
            "arcane_missile_release",
            "arcane_missile_impact",
            "arcane_blast_release",
            "arcane_blast_impact",
            "arcane_beam_start",
            "arcane_beam_casting",
            "arcane_beam_impact",
            "arcane_beam_release",

            "fireball_impact",
            "fire_breath_start",
            "fire_breath_casting",
            "fire_breath_release",
            "fire_breath_impact",
            "fire_meteor_release",
            "fire_meteor_impact",

            "frost_nova_release",
            "frost_nova_damage_impact",
            "frost_nova_effect_impact",
            "frost_shield_release"
    );

    public static Map<String, Float> soundDistances = Map.of(
            "fire_meteor_impact", Float.valueOf(48F)
    );

    public static void registerSounds() {
        for (var soundKey: soundKeys) {
            var soundId = new Identifier(WizardsMod.ID, soundKey);
            var customTravelDistance = soundDistances.get(soundKey);
            var soundEvent = (customTravelDistance == null)
                    ? new SoundEvent(soundId)
                    : new SoundEvent(soundId, customTravelDistance);
            Registry.register(Registry.SOUND_EVENT, soundId, soundEvent);
        }

        // Registry.register(Registry.SOUND_EVENT, FrostShieldStatusEffect.soundId, FrostShieldStatusEffect.sound);
    }
}
