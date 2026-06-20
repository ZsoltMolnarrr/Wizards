package net.wizards.entity;

import net.minecraft.entity.EntityType;
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

    public static void register() {
        FrostElementalEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                FrostElementalEntity.ID,
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
                        .build()
        );

        ArcaneEmitterEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                ArcaneEmitterEntity.ID,
                EntityType.Builder.<ArcaneEmitterEntity>create(ArcaneEmitterEntity::new, SpawnGroup.MISC)
                        // was fixed(); vanilla builder only yields `changing`, which is equivalent
                        // here since this entity carries no GENERIC_SCALE attribute.
                        .dimensions(0.6F, 0.6F)
                        .maxTrackingRange(64)
                        .trackingTickInterval(3)
                        .build()
        );

        FireHydraEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                FireHydraEntity.ID,
                EntityType.Builder.<FireHydraEntity>create(FireHydraEntity::new, SpawnGroup.MISC)
                        // was fixed(); vanilla builder only yields `changing`, which is equivalent
                        // here since this entity carries no GENERIC_SCALE attribute.
                        .dimensions(1.5F, 3.0F)
                        .maxTrackingRange(64)
                        .trackingTickInterval(3)
                        .build()
        );
    }
}
