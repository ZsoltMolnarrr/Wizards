package net.wizards.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import net.wizards.content.WizardsSounds;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/// Central definition + spawning of the Wizards summons.
///
/// Each summon is a declarative {@link Summon} (entity id + {@link SummonBehaviour} + placement),
/// keyed by the custom-impact handler id that a spell's `action.custom.handler` references. A single
/// generic handler (see {@link #registerHandlers()}) drives all of them, so adding a summon is a new
/// {@link Summon} entry rather than another bespoke handler.
public class WizardSummons {

    /// Builds the default summon definitions, keyed by handler id. Built fresh on demand; the
    /// returned instances are shared per handler (entities only read the behaviour, never mutate it).
    public static LinkedHashMap<Identifier, Summon> defaultSummons() {
        var summons = new LinkedHashMap<Identifier, Summon>();
        summons.put(WizardEntities.summon_frost_elemental, frostElemental());
        summons.put(WizardEntities.summon_arcane_emitter, arcaneEmitter());
        summons.put(WizardEntities.summon_fire_hydra, fireHydra());
        return summons;
    }

    // MARK: Summon definitions

    private static Summon frostElemental() {
        var b = new SummonBehaviour();
        b.lifespan.active_seconds = 30;
        b.lifespan.spawn_ticks = 20;
        b.lifespan.despawn_ticks = 20;

        // Movement: follow owner, teleport if too far
        b.movement.follow = new SummonBehaviour.Movement.Follow();
        b.movement.follow.teleport_after_distance = 32;
        b.movement.collision = SummonBehaviour.Movement.CollisionMode.ENEMIES;

        // Targeting: mirror owner's attacks and retaliate, auto-aggro hostiles
        b.targeting.attack_with_owner = true;
        b.targeting.revenge = true;
        b.targeting.automatic_targeting = SummonBehaviour.Targeting.AutoTarget.HOSTILE;

        // Actions: frost shard spell (preferred), frost nova up close, melee as fallback
        var attack = new SummonBehaviour.Action.MeleeAttack();
        attack.speed = 2F;
        attack.radius = 1F;
        attack.swing_sound = WizardsSounds.FROST_ELEMENTAL_ATTACK.id().toString();
        attack.impact_sound = WizardsSounds.FROST_ELEMENTAL_IMPACT.id().toString();
        attack.windup = 0.4F;
        attack.animation_variants = List.of(1, 2); // alternates between `attack` and `attack_2`

        var frostNova = new SummonBehaviour.Action.SpellCast();
        frostNova.spell_id = "wizards:frost_nova";
        frostNova.range.max = 1.5F;
        frostNova.cooldown = 60;
        frostNova.release_animation_variants = List.of(2);

        b.actions = List.of(
                SummonBehaviour.Action.spell("wizards:frost_shard", 20),
                SummonBehaviour.Action.spell(frostNova),
                SummonBehaviour.Action.attack(attack)
        );

        // Lifecycle sounds
        b.sounds.spawn = WizardsSounds.FROST_ELEMENTAL_SPAWN.id().toString();
        b.sounds.despawn = WizardsSounds.FROST_ELEMENTAL_DESPAWN.id().toString();
        b.sounds.hurt = WizardsSounds.FROST_ELEMENTAL_HURT.id().toString();
        b.sounds.death = WizardsSounds.FROST_ELEMENTAL_DEATH.id().toString();
        b.sounds.ambient = WizardsSounds.FROST_ELEMENTAL_IDLE.id().toString();
        b.sounds.step = WizardsSounds.FROST_ELEMENTAL_STEP.id().toString();

        // Attribute scaling: standard combat stats + a size bump, all scaling with frost spell power
        var scaling = schoolCombatScaling(SpellSchools.FROST);
        scaling.add(scalingEntry(EntityAttributes.GENERIC_SCALE.getIdAsString(),
                SpellSchools.FROST.id.toString(), 0, 0.05));
        b.attribute_scaling.entries = scaling;

        // Placement: 2 blocks ahead of the caster, snapped to the ground
        var placement = new Spell.EntityPlacement();
        placement.location_offset_by_look = 2;
        placement.force_onto_ground = true;

        return new Summon(FrostElementalEntity.ID.toString(), b, placement);
    }

