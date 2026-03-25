package net.wizards.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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

import java.util.List;

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
                summonBehaviour.timeToLive = 60;

                // Movement: follow owner, teleport if too far
                summonBehaviour.movement.follow = new SummonBehaviour.Movement.Follow();

                // Targeting: mirror owner's attacks and retaliate, but don't auto-aggro
                summonBehaviour.targeting.attack_with_owner = true;
                summonBehaviour.targeting.revenge = true;
                summonBehaviour.targeting.automatic_targeting = false;

                // Actions: standard melee (chases target)
                summonBehaviour.actions = List.of(
                    SummonBehaviour.Action.attack(0, 1.5F)
                );

                // Attribute scaling: health and attack scale with owner's frost spell power
                var healthEntry = new SummonBehaviour.AttributeScaling.Entry();
                healthEntry.attribute_id = "minecraft:generic.max_health";
                healthEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    "spell_power:power.frost", EntityAttributeModifier.Operation.ADD_VALUE, 2.0));

                var attackEntry = new SummonBehaviour.AttributeScaling.Entry();
                attackEntry.attribute_id = "minecraft:generic.attack_damage";
                attackEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    "spell_power:power.frost", EntityAttributeModifier.Operation.ADD_VALUE, 0.5));

                summonBehaviour.attribute_scaling.entries = List.of(healthEntry, attackEntry);
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
