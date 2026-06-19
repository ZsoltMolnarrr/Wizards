package net.wizards.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spell_power.api.SpellSchools;

public class WizardEntities {
    public static EntityConfig defaultEntityConfig() {
        var c = new EntityConfig();

        var frostEntry = new EntityConfig.Entry();
        frostEntry.common = new EntityConfig.CommonAttributes(30, 0.25, 4);
        frostEntry.common.follow_range = 32;
        frostEntry.custom.add(new EntityConfig.CustomAttribute(SpellSchools.FROST.id.toString(), 1));
        c.entries.put(FrostElementalEntity.ID.getPath(), frostEntry);

        var arcaneEntry = new EntityConfig.Entry();
        arcaneEntry.common = new EntityConfig.CommonAttributes(20, 0.3, 3);
        arcaneEntry.common.follow_range = 32;
        arcaneEntry.custom.add(new EntityConfig.CustomAttribute(SpellSchools.ARCANE.id.toString(), 1));
        c.entries.put(ArcaneEmitterEntity.ID.getPath(), arcaneEntry);

        var fireHydraEntry = new EntityConfig.Entry();
        fireHydraEntry.common = new EntityConfig.CommonAttributes(30, 0.0, 4);
        fireHydraEntry.common.follow_range = 32;
        fireHydraEntry.custom.add(new EntityConfig.CustomAttribute(SpellSchools.FIRE.id.toString(), 1));
        c.entries.put(FireHydraEntity.ID.getPath(), fireHydraEntry);

        return c;
    }

    public static final Identifier summon_frost_elemental = Identifier.of("wizards", "summon_frost_elemental");
    public static final Identifier summon_arcane_emitter = Identifier.of("wizards", "summon_arcane_emitter");
    public static final Identifier summon_fire_hydra = Identifier.of("wizards", "summon_fire_hydra");

    public static void register() {
        FrostElementalEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                FrostElementalEntity.ID,
                FabricEntityTypeBuilder.<FrostElementalEntity>create(SpawnGroup.MISC, FrostElementalEntity::new)
                        // `changing` (fixed=false) so EntityDimensions.scaled() actually applies
                        // the GENERIC_SCALE attribute when getBaseDimensions falls through to the
                        // type (i.e., when behaviour.dimensions is null). With `fixed`, scaled()
                        // is a no-op and getWidth()/getHeight() stay locked at base size — which
                        // silently shrinks the melee reach below the visible model size.
                        .dimensions(EntityDimensions.changing(1F, 2F))
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(3)
                        .build()
        );

        ArcaneEmitterEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                ArcaneEmitterEntity.ID,
                FabricEntityTypeBuilder.<ArcaneEmitterEntity>create(SpawnGroup.MISC, ArcaneEmitterEntity::new)
                        .dimensions(EntityDimensions.fixed(0.6F, 0.6F))
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(3)
                        .build()
        );

        FireHydraEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                FireHydraEntity.ID,
                FabricEntityTypeBuilder.<FireHydraEntity>create(SpawnGroup.MISC, FireHydraEntity::new)
                        .dimensions(EntityDimensions.fixed(1.5F, 3.0F))
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(3)
                        .build()
        );


        WizardSummons.registerHandlers();
    }
}
