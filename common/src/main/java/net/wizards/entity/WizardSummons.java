package net.wizards.entity;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.spell_engine.api.datagen.SpellBuilder.Placements;
import net.spell_engine.api.spell.Spell.Impact.Action.Summon;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.spell.fx.VFX;
import net.spell_engine.api.spell.summon.AttributeScaling;
import net.spell_engine.api.spell.summon.SummonBehaviour;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import net.wizards.content.WizardsSounds;

import java.util.ArrayList;
import java.util.List;

/// Factory of the Wizards summon definitions. Each builder returns a {@link Summon}
/// ({@code Spell.Impact.Action.Summon}) that a spell drops into a `SUMMON` impact — the engine's
/// {@code SpellHelper} spawns and configures it.
public class WizardSummons {

    // MARK: Summon definitions

    public static Summon frostElemental() {
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

        // Spawn FX: a rising column of snowflakes bursting from the ground as the elemental forms.
        b.spawn_fx = new VFX();
        b.spawn_fx.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        80, 0.15F, 0.6F)
                        .extent(0.5F)
        };

        // Placement: 2 blocks ahead of the caster, snapped to the ground, keeping its own facing.
        var placement = Placements.point(2F, 0F, 0);
        placement.apply_yaw = false;

        var summon = new Summon(FrostElementalEntity.ID.toString(), b, List.of(placement), 1);
        // Attribute scaling: standard combat stats + a size bump, all scaling with frost spell power
        var scaling = schoolCombatScaling(SpellSchools.FROST);
        scaling.add(scalingEntry(EntityAttributes.GENERIC_SCALE.getIdAsString(),
                SpellSchools.FROST.id.toString(), 0, 0.05));
        summon.attribute_scaling.entries = scaling;
        return summon;
    }

    public static Summon arcaneEmitter() {
        var b = new SummonBehaviour();
        b.lifespan.active_seconds = 15;
        b.is_attackable = false;

        b.sounds.spawn = WizardsSounds.ARCANE_EMITTER_SPAWN.id().toString();
        b.sounds.despawn = WizardsSounds.ARCANE_EMITTER_DESPAWN.id().toString();

        // Spawn FX: an arcane explosion as the emitter materialises — the same burst as Arcane Blast's
        // impact, but with DECELERATE motion so the particles rush outward and settle rather than scatter.
        b.spawn_fx = new VFX();
        b.spawn_fx.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.3F, 0.5F)
                        .color(Color.from(SpellSchools.ARCANE.color).toRGBA()),
                new ParticleBatch(
                        "firework",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.15F)
        };

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

        // Placement: a perpendicular line of up to 7 floating emitters, filled from the centre
        // outwards and staggered by 10 ticks, the whole line pushed behind the caster via a single
        // group offset. A smaller spawn_count yields a tighter centered line. Each floats ~1 block up
        // and faces the caster's aim (apply_yaw/pitch), so the FORWARD-fallback barrage fires the way
        // the player is looking.
        var emitter = Placements.template(); // apply_yaw = true, ground-snap = true
        emitter.force_onto_ground = false;   // floats instead of snapping to the ground
        emitter.apply_pitch = true;          // also aim with the caster's pitch
        emitter.location_offset_y = 1.0F;    // ~1 block up
        var placements = Placements.delayCascade(
                Placements.row(7, 1.5F, Placements.RowOrder.CENTER_OUT, emitter), 10);

        // One group, offset straight behind the caster (pure translation, no ground snap), seeding
        // the perpendicular per-entity line.
        var behindGroup = Placements.point(2F, 180F, 0);
        behindGroup.force_onto_ground = false;
        behindGroup.apply_yaw = false;
        var groupPlacements = List.of(behindGroup);

        var summon = new Summon(ArcaneEmitterEntity.ID.toString(), b, placements, 3, groupPlacements, 1);
        summon.attribute_scaling.entries = List.of(spellPower);
        return summon;
    }

    public static Summon fireHydra() {
        var b = new SummonBehaviour();
        b.lifespan.active_seconds = 30;
        b.lifespan.spawn_ticks = 20;
        b.lifespan.despawn_ticks = 20;
        b.is_attackable = false;

        b.sounds.spawn = WizardsSounds.FIRE_HYDRA_SPAWN.id().toString();
        b.sounds.despawn = WizardsSounds.FIRE_HYDRA_DESPAWN.id().toString();
        b.sounds.ambient = WizardsSounds.FIRE_HYDRA_AMBIENT.id().toString();

        // Existence FX: a fiery ground ring looping under the hydra for its whole active phase. The
        // area_effect_715 ring animates over 22 ticks, so it re-emits every 22 ticks to loop seamlessly.
        var aura = new SummonBehaviour.ExistenceParticles();
        var particle = SpellEngineParticles.area_effect_715;
        aura.particles = new ParticleBatch[] {
                new ParticleBatch(
                        particle.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.GROUND,
                        1, 0, 0)
                        .scale(1.2F)
                        .color(Color.from(SpellSchools.FIRE.color).toRGBA())
        };
        aura.interval_ticks = particle.texture().frames();

        // A few small flames flickering around the hydra's feet, scattered within a short radius and
        // rising slightly. Refreshed every 5 ticks so the feet always look alight.
        var flames = new SummonBehaviour.ExistenceParticles();
        flames.interval_ticks = 5;
        flames.particles = new ParticleBatch[] {
                new ParticleBatch(SpellEngineParticles.flame_medium_a.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET, 1, 0.01F, 0.06F).extent(0.4F),
                new ParticleBatch(SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET, 1, 0.01F, 0.06F).extent(0.4F),
                new ParticleBatch(SpellEngineParticles.flame_spark.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET, 1, 0.02F, 0.08F).extent(0.5F),
        };

        b.existence_particles = List.of(aura, flames);

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
                SummonBehaviour.Action.spell("wizards:fireball", 20)
                // SummonBehaviour.Action.attack(melee)
        );


        // Placement: a tight formation around the caster — front, right, left, back, each 1 block out,
        // ground-snapped and facing the caster's yaw, staggered 5 ticks apart. With spawn_count = 3
        // the loop cycles through the first three slots: front, right, left.
        float d = 1F;
        var placements = List.of(
                Placements.point(d, 0F, 0),    // front
                Placements.point(d, 90F, 5),   // right
                Placements.point(d, 270F, 10), // left
                Placements.point(d, 180F, 15)  // back
        );

        // Group placement: the same formation at 3x the distance, used as a per-group offset. With
        // group_count = 2 the loop cycles through the first two slots.
        float gd = d * 3F;
        var groupPlacements = List.of(
                Placements.point(gd, 90F, 0),   // right
                Placements.point(gd, 270F, 20), // left
                Placements.point(gd, 0F, 40),   // front
                Placements.point(gd, 180F, 60)  // back
        );

        var summon = new Summon(FireHydraEntity.ID.toString(), b, placements, 3, groupPlacements, 2);
        // Attribute scaling: standard combat stats scaling with fire spell power (no size bump)
        summon.attribute_scaling.entries = schoolCombatScaling(SpellSchools.FIRE);
        summon.group_spawn_sound = Sound.of(WizardsSounds.FIRE_HYDRA_GROUP_SPAWN.id());
        summon.group_spawn_fx = fireHydraGroupSpawnFx();
        return summon;
    }

    /// A one-shot fire puff played once per group as the Fire Hydra spawns: the same flame batches
    /// the Wall of Flames clouds emit (ground flames, medium flames, sparks and cosy smoke, all
    /// rising from the feet), but at reduced counts so it reads as a subtle flourish, not a blaze.
    private static VFX fireHydraGroupSpawnFx() {
        var fx = new VFX();
        fx.particles = new ParticleBatch[] {
                new ParticleBatch(SpellEngineParticles.flame_ground.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET, 4, 0, 0),
                new ParticleBatch(SpellEngineParticles.flame_medium_a.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET, 8, 0.02F, 0.3F),
                new ParticleBatch(SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET, 8, 0.01F, 0.35F),
                new ParticleBatch(SpellEngineParticles.flame_spark.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET, 12, 0.05F, 0.3F),
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET, 1F, 0.05F, 0.1F),
        };
        return fx;
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

}
