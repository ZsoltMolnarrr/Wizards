package net.wizards.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.internals.SpellHelper;
import net.spell_power.api.SpellPower;
import org.jetbrains.annotations.Nullable;

public class WizardEntities {
    public static EntityConfig defaultEntityConfig() {
        var c = new EntityConfig();
        var entry = new EntityConfig.Entry();
        entry.common = new EntityConfig.CommonAttributes(30, 0.25, 4);
        c.entries.put(FrostElementalEntity.ID.getPath(), entry);
        return c;
    }

    public static final Identifier summon = Identifier.of("wizards", "summon");

    public static void register() {
        FrostElementalEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                FrostElementalEntity.ID,
                FabricEntityTypeBuilder.<FrostElementalEntity>create(SpawnGroup.MISC, FrostElementalEntity::new)
                        .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(3)
                        .build()
        );


        SpellHandlers.registerCustomImpact(summon, new SpellHandlers.CustomImpact() {
            @Override
            public SpellHandlers.ImpactResult onSpellImpact(RegistryEntry<Spell> registryEntry, SpellPower.Result result,
                                                            LivingEntity livingEntity, @Nullable Entity entity,
                                                            SpellHelper.ImpactContext impactContext) {
                var summonBehaviour = new SummonBehaviour();
                var world = livingEntity.getWorld();
                if (world instanceof ServerWorld serverWorld) {
                    var summoned = new FrostElementalEntity(FrostElementalEntity.TYPE, livingEntity.getWorld());
                    summoned.onSummonedBySpell(new SpellSummoned.Args(livingEntity, registryEntry, summonBehaviour, impactContext));
                    summoned.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                    serverWorld.spawnEntity(summoned);
                }

                return new SpellHandlers.ImpactResult(true, false);
            }
        });
    }
}