    private static Summon arcaneEmitter() {
        var b = new SummonBehaviour();
        b.lifespan.active_seconds = 15;
        b.is_attackable = false;

        // Movement: stationary — no follow, no wander, no collision, no gravity
        b.movement.can_move = false;
        b.movement.is_pushable = false;
        b.movement.affected_by_gravity = false;
        b.movement.collision = SummonBehaviour.Movement.CollisionMode.NONE;

        // Bounding box: compact cube.
        b.dimensions = new SummonBehaviour.Dimensions();
        b.dimensions.width = 0.6F;
        b.dimensions.height = 0.6F;

        // Targeting: pure turret — never acquire or track a target, holds spawn-set facing.
        b.targeting.attack_with_owner = true;
        b.targeting.revenge = false;
        b.targeting.automatic_targeting = SummonBehaviour.Targeting.AutoTarget.NONE;
        b.targeting.look_around = false;

        // Actions: arcane bolt fired straight ahead along the emitter's facing, on cooldown.
        var arcaneBolt = new SummonBehaviour.Action.SpellCast("wizards:arcane_bolt", 5);
        arcaneBolt.aiming.accept_target = true;
        arcaneBolt.aiming.fallback = SummonBehaviour.Action.SpellCast.Aiming.Fallback.FORWARD;
        b.actions = List.of(SummonBehaviour.Action.spell(arcaneBolt));

        var spellPower = scalingEntry(SpellSchools.ARCANE.id.toString(),
                SpellSchools.ARCANE.id.toString(), 0, 1.0);
        b.attribute_scaling.entries = List.of(spellPower);

        // Placement: floats ~1 block up, 2 blocks ahead, facing the caster's look direction.
        // apply_yaw/apply_pitch reproduce the old setYaw/setPitch turret aim; the offset is applied
        // once (after the SpellEngine double-offset fix).
        var placement = new Spell.EntityPlacement();
        placement.location_offset_by_look = 2;
        placement.location_offset_y = 1.0F;
        placement.force_onto_ground = false;
        placement.apply_yaw = true;
        placement.apply_pitch = true;

        return new Summon(ArcaneEmitterEntity.ID.toString(), b, placement);
    }

    private static Summon fireHydra() {
        var b = new SummonBehaviour();
        b.lifespan.active_seconds = 30;
        b.lifespan.spawn_ticks = 20;
        b.lifespan.despawn_ticks = 20;
        b.is_attackable = false;

        // Detection scoped to what it can actually act on
        b.targeting.detection_range.mode = SummonBehaviour.Targeting.DetectionRange.Mode.MAXIMUM_ACTION_RANGE;

        // Movement: stationary — anchored, no collision, no gravity
        b.movement.can_move = false;
        b.movement.is_pushable = false;
        b.movement.affected_by_gravity = false;
        b.movement.collision = SummonBehaviour.Movement.CollisionMode.NONE;

        // Targeting: mirror owner's attacks and retaliate, auto-aggro hostiles
        b.targeting.attack_with_owner = true;
        b.targeting.revenge = true;
        b.targeting.automatic_targeting = SummonBehaviour.Targeting.AutoTarget.HOSTILE;

        // Actions: fireball (preferred), melee bite when close
        var melee = new SummonBehaviour.Action.MeleeAttack();
        melee.max_range = 4F;
        melee.speed = 1F;
        melee.windup = 0.4F;
        melee.radius = 0.5F;
        melee.animation_variants = List.of(1);

        b.actions = List.of(
                SummonBehaviour.Action.spell("wizards:fireball", 20),
                SummonBehaviour.Action.attack(melee)
        );

        // Attribute scaling: standard combat stats scaling with fire spell power (no size bump)
        b.attribute_scaling.entries = schoolCombatScaling(SpellSchools.FIRE);

        // Placement: 2 blocks ahead of the caster, snapped to the ground
        var placement = new Spell.EntityPlacement();
        placement.location_offset_by_look = 2;
        placement.force_onto_ground = true;

        return new Summon(FireHydraEntity.ID.toString(), b, placement);
    }

