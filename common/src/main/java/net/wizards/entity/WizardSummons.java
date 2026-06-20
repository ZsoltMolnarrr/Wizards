package net.wizards.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.spell_engine.api.datagen.SpellBuilder.Placements;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.fx.ModelEffectHelper;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.WorldScheduler;
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

        // Placement: 2 blocks ahead of the caster, snapped to the ground, keeping its own facing.
        var placement = Placements.byLook(2F, 0F, 0);
        placement.apply_yaw = false;

        return new Summon(FrostElementalEntity.ID.toString(), b, List.of(placement), 1);
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

        // Placement: a perpendicular line of up to 7 floating emitters, filled from the centre
        // outwards and staggered by 10 ticks, the whole line pushed behind the caster via a single
        // group offset. A smaller spawn_count yields a tighter centered line. Each floats ~1 block up
        // and faces the caster's aim (apply_yaw/pitch), so the FORWARD-fallback barrage fires the way
        // the player is looking.
        var emitter = Placements.template(); // apply_yaw = true, ground-snap = true
        emitter.force_onto_ground = false;   // floats instead of snapping to the ground
        emitter.apply_pitch = true;          // also aim with the caster's pitch
        emitter.location_offset_y = 1.0F;    // ~1 block up
        var placements = Placements.staggered(
                Placements.line(7, 1.5F, Placements.LineOrder.CENTER_OUT, emitter), 10);

        // One group, offset straight behind the caster (pure translation, no ground snap), seeding
        // the perpendicular per-entity line.
        var behindGroup = Placements.byLook(2F, 180F, 0);
        behindGroup.force_onto_ground = false;
        behindGroup.apply_yaw = false;
        var groupPlacements = List.of(behindGroup);

        return new Summon(ArcaneEmitterEntity.ID.toString(), b, placements, 3, groupPlacements, 1);
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

        // Placement: a tight formation around the caster — front, right, left, back, each 1 block out,
        // ground-snapped and facing the caster's yaw, staggered 5 ticks apart. With spawn_count = 3
        // the loop cycles through the first three slots: front, right, left.
        float d = 1F;
        var placements = List.of(
                Placements.byLook(d, 0F, 0),    // front
                Placements.byLook(d, 90F, 5),   // right
                Placements.byLook(d, 270F, 10), // left
                Placements.byLook(d, 180F, 15)  // back
        );

        // Group placement: the same formation at 3x the distance, used as a per-group offset. With
        // group_count = 2 the loop cycles through the first two slots.
        float gd = d * 3F;
        var groupPlacements = List.of(
                Placements.byLook(gd, 90F, 0),   // right
                Placements.byLook(gd, 270F, 20), // left
                Placements.byLook(gd, 0F, 40),   // front
                Placements.byLook(gd, 180F, 60)  // back
        );

        return new Summon(FireHydraEntity.ID.toString(), b, placements, 3, groupPlacements, 2);
    }

    // MARK: Scaling helpers

    /// The standard owner-scaled combat stat block shared by attacker summons: health, armor, attack
    /// damage, spell power, attack knockback and knockback resistance — all scaling off the owner's
    /// spell power in the given school. Returned mutable so callers can append school-specific extras.
    private static List<AttributeScaling.Entry> schoolCombatScaling(SpellSchool school) {
        var s = school.id.toString();
        var entries = new ArrayList<AttributeScaling.Entry>();
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
    private static AttributeScaling.Entry scalingEntry(String targetAttribute, String ownerAttribute,
                                                                       double base, double coefficient) {
        var entry = new AttributeScaling.Entry();
        entry.attribute_id = targetAttribute;
        entry.modifiers = List.of(new AttributeScaling.Entry.OwnerModifier(
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

    /// Spawns the summon(s) from a definition. `group_count` groups are spawned; each group replays
    /// the per-entity formation (`spawn_count` entities cycling through `placements`), translated by
    /// the next group placement (cycling through `group_placements`). Every entity is created by id,
    /// handed the behaviour, positioned via SpellEngine's EntityPlacement (group offset first, then
    /// the per-entity placement on top), and falls back to a line-of-sight search if the placed
    /// position is clipped into geometry. Group and per-entity `delay_ticks` are summed and defer the
    /// actual world spawn (entities are positioned at cast time, anchored to the caster's cast-time
    /// state, matching SpellEngine's built-in SPAWN action).
    private static void spawn(Summon def, RegistryEntry<Spell> spellEntry, LivingEntity caster, SpellHelper.ImpactContext context) {
        var world = caster.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        var type = Registries.ENTITY_TYPE.get(Identifier.of(def.entity_type_id));
        for (int g = 0; g < def.group_count; g++) {
            // Next group slot, wrapping around the list (null when no group offset is configured).
            var groupPlacement = def.group_placements.isEmpty() ? null : def.group_placements.get(g % def.group_placements.size());
            int groupDelay = groupPlacement != null ? groupPlacement.delay_ticks : 0;
            Vec3d groupAnchor = null; // caster position + group offset; captured from the first entity

            for (int i = 0; i < def.spawn_count; i++) {
                var created = (Entity) type.create(world);
                if (!(created instanceof SpellSummoned summoned)) return;

                // Next per-entity slot, wrapping around the list (null when no slots are configured).
                var placement = def.placements.isEmpty() ? null : def.placements.get(i % def.placements.size());

                summoned.onSummonedBySpell(new SpellSummoned.Args(caster, spellEntry, def.behaviour, context));

                // Compose placements: the group offset's resulting position seeds the per-entity
                // placement (both rotate the look-offset by the caster's yaw, so the formation keeps
                // a consistent caster-relative orientation across groups).
                var origin = caster.getPos();
                if (groupPlacement != null) {
                    SpellHelper.applyEntityPlacement(created, caster, origin, groupPlacement);
                    origin = created.getPos();
                }
                if (i == 0) groupAnchor = origin; // the group's anchor (pre per-entity offset)
                SpellHelper.applyEntityPlacement(created, caster, origin, placement);

                // applyEntityPlacement only sets entity yaw; sync head/body yaw so the initial pose matches.
                boolean appliedYaw = (groupPlacement != null && groupPlacement.apply_yaw)
                        || (placement != null && placement.apply_yaw);
                if (appliedYaw && created instanceof LivingEntity living) {
                    living.setHeadYaw(living.getYaw());
                    living.setBodyYaw(living.getYaw());
                }
                // Anti-clip: when a contributing placement opts in via `line_of_sight`, and the placed
                // position is out of the caster's line of sight (e.g. behind a wall), pull it back
                // along the sightline to the closest point that is still visible.
                boolean checkLineOfSight = (placement != null && placement.line_of_sight)
                        || (groupPlacement != null && groupPlacement.line_of_sight);
                if (checkLineOfSight) {
                    var visible = nearestVisiblePosition(caster, created.getPos(), serverWorld);
                    created.setPosition(visible.x, visible.y, visible.z);
                }

                // Defer the world spawn by the combined group + per-entity delay (0 = spawn this tick).
                int entityDelay = placement != null ? placement.delay_ticks : 0;
                ((WorldScheduler) serverWorld).schedule(groupDelay + entityDelay, () -> serverWorld.spawnEntity(created));
            }

            // Group spawn FX: one-shot at the group anchor, deferred by the group delay.
            if (def.group_spawn_fx != null && groupAnchor != null) {
                var anchor = groupAnchor;
                var fx = def.group_spawn_fx;
                ((WorldScheduler) serverWorld).schedule(groupDelay, () -> emitGroupSpawnFx(serverWorld, caster, anchor, fx));
            }
        }
    }

    /// Emits a one-shot FX bundle at a fixed location (the group anchor): particles via a tracker
    /// packet to the caster's viewers, model effects as self-syncing entities, and the sound at the
    /// anchor position.
    private static void emitGroupSpawnFx(ServerWorld world, LivingEntity caster, Vec3d anchor, SummonFx fx) {
        if (fx.particles != null && fx.particles.length > 0) {
            ParticleHelper.sendBatches(anchor, caster, fx.particles);
        }
        ModelEffectHelper.spawn(world, anchor, caster.getYaw(), fx.model_fx);
        if (fx.sound != null) {
            var soundEvent = Registries.SOUND_EVENT.get(Identifier.of(fx.sound.id()));
            if (soundEvent != null) {
                world.playSound(null, anchor.x, anchor.y, anchor.z, soundEvent,
                        SoundCategory.PLAYERS, fx.sound.volume(), fx.sound.randomizedPitch());
            }
        }
    }

    /// Distance the result is pulled back from a blocking surface along the sightline, so the entity
    /// sits just shy of the geometry rather than embedded in its face.
    private static final double LOS_SURFACE_BACKOFF = 0.5;

    /// The point along the segment from the caster's eyes to `desired` that is still in line of sight:
    /// `desired` itself when the path is unobstructed, otherwise the closest clear point just before
    /// the blocking surface (pulled back `LOS_SURFACE_BACKOFF` blocks off the face). Never returns a
    /// point behind the caster's eyes.
    private static Vec3d nearestVisiblePosition(LivingEntity caster, Vec3d desired, ServerWorld world) {
        var from = caster.getEyePos();
        var hit = world.raycast(new RaycastContext(
                from, desired,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                caster));
        if (hit.getType() != HitResult.Type.BLOCK) {
            return desired; // unobstructed line of sight
        }
        var ray = desired.subtract(from);
        var length = ray.length();
        if (length < 1.0e-4) {
            return from;
        }
        var candidate = hit.getPos().subtract(ray.multiply(LOS_SURFACE_BACKOFF / length));
        // Guard against a surface right at the caster's face pushing the point behind the eyes.
        if (candidate.subtract(from).dotProduct(ray) < 0) {
            return from;
        }
        return candidate;
    }
}
