package net.wizards.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.spell_engine.api.spell.summon.SummonedEntities;
import net.spell_engine.api.spell.summon.SummonedEntityConfig;
import net.spell_power.api.SpellSchools;

public class WizardEntities {

    // Default base attributes per summon — seeded into the central SpellEngine config
    // (config/spell_engine/summoned_entities.json) via SummonedEntities.registerAttributes.

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
        // Attributes are registered right here with the freshly-built type, so type and attribute
        // registration are a single co-located step — no required ordering between them.
        SummonedEntities.registerAttributes(FrostElementalEntity.ID, FrostElementalEntity.TYPE, frostDefaults());

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
        SummonedEntities.registerAttributes(ArcaneEmitterEntity.ID, ArcaneEmitterEntity.TYPE, arcaneDefaults());

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
        SummonedEntities.registerAttributes(FireHydraEntity.ID, FireHydraEntity.TYPE, fireDefaults());
    }
}
