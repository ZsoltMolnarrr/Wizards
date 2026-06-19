package net.wizards.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.wizards.content.WizardsSounds;
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


        SpellHandlers.registerCustomImpact(summon_frost_elemental, new SpellHandlers.CustomImpact() {
            @Override
            public SpellHandlers.ImpactResult onSpellImpact(RegistryEntry<Spell> registryEntry, SpellPower.Result result,
                                                            LivingEntity livingEntity, @Nullable Entity entity,
                                                            SpellHelper.ImpactContext impactContext) {
                var summonBehaviour = new SummonBehaviour();
                summonBehaviour.lifespan.active_seconds = 30;
                summonBehaviour.lifespan.spawn_ticks = 20;
                summonBehaviour.lifespan.despawn_ticks = 20;

                // Movement: follow owner, teleport if too far
                summonBehaviour.movement.follow = new SummonBehaviour.Movement.Follow();
                summonBehaviour.movement.follow.teleport_after_distance = 32;
                summonBehaviour.movement.collision = SummonBehaviour.Movement.CollisionMode.ENEMIES;

                // Targeting: mirror owner's attacks and retaliate, but don't auto-aggro
                summonBehaviour.targeting.attack_with_owner = true;
                summonBehaviour.targeting.revenge = true;
                summonBehaviour.targeting.automatic_targeting = SummonBehaviour.Targeting.AutoTarget.HOSTILE;

                // Actions: frost shard spell (preferred), melee as fallback
                var attack = new SummonBehaviour.Action.MeleeAttack();
                attack.speed = 2F;
                attack.radius = 1F;
                attack.swing_sound  = WizardsSounds.FROST_ELEMENTAL_ATTACK.id().toString();
                attack.impact_sound = WizardsSounds.FROST_ELEMENTAL_IMPACT.id().toString();
                attack.windup = 0.4F;
                attack.animation_variants = List.of(1, 2); // alternates between `attack` and `attack_2`

                var frostNova = new SummonBehaviour.Action.SpellCast();
                frostNova.spell_id = "wizards:frost_nova";
                frostNova.range.max = 1.5F;
                frostNova.cooldown = 60;
                frostNova.release_animation_variants = List.of(2);
                summonBehaviour.actions = List.of(
                    SummonBehaviour.Action.spell("wizards:frost_shard", 20),
                    SummonBehaviour.Action.spell(frostNova),
                    SummonBehaviour.Action.attack(attack)
                );

                // Lifecycle sounds
                summonBehaviour.sounds.spawn   = WizardsSounds.FROST_ELEMENTAL_SPAWN.id().toString();
                summonBehaviour.sounds.despawn = WizardsSounds.FROST_ELEMENTAL_DESPAWN.id().toString();
                summonBehaviour.sounds.hurt    = WizardsSounds.FROST_ELEMENTAL_HURT.id().toString();
                summonBehaviour.sounds.death   = WizardsSounds.FROST_ELEMENTAL_DEATH.id().toString();
                summonBehaviour.sounds.ambient = WizardsSounds.FROST_ELEMENTAL_IDLE.id().toString();
                summonBehaviour.sounds.step    = WizardsSounds.FROST_ELEMENTAL_STEP.id().toString();

                // Attribute scaling: health and attack scale with owner's frost spell power
                var healthEntry = new SummonBehaviour.AttributeScaling.Entry();
                healthEntry.attribute_id = EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString();
                healthEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 2.0));

                // Armor
                var armorEntry = new SummonBehaviour.AttributeScaling.Entry();
                armorEntry.attribute_id = EntityAttributes.GENERIC_ARMOR.getIdAsString();
                armorEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 10, 0.1));

                var attackEntry = new SummonBehaviour.AttributeScaling.Entry();
                attackEntry.attribute_id = EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString();
                attackEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 0.5));

                var knockbackEntry = new SummonBehaviour.AttributeScaling.Entry();
                knockbackEntry.attribute_id = EntityAttributes.GENERIC_ATTACK_KNOCKBACK.getIdAsString();
                knockbackEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 0.1));

                var knockbackResistEntry = new SummonBehaviour.AttributeScaling.Entry();
                knockbackResistEntry.attribute_id = EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getIdAsString();
                knockbackResistEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 5,0.05));

                var spellPowerEntry = new SummonBehaviour.AttributeScaling.Entry();
                spellPowerEntry.attribute_id = SpellSchools.FROST.id.toString();
                spellPowerEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                        SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 3, 0.1F));

                // SCALE
                var scaleEntry = new SummonBehaviour.AttributeScaling.Entry();
                scaleEntry.attribute_id = EntityAttributes.GENERIC_SCALE.getIdAsString();
                scaleEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                        SpellSchools.FROST.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 0.05F));

                summonBehaviour.attribute_scaling.entries = List.of(healthEntry, armorEntry, attackEntry, spellPowerEntry, knockbackEntry, knockbackResistEntry, scaleEntry);

                var world = livingEntity.getWorld();
                if (world instanceof ServerWorld serverWorld) {
                    var summoned = new FrostElementalEntity(FrostElementalEntity.TYPE, world);
                    summoned.onSummonedBySpell(new SpellSummoned.Args(livingEntity, registryEntry, summonBehaviour, impactContext));
                    Vec3d spawnPos = findSpawnPosition(livingEntity, serverWorld);
                    // setPosition (not setPos) so the bounding box moves with the entity:
                    // plain setPos leaves the box at the origin for summons that never move,
                    // breaking box-based world queries (target search, collision).
                    summoned.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
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
                summonBehaviour.lifespan.active_seconds = 15;
                summonBehaviour.is_attackable = false;

                // Movement: stationary — no follow, no wander
                summonBehaviour.movement.can_move = false;
                summonBehaviour.movement.is_pushable = false;
                summonBehaviour.movement.affected_by_gravity = false;
                summonBehaviour.movement.collision = SummonBehaviour.Movement.CollisionMode.NONE;

                // Bounding box: compact cube. `dimensions` is an optional override and
                // defaults to null, so instantiate it before assigning.
                summonBehaviour.dimensions = new SummonBehaviour.Dimensions();
                summonBehaviour.dimensions.width  = 0.6F;
                summonBehaviour.dimensions.height = 0.6F;

                // Targeting: pure turret — never acquire or track a target, so the emitter
                // holds the spawn-set facing for its whole lifespan.
                summonBehaviour.targeting.attack_with_owner = true;
                summonBehaviour.targeting.revenge = false;
                summonBehaviour.targeting.automatic_targeting = SummonBehaviour.Targeting.AutoTarget.NONE;
                summonBehaviour.targeting.look_around = false;

                // Actions: arcane bolt fired straight ahead along the emitter's facing, on
                // cooldown — pure turret, never acquires a target.
                var arcaneBolt = new SummonBehaviour.Action.SpellCast("wizards:arcane_bolt", 5);
                arcaneBolt.aiming.accept_target = true;
                arcaneBolt.aiming.fallback = SummonBehaviour.Action.SpellCast.Aiming.Fallback.FORWARD;
                summonBehaviour.actions = List.of(
                    SummonBehaviour.Action.spell(arcaneBolt)
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
                    // setPosition (not setPos) so the bounding box moves with the entity:
                    // plain setPos leaves the box at the origin for summons that never move,
                    // breaking box-based world queries (target search, collision).
                    summoned.setPosition(spawnPos.x, spawnPos.y + 1.0, spawnPos.z);
                    // Aim the turret down the summoner's look direction — the FORWARD-fallback
                    // cast fires along this facing for the emitter's whole lifespan.
                    summoned.setYaw(livingEntity.getYaw());
                    summoned.setBodyYaw(livingEntity.getYaw());
                    summoned.setHeadYaw(livingEntity.getYaw());
                    summoned.setPitch(livingEntity.getPitch());
                    serverWorld.spawnEntity(summoned);
                }

                return new SpellHandlers.ImpactResult(true, false);
            }
        });

        SpellHandlers.registerCustomImpact(summon_fire_hydra, new SpellHandlers.CustomImpact() {
            @Override
            public SpellHandlers.ImpactResult onSpellImpact(RegistryEntry<Spell> registryEntry, SpellPower.Result result,
                                                            LivingEntity livingEntity, @Nullable Entity entity,
                                                            SpellHelper.ImpactContext impactContext) {
                var summonBehaviour = new SummonBehaviour();
                summonBehaviour.lifespan.active_seconds = 30;
                summonBehaviour.lifespan.spawn_ticks = 20;
                summonBehaviour.lifespan.despawn_ticks = 20;

                // Not attackable
                summonBehaviour.is_attackable = false;

                summonBehaviour.targeting.detection_range.mode = SummonBehaviour.Targeting.DetectionRange.Mode.MAXIMUM_ACTION_RANGE;

                // Movement: stationary — anchored to the spawn position, no collision
                summonBehaviour.movement.can_move = false;
                summonBehaviour.movement.is_pushable = false;
                summonBehaviour.movement.affected_by_gravity = false;
                summonBehaviour.movement.collision = SummonBehaviour.Movement.CollisionMode.NONE;

                // Targeting: mirror owner's attacks and retaliate, auto-aggro hostiles
                summonBehaviour.targeting.attack_with_owner = true;
                summonBehaviour.targeting.revenge = true;
                summonBehaviour.targeting.automatic_targeting = SummonBehaviour.Targeting.AutoTarget.HOSTILE;

                // Actions: fireball (preferred), melee bite when close
                var melee = new SummonBehaviour.Action.MeleeAttack();
                melee.max_range = 4F;
                melee.speed = 1F;
                melee.windup = 0.4F;
                melee.radius = 0.5F;
                melee.animation_variants = List.of(1);

                summonBehaviour.actions = List.of(
                    SummonBehaviour.Action.spell("wizards:fireball", 20),
                    SummonBehaviour.Action.attack(melee)
                );

                // Attribute scaling: scale with owner's fire spell power
                var healthEntry = new SummonBehaviour.AttributeScaling.Entry();
                healthEntry.attribute_id = EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString();
                healthEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FIRE.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 2.0));

                var armorEntry = new SummonBehaviour.AttributeScaling.Entry();
                armorEntry.attribute_id = EntityAttributes.GENERIC_ARMOR.getIdAsString();
                armorEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FIRE.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 10, 0.1));

                var attackEntry = new SummonBehaviour.AttributeScaling.Entry();
                attackEntry.attribute_id = EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString();
                attackEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FIRE.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 0.5));

                var knockbackEntry = new SummonBehaviour.AttributeScaling.Entry();
                knockbackEntry.attribute_id = EntityAttributes.GENERIC_ATTACK_KNOCKBACK.getIdAsString();
                knockbackEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FIRE.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 0.1));

                var knockbackResistEntry = new SummonBehaviour.AttributeScaling.Entry();
                knockbackResistEntry.attribute_id = EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getIdAsString();
                knockbackResistEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                    SpellSchools.FIRE.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 5, 0.05));

                var spellPowerEntry = new SummonBehaviour.AttributeScaling.Entry();
                spellPowerEntry.attribute_id = SpellSchools.FIRE.id.toString();
                spellPowerEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                        SpellSchools.FIRE.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 3, 0.1F));

//                var scaleEntry = new SummonBehaviour.AttributeScaling.Entry();
//                scaleEntry.attribute_id = EntityAttributes.GENERIC_SCALE.getIdAsString();
//                scaleEntry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
//                        SpellSchools.FIRE.id.toString(), EntityAttributeModifier.Operation.ADD_VALUE, 0.05F));

                summonBehaviour.attribute_scaling.entries = List.of(healthEntry, armorEntry, attackEntry, spellPowerEntry, knockbackEntry, knockbackResistEntry);

                var world = livingEntity.getWorld();
                if (world instanceof ServerWorld serverWorld) {
                    var summoned = new FireHydraEntity(FireHydraEntity.TYPE, world);
                    summoned.onSummonedBySpell(new SpellSummoned.Args(livingEntity, registryEntry, summonBehaviour, impactContext));
                    Vec3d spawnPos = findSpawnPosition(livingEntity, serverWorld);
                    // setPosition (not setPos) so the bounding box moves with the entity:
                    // plain setPos leaves the box at the origin for summons that never move,
                    // breaking box-based world queries (target search, collision).
                    summoned.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
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
