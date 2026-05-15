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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.spell_engine.utils.TargetHelper;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.internals.SpellHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

        return c;
    }

    public static final Identifier summon_frost_elemental = Identifier.of("wizards", "summon_frost_elemental");
    public static final Identifier summon_arcane_emitter = Identifier.of("wizards", "summon_arcane_emitter");

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

        ArcaneEmitterEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                ArcaneEmitterEntity.ID,
                FabricEntityTypeBuilder.<ArcaneEmitterEntity>create(SpawnGroup.MISC, ArcaneEmitterEntity::new)
                        .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(3)
                        .build()
        );


        SpellHandlers.registerCustomImpact(summon_frost_elemental, new SpellHandlers.CustomImpact() {
            @Override
            public SpellHandlers.ImpactResult onSpellImpact(RegistryEntry<Spell> registryEntry, SpellPower.Result result,
                                                            LivingEntity livingEntity, @Nullable Entity entity,
                                                            SpellHelper.ImpactContext impactContext) {
                var summonBehaviour = new SummonBehaviour();
                summonBehaviour.timeToLive = 60;

                // Movement: follow owner, teleport if too far
                summonBehaviour.movement.follow = new SummonBehaviour.Movement.Follow();
                summonBehaviour.movement.follow.teleport_after_distance = 32;

                // Targeting: mirror owner's attacks and retaliate, but don't auto-aggro
                summonBehaviour.targeting.attack_with_owner = true;
                summonBehaviour.targeting.revenge = true;
                summonBehaviour.targeting.automatic_targeting = true;

                // Actions: frost shard spell (preferred), melee as fallback
                summonBehaviour.actions = List.of(
                    SummonBehaviour.Action.spell("wizards:frost_shard", 60),
                    SummonBehaviour.Action.spell("wizards:frost_nova", 60),
                    SummonBehaviour.Action.attack(3, 1.5F)
                );

                // Attribute scaling: health and attack scale with owner's frost spell power
                var healthEntry = new SummonBehaviour.AttributeScaling.Entry();
                healthEntry.attribute_id = "minecraft:generic.max_health";
                healthEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 2.0));

                var attackEntry = new SummonBehaviour.AttributeScaling.Entry();
                attackEntry.attribute_id = "minecraft:generic.attack_damage";
                attackEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 0.5));

                var spellPowerEntry = new SummonBehaviour.AttributeScaling.Entry();
                spellPowerEntry.attribute_id = SpellSchools.FROST.id.toString();
                spellPowerEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                        SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 1F));

                summonBehaviour.attribute_scaling.entries = List.of(healthEntry, attackEntry, spellPowerEntry);

                var world = livingEntity.getWorld();
                if (world instanceof ServerWorld serverWorld) {
                    var summoned = new FrostElementalEntity(FrostElementalEntity.TYPE, world);
                    summoned.onSummonedBySpell(new SpellSummoned.Args(livingEntity, registryEntry, summonBehaviour, impactContext));
                    Vec3d spawnPos = findSpawnPosition(livingEntity, serverWorld);
                    summoned.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                    serverWorld.spawnEntity(summoned);
                }

                return new SpellHandlers.ImpactResult(true, false);
            }
        });

        SpellHandlers.registerCustomImpact(summon_arcane_emitter, new SpellHandlers.CustomImpact() {
            @Override
            public SpellHandlers.ImpactResult onSpellImpact(RegistryEntry<Spell> registryEntry, SpellPower.Result result,
                                                            LivingEntity livingEntity, @Nullable Entity entity,
                                                            SpellHelper.ImpactContext impactContext) {
                var summonBehaviour = new SummonBehaviour();
                summonBehaviour.timeToLive = 15;
                summonBehaviour.is_attackable = false;

                // Movement: stationary — no follow, no wander
                summonBehaviour.movement.can_move = false;
                summonBehaviour.movement.is_pushable = false;
                summonBehaviour.movement.affected_by_gravity = false;
                summonBehaviour.movement.collision = SummonBehaviour.Movement.CollisionMode.NONE;

                // Bounding box: compact cube
                summonBehaviour.dimensions.width  = 0.6F;
                summonBehaviour.dimensions.height = 0.6F;

                // Targeting: mirror owner's attacks and retaliate, but don't auto-aggro
                summonBehaviour.targeting.attack_with_owner = true;
                summonBehaviour.targeting.revenge = true;
                summonBehaviour.targeting.automatic_targeting = true;
                summonBehaviour.targeting.look_around = false;

                // Actions: arcane bolt only
                summonBehaviour.actions = List.of(
                    SummonBehaviour.Action.spell("wizards:arcane_bolt", 5)
                );

                var spellPowerEntry = new SummonBehaviour.AttributeScaling.Entry();
                spellPowerEntry.attribute_id = SpellSchools.ARCANE.id.toString();
                spellPowerEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                        SpellSchools.ARCANE.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 1F));

                summonBehaviour.attribute_scaling.entries = List.of(spellPowerEntry);

                var world = livingEntity.getWorld();
                if (world instanceof ServerWorld serverWorld) {
                    var summoned = new ArcaneEmitterEntity(ArcaneEmitterEntity.TYPE, world);
                    summoned.onSummonedBySpell(new SpellSummoned.Args(livingEntity, registryEntry, summonBehaviour, impactContext));
                    Vec3d spawnPos = findSpawnPosition(livingEntity, serverWorld);
                    summoned.setPos(spawnPos.x, spawnPos.y + 1.0, spawnPos.z);
                    serverWorld.spawnEntity(summoned);
                }

                return new SpellHandlers.ImpactResult(true, false);
            }
        });
    }

    // Tries N/E/S/W positions 2 blocks away; picks the first that has solid ground and clear LOS
    // to the summoner. Falls back to a ground-snapped position at the summoner's feet, then to the
    // summoner's raw position if no solid ground is found at all.
    private static Vec3d findSpawnPosition(LivingEntity summoner, ServerWorld world) {
        double[][] offsets = { {0, -2}, {2, 0}, {0, 2}, {-2, 0} };
        for (double[] offset : offsets) {
            Vec3d candidate = summoner.getPos().add(offset[0], 0, offset[1]);
            Vec3d grounded = TargetHelper.findSolidBlockBelow(summoner, candidate, world, -5);
            if (grounded == null) continue;
            if (!hasLineOfSight(summoner, grounded, world)) continue;
            return grounded;
        }
        Vec3d selfGrounded = TargetHelper.findSolidBlockBelow(summoner, summoner.getPos(), world, -5);
        return selfGrounded != null ? selfGrounded : summoner.getPos();
    }

    private static boolean hasLineOfSight(LivingEntity summoner, Vec3d target, ServerWorld world) {
        var hit = world.raycast(new RaycastContext(
                summoner.getEyePos(), target,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                summoner));
        return hit.getType() != HitResult.Type.BLOCK;
    }
}
