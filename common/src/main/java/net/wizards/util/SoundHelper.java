package net.wizards.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.wizards.WizardsMod;
import net.wizards.effect.FrostShieldStatusEffect;
import net.wizards.item.WizardArmor;

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
                    ? SoundEvent.of(soundId)
                    : SoundEvent.of(soundId, customTravelDistance);
            Registry.register(Registries.SOUND_EVENT, soundId, soundEvent);
        }

        Registry.register(Registries.SOUND_EVENT, FrostShieldStatusEffect.soundId, FrostShieldStatusEffect.sound);
        Registry.register(Registries.SOUND_EVENT, WizardArmor.equipSoundId, WizardArmor.equipSound);
    }

    public static void playSoundEvent(World world, Entity entity, SoundEvent soundEvent) {
        playSoundEvent(world, entity, soundEvent, 1, 1);
    }

    public static void playSoundEvent(World world, Entity entity, SoundEvent soundEvent, float volume, float pitch) {
        world.playSound(
                (PlayerEntity)null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                soundEvent,
                SoundCategory.PLAYERS,
                volume,
                pitch);
    }
}
