package net.wizards.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

import java.util.ArrayList;
import java.util.List;

public class WizardsSounds {
    public record Entry(Identifier id, SoundEvent soundEvent, RegistryEntry<SoundEvent> entry) {
        public Entry(String name) {
            this(Identifier.of(WizardsMod.ID, name));
        }
        public Entry(Identifier id) {
            this(id, SoundEvent.of(id));
        }
        public Entry travelDistance(float distance) {
            return new Entry(id, SoundEvent.of(id, distance));
        }
        public Entry(Identifier id, SoundEvent soundEvent) {
            this(id, soundEvent, Registry.registerReference(Registries.SOUND_EVENT, id, soundEvent));
        }
    }
    public static final List<Entry> entries = new ArrayList<>();
    public static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Entry ARCANE_MISSILE_RELEASE = add(new Entry("arcane_missile_release"));
    public static final Entry ARCANE_MISSILE_IMPACT = add(new Entry("arcane_missile_impact"));
    public static final Entry ARCANE_SHOOT_SMALL = add(new Entry("arcane_shoot_small"));
    public static final Entry ARCANE_BLAST_RELEASE = add(new Entry("arcane_blast_release"));
    public static final Entry ARCANE_BLAST_IMPACT = add(new Entry("arcane_blast_impact"));
    public static final Entry ARCANE_BEAM_START = add(new Entry("arcane_beam_start"));
    public static final Entry ARCANE_BEAM_CASTING = add(new Entry("arcane_beam_casting"));
    public static final Entry ARCANE_BEAM_IMPACT = add(new Entry("arcane_beam_impact"));
    public static final Entry ARCANE_BEAM_RELEASE = add(new Entry("arcane_beam_release"));

    public static final Entry FIRE_SCORCH_IMPACT = add(new Entry("fire_scorch_impact"));
    public static final Entry FIREBALL_IMPACT = add(new Entry("fireball_impact"));
    public static final Entry FIRE_BREATH_START = add(new Entry("fire_breath_start"));
    public static final Entry FIRE_BREATH_CASTING = add(new Entry("fire_breath_casting"));
    public static final Entry FIRE_BREATH_RELEASE = add(new Entry("fire_breath_release"));
    public static final Entry FIRE_BREATH_IMPACT = add(new Entry("fire_breath_impact"));
    public static final Entry FIRE_METEOR_RELEASE = add(new Entry("fire_meteor_release").travelDistance(48F));
    public static final Entry FIRE_METEOR_IMPACT = add(new Entry("fire_meteor_impact"));
    public static final Entry FIRE_WALL_IGNITE = add(new Entry("fire_wall_ignite"));

    public static final Entry FROST_SHARD_IMPACT = add(new Entry("frost_shard_impact"));
    public static final Entry FROST_NOVA_RELEASE = add(new Entry("frost_nova_release"));
    public static final Entry FROST_NOVA_DAMAGE_IMPACT = add(new Entry("frost_nova_damage_impact"));
    public static final Entry FROST_NOVA_EFFECT_IMPACT = add(new Entry("frost_nova_effect_impact"));
    public static final Entry FROST_SHIELD_RELEASE = add(new Entry("frost_shield_release"));
    public static final Entry FROST_SHIELD_IMPACT = add(new Entry("frost_shield_impact"));
    public static final Entry FROST_BLIZZARD_CASTING = add(new Entry("frost_blizzard_casting"));

    public static final Entry WIZARD_ROBES_EQUIP = new Entry("wizard_robes_equip");

    public static void register() {
        for (var entry: entries) {
            Registry.register(Registries.SOUND_EVENT, entry.id(), entry.soundEvent());
        }
    }
}
