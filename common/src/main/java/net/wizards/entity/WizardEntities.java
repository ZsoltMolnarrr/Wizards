package net.wizards.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.summon.SummonedEntities;
import net.spell_engine.api.spell.summon.SummonedEntityConfig;
import net.spell_power.api.SpellSchools;
import net.tiny_config.ConfigManager;
import net.wizards.WizardsMod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WizardEntities {

    /// Pairs a custom entity's type with its display name (for lang datagen) and, optionally, its
    /// summoned-entity attribute defaults. Mirrors the {@code Effects.Entry} pattern: the name lives
    /// next to the registration so it can't drift or be forgotten.
    public static class Entry<T extends Entity> {
        public final Identifier id;
        /// English display name, emitted as {@code entity.<namespace>.<path>} by lang datagen.
        public final String name;
        public final EntityType<T> type;
        /// Attribute defaults for summoned entities (seeded into Wizards' own config/wizards/summoned_entities.json).
        /// Null for entities that aren't spell-power-scaled summons.
        @Nullable public final SummonedEntityConfig.Entry summonConfig;

        public Entry(Identifier id, String name, EntityType<T> type) {
            this(id, name, type, null);
        }
        public Entry(Identifier id, String name, EntityType<T> type, @Nullable SummonedEntityConfig.Entry summonConfig) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.summonConfig = summonConfig;
        }
    }

    public static final List<Entry<?>> entries = new ArrayList<>();
    private static <T extends Entity> Entry<T> add(Entry<T> entry) {
        entries.add(entry);
        return entry;
    }

    public static final Entry<FrostElementalEntity> FROST_ELEMENTAL = add(new Entry<>(
            Identifier.of(WizardsMod.ID, "frost_elemental"),
            "Frost Elemental",
            EntityType.Builder.<FrostElementalEntity>create(FrostElementalEntity::new, SpawnGroup.MISC)
                    // dimensions(float, float) yields `changing` (fixed=false) so
                    // EntityDimensions.scaled() actually applies the GENERIC_SCALE attribute
                    // when getBaseDimensions falls through to the type (i.e., when
                    // behaviour.dimensions is null). With `fixed`, scaled() is a no-op and
                    // getWidth()/getHeight() stay locked at base size — which silently shrinks
                    // the melee reach below the visible model size.
                    .dimensions(1F, 2F)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
                    // Vanilla build(RegistryKey) — the no-arg build() is a Fabric API interface-injected
                    // default (FabricEntityType.Builder) that does not exist on NeoForge at runtime.
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(WizardsMod.ID, "frost_elemental"))),
            frostDefaults()));

    public static final Entry<ArcaneEmitterEntity> ARCANE_EMITTER = add(new Entry<>(
            Identifier.of(WizardsMod.ID, "arcane_emitter"),
            "Arcane Emitter",
            EntityType.Builder.<ArcaneEmitterEntity>create(ArcaneEmitterEntity::new, SpawnGroup.MISC)
                    // was fixed(); vanilla builder only yields `changing`, which is equivalent
                    // here since this entity carries no GENERIC_SCALE attribute.
                    .dimensions(0.6F, 0.6F)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(WizardsMod.ID, "arcane_emitter"))),
            arcaneDefaults()));

    public static final Entry<FireHydraEntity> FIRE_HYDRA = add(new Entry<>(
            Identifier.of(WizardsMod.ID, "fire_hydra"),
            "Fire Hydra",
            EntityType.Builder.<FireHydraEntity>create(FireHydraEntity::new, SpawnGroup.MISC)
                    // was fixed(); vanilla builder only yields `changing`, which is equivalent
                    // here since this entity carries no GENERIC_SCALE attribute.
                    .dimensions(1.5F, 3.0F)
                    .maxTrackingRange(64)
                    .trackingTickInterval(3)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(WizardsMod.ID, "fire_hydra"))),
            fireDefaults()));

    // Default base attributes per summon — seeded into Wizards' OWN config file
    // (config/wizards/summoned_entities.json), which Wizards versions independently of SpellEngine and
    // the other class mods. The live values are read back through summonConfig at registration time.

    public static SummonedEntityConfig.Entry frostDefaults() {
        var e = new SummonedEntityConfig.Entry();
        e.common = new SummonedEntityConfig.CommonAttributes(30, 0.25, 4);
        e.common.follow_range = 32;
        e.custom.add(new SummonedEntityConfig.CustomAttribute(SpellSchools.FROST.id.toString(), 1));
        return e;
    }

    public static SummonedEntityConfig.Entry arcaneDefaults() {
        var e = new SummonedEntityConfig.Entry();
        e.common = new SummonedEntityConfig.CommonAttributes(20, 0.3, 3);
        e.common.follow_range = 32;
        e.custom.add(new SummonedEntityConfig.CustomAttribute(SpellSchools.ARCANE.id.toString(), 1));
        return e;
    }

    public static SummonedEntityConfig.Entry fireDefaults() {
        var e = new SummonedEntityConfig.Entry();
        e.common = new SummonedEntityConfig.CommonAttributes(30, 0.0, 4);
        e.common.follow_range = 32;
        e.custom.add(new SummonedEntityConfig.CustomAttribute(SpellSchools.FIRE.id.toString(), 1));
        return e;
    }

    /// Wizards' own summoned-entity config file, seeded from the per-entity defaults above and versioned
    /// independently (bump `schemaVersion` to reset users' files after a defaults change). Declared after
    /// the entity constants so {@link #entries} is fully populated when the defaults are collected.
    public static final ConfigManager<SummonedEntityConfig> summonConfig = new ConfigManager<>
            ("summoned_entities", seededDefaults())
            .builder()
            .setDirectory(WizardsMod.ID)
            .schemaVersion(1)
            .sanitize(true)
            .build();

    private static SummonedEntityConfig seededDefaults() {
        var config = new SummonedEntityConfig();
        for (var entry : entries) {
            if (entry.summonConfig != null) {
                config.entries.put(entry.id.toString(), entry.summonConfig);
            }
        }
        return config;
    }

    public static void register() {
        summonConfig.refresh(); // load (or write) Wizards' own config file before reading values from it
        for (var entry : entries) {
            // Attributes are registered right here with the freshly-built type, so type and attribute
            // registration are a single co-located step — no required ordering between them.
            Registry.register(Registries.ENTITY_TYPE, entry.id, entry.type);
            if (entry.summonConfig != null) {
                // Only summoned (living) entities carry a config; safe by construction.
                @SuppressWarnings("unchecked")
                var livingType = (EntityType<? extends LivingEntity>) entry.type;
                // Inject Wizards' config as the attribute source — a plain Function<Identifier, Entry>.
                SummonedEntities.registerAttributes(entry.id, livingType, summonConfig.value::entryFor);
            }
        }
    }
}