    // MARK: Scaling helpers

    /// The standard owner-scaled combat stat block shared by attacker summons: health, armor, attack
    /// damage, spell power, attack knockback and knockback resistance — all scaling off the owner's
    /// spell power in the given school. Returned mutable so callers can append school-specific extras.
    private static List<SummonBehaviour.AttributeScaling.Entry> schoolCombatScaling(SpellSchool school) {
        var s = school.id.toString();
        var entries = new ArrayList<SummonBehaviour.AttributeScaling.Entry>();
        entries.add(scalingEntry(EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString(), s, 0, 2.0));
        entries.add(scalingEntry(EntityAttributes.GENERIC_ARMOR.getIdAsString(), s, 10, 0.1));
        entries.add(scalingEntry(EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString(), s, 0, 0.5));
        entries.add(scalingEntry(s, s, 3, 0.1)); // spell power feeds back into the school attribute
        entries.add(scalingEntry(EntityAttributes.GENERIC_ATTACK_KNOCKBACK.getIdAsString(), s, 0, 0.1));
        entries.add(scalingEntry(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getIdAsString(), s, 5, 0.05));
        return entries;
    }

    /// A single attribute-scaling entry: `targetAttribute += base + ownerAttribute * coefficient`
    /// (ADD_VALUE).
    private static SummonBehaviour.AttributeScaling.Entry scalingEntry(String targetAttribute, String ownerAttribute,
                                                                       double base, double coefficient) {
        var entry = new SummonBehaviour.AttributeScaling.Entry();
        entry.attribute_id = targetAttribute;
        entry.modifiers = List.of(new SummonBehaviour.AttributeScaling.Entry.OwnerModifier(
                ownerAttribute, EntityAttributeModifier.Operation.ADD_VALUE, base, coefficient));
        return entry;
    }

    // MARK: Handler registration + spawning

    /// Registers one generic custom-impact handler per summon definition. Replaces the former
    /// per-entity handler lambdas.
    public static void registerHandlers() {
        defaultSummons().forEach((id, def) ->
                SpellHandlers.registerCustomImpact(id, new SpellHandlers.CustomImpact() {
                    @Override
                    public SpellHandlers.ImpactResult onSpellImpact(RegistryEntry<Spell> spellEntry, net.spell_power.api.SpellPower.Result power,
                                                                    LivingEntity caster, @Nullable Entity target,
                                                                    SpellHelper.ImpactContext context) {
                        spawn(def, spellEntry, caster, context);
                        return new SpellHandlers.ImpactResult(true, false);
                    }
                }));
    }

    /// Spawns one summon from its definition: creates the entity by id, hands it the behaviour,
    /// positions it via SpellEngine's EntityPlacement, and falls back to a line-of-sight search if
    /// the placed position is clipped into geometry.
    private static void spawn(Summon def, RegistryEntry<Spell> spellEntry, LivingEntity caster, SpellHelper.ImpactContext context) {
        var world = caster.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        var type = Registries.ENTITY_TYPE.get(Identifier.of(def.entity_type_id));
        var created = (Entity) type.create(world);
        if (!(created instanceof SpellSummoned summoned)) return;

        summoned.onSummonedBySpell(new SpellSummoned.Args(caster, spellEntry, def.behaviour, context));

        // Primary placement: SpellEngine's EntityPlacement (sets position, and yaw/pitch when configured).
        SpellHelper.applyEntityPlacement(created, caster, caster.getPos(), def.placement);
        // applyEntityPlacement only sets entity yaw; sync head/body yaw so the initial pose matches.
        if (def.placement.apply_yaw && created instanceof LivingEntity living) {
            living.setHeadYaw(living.getYaw());
            living.setBodyYaw(living.getYaw());
        }
        // Anti-clip fallback: if the placed position can't see the caster (likely inside a wall),
        // relocate using the cardinal + line-of-sight search.
        if (!hasLineOfSight(caster, created.getPos(), serverWorld)) {
            var fallback = findSpawnPosition(caster, serverWorld);
            created.setPosition(fallback.x, fallback.y, fallback.z);
        }

        serverWorld.spawnEntity(created);
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
