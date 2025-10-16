package net.wizards.content;

import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_power.api.SpellSchools;
import net.wizards.WizardsMod;
import net.wizards.effect.WizardsEffects;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WizardSpells {
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator) { }
    public static final List<Entry> entries = new ArrayList<>();
    private static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static final String PRIMARY_GROUP = "primary";
    private static final float BASIC_PROJECTILE_RANGE = 48F;
    private static final Color ARCANE_COLOR = Color.from(SpellSchools.ARCANE.color);

    private static Spell activeSpellBase() {
        var spell = new Spell();
        spell.type = Spell.Type.ACTIVE;
        spell.active = new Spell.Active();
        spell.active.cast = new Spell.Active.Cast();
        return spell;
    }

    private static ParticleBatch arcaneCastingParticles() {
        return new ParticleBatch(
                SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPELL,
                        SpellEngineParticles.MagicParticles.Motion.ASCEND
                ).id().toString(),
                ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                1, 0.05F, 0.1F)
                .color(ARCANE_COLOR.toRGBA());
    }

    private static Spell.Impact damage(float coefficient, float knockback) {
        var impact = new Spell.Impact();
        impact.action = new Spell.Impact.Action();
        impact.action.type = Spell.Impact.Action.Type.DAMAGE;
        impact.action.damage = new Spell.Impact.Action.Damage();
        impact.action.damage.spell_power_coefficient = coefficient;
        impact.action.damage.knockback = knockback;
        return impact;
    }

    private static void configureArcaneRuneCost(Spell spell) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        spell.cost.item = new Spell.Cost.Item();
        spell.cost.item.id = "runes:arcane_stone";
    }

    private static void configureCooldown(Spell spell, float duration) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        spell.cost.cooldown = new Spell.Cost.Cooldown();
        spell.cost.cooldown.duration = duration;
    }

    public static Entry arcane_bolt = add(arcane_bolt());
    private static Entry arcane_bolt() {
        var id = Identifier.of(WizardsMod.ID, "arcane_bolt");
        var spell = activeSpellBase();
        spell.school = SpellSchools.ARCANE;
        spell.group = PRIMARY_GROUP;
        spell.tier = 0;
        spell.range = BASIC_PROJECTILE_RANGE;
        spell.active.cast.duration = 1;
        spell.active.cast.animation = "spell_engine:one_handed_projectile_charge";
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_ARCANE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { arcaneCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = "spell_engine:one_handed_projectile_release";
        spell.release.sound = new Sound(WizardsSounds.ARCANE_MISSILE_RELEASE.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 1F;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 10;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPELL,
                                SpellEngineParticles.MagicParticles.Motion.ASCEND
                        ).id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0.05F, 0.1F, 0.0F, 0F)
                        .color(ARCANE_COLOR.toRGBA())
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:projectile/arcane_bolt";
        projectile.client_data.model.scale = 0.5F;
        spell.deliver.projectile.projectile = projectile;

        var damage = damage(0.7F, 0.6F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        null, 20, 0.2F, 0.7F, 0.0F, 0F)
                        .color(ARCANE_COLOR.toRGBA())
        };
        damage.sound = new Sound(WizardsSounds.ARCANE_MISSILE_IMPACT.id());
        spell.impacts = List.of(damage);

        configureArcaneRuneCost(spell);

        return new Entry(id, spell, "", "", null);
    }

    public static Entry arcane_blast = add(arcane_blast());
    private static Entry arcane_blast() {
        var id = Identifier.of(WizardsMod.ID, "arcane_blast");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.group = PRIMARY_GROUP;
        spell.tier = 1;
        spell.sub_tier = 2;
        spell.range = 16;

        spell.learn = new Spell.Learn();
        spell.active.scroll = new Spell.Active.Scroll();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = "spell_engine:one_handed_projectile_charge";
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_ARCANE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { arcaneCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = "spell_engine:one_handed_projectile_release";
        spell.release.sound = new Sound(WizardsSounds.ARCANE_MISSILE_RELEASE.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = true;

        var damage = SpellBuilder.Impacts.damage(0.8F, 0.5F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        30, 0.2F, 0.7F)
                        .color(ARCANE_COLOR.toRGBA()),
                new ParticleBatch(
                        "firework",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        20, 0.05F, 0.2F)
        };
        damage.sound = new Sound(WizardsSounds.ARCANE_BLAST_IMPACT.id());

        var arcaneCharge = SpellBuilder.Impacts.effectAdd(WizardsEffects.arcaneCharge.id.toString(), 10, 1, 2);
        arcaneCharge.action.status_effect.show_particles = false;
        arcaneCharge.action.apply_to_caster = true;

        spell.impacts = List.of(damage, arcaneCharge);

        configureArcaneRuneCost(spell);

        return new Entry(id, spell, "", "", null);
    }

    public static Entry arcane_missile = add(arcane_missile());
    private static Entry arcane_missile() {
        var id = Identifier.of(WizardsMod.ID, "arcane_missile");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 2;
        spell.range = 64;

        spell.learn = new Spell.Learn();
        spell.active.scroll = new Spell.Active.Scroll();

        SpellBuilder.Casting.channel(spell, 4, 6);
        spell.active.cast.animation = "spell_engine:two_handed_channeling";
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_ARCANE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { arcaneCastingParticles() };

        spell.release = new Spell.Release();

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = true;

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.direction_offsets_require_target = true;
        spell.deliver.projectile.direction_offsets = new Spell.Delivery.ShootProjectile.DirectionOffset[] {
                new Spell.Delivery.ShootProjectile.DirectionOffset(15, 0),
                new Spell.Delivery.ShootProjectile.DirectionOffset(0, -15),
                new Spell.Delivery.ShootProjectile.DirectionOffset(-15, 0)
        };
        spell.deliver.projectile.launch_properties.velocity = 1.25F;
        spell.deliver.projectile.launch_properties.sound = new Sound(WizardsSounds.ARCANE_MISSILE_RELEASE.id());

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 4F;
        projectile.homing_angles = new float[] { 60F, 30F, 15F };
        projectile.homing_after_absolute_distance = 8F;
        projectile.homing_after_relative_distance = 0.3F;
        projectile.perks.pierce = 2;
        projectile.perks.bounce = 1;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPELL,
                                SpellEngineParticles.MagicParticles.Motion.ASCEND
                        ).id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 2, 0.05F, 0.1F, 0)
                        .color(ARCANE_COLOR.toRGBA())
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:projectile/arcane_missile";
        projectile.client_data.model.scale = 0.6F;
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.8F, 0.5F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        40, 0.2F, 0.7F)
                        .color(ARCANE_COLOR.toRGBA())
        };
        damage.sound = new Sound(WizardsSounds.ARCANE_MISSILE_IMPACT.id());
        spell.impacts = List.of(damage);

        configureArcaneRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 2);
        spell.cost.cooldown.proportional = true;

        return new Entry(id, spell, "", "", null);
    }

    public static Entry arcane_beam = add(arcane_beam());
    private static Entry arcane_beam() {
        var id = Identifier.of(WizardsMod.ID, "arcane_beam");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 3;
        spell.range = 32;

        spell.learn = new Spell.Learn();
        spell.active.scroll = new Spell.Active.Scroll();

        SpellBuilder.Casting.channel(spell, 5, 4);
        spell.active.cast.animation = "spell_engine:two_handed_channeling";
        spell.active.cast.sound = new Sound(WizardsSounds.ARCANE_BEAM_CASTING.id(), 0);
        spell.active.cast.start_sound = new Sound(WizardsSounds.ARCANE_BEAM_START.id());
        spell.active.cast.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPELL,
                                SpellEngineParticles.MagicParticles.Motion.ASCEND
                        ).id().toString(),
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK, 0.5F, 0.1F, 0.2F, 0)
                        .color(ARCANE_COLOR.toRGBA()),
                new ParticleBatch(
                        "firework",
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK, 0.5F, 0.1F, 0.2F, 0)
        };

        spell.release = new Spell.Release();
        spell.release.sound = new Sound(WizardsSounds.ARCANE_BEAM_RELEASE.id());

        spell.target.type = Spell.Target.Type.BEAM;
        spell.target.beam = new Spell.Target.Beam();
        spell.target.beam.color_rgba = 0xFF66FFFFL;
        spell.target.beam.width = 0.08F;
        spell.target.beam.flow = 1.5F;
        spell.target.beam.block_hit_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPELL,
                                SpellEngineParticles.MagicParticles.Motion.ASCEND
                        ).id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0.1F, 0.2F, 0)
                        .color(ARCANE_COLOR.toRGBA()),
                new ParticleBatch(
                        "firework",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0.1F, 0.2F, 0),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        3, 0.3F, 0.4F)
                        .color(ARCANE_COLOR.toRGBA())
        };

        var damage = SpellBuilder.Impacts.damage(1F, 1F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        4, 0.2F, 0.7F)
                        .color(ARCANE_COLOR.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPELL,
                                SpellEngineParticles.MagicParticles.Motion.ASCEND
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        2, 0.1F, 0.2F)
                        .color(ARCANE_COLOR.toRGBA()),
                new ParticleBatch(
                        "firework",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        3, 0.1F, 0.2F)
        };
        damage.sound = new Sound(WizardsSounds.ARCANE_BEAM_IMPACT.id());
        spell.impacts = List.of(damage);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        spell.cost.effect_id = WizardsEffects.arcaneCharge.id.toString();
        configureArcaneRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 10);
        spell.cost.cooldown.proportional = true;

        return new Entry(id, spell, "", "", null);
    }

    public static Entry arcane_blink = add(arcane_blink());
    private static Entry arcane_blink() {
        var id = Identifier.of(WizardsMod.ID, "arcane_blink");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 4;
        spell.range = 0;

        spell.learn = new Spell.Learn();
        spell.active.scroll = new Spell.Active.Scroll();

        SpellBuilder.Casting.instant(spell);

        spell.release = new Spell.Release();
        spell.release.animation = "spell_engine:one_handed_area_release";
        spell.release.sound = new Sound(Identifier.of("minecraft", "entity.enderman.teleport"));

        var teleport = new Spell.Impact();
        teleport.action = new Spell.Impact.Action();
        teleport.action.type = Spell.Impact.Action.Type.TELEPORT;
        teleport.action.teleport = new Spell.Impact.Action.Teleport();
        teleport.action.teleport.mode = Spell.Impact.Action.Teleport.Mode.FORWARD;
        teleport.action.teleport.forward = new Spell.Impact.Action.Teleport.Forward();
        teleport.action.teleport.forward.distance = 15F;
        teleport.action.teleport.depart_particles = new ParticleBatch[] {
                new ParticleBatch(
                        "minecraft:portal",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        40, 0.1F, 0.3F)
                        .preSpawnTravel(1)
        };
        teleport.particles = new ParticleBatch[] {
                new ParticleBatch(
                        "minecraft:portal",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        40, 0.1F, 0.3F)
                        .invert()
                        .preSpawnTravel(4)
        };
        spell.impacts = List.of(teleport);

        SpellBuilder.Cost.exhaust(spell, 0.4F);
        configureArcaneRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 12);

        return new Entry(id, spell, "", "", null);
    }

    public static Entry fire_wall = add(fire_wall());
    private static Entry fire_wall() {
        var id = Identifier.of(WizardsMod.ID, "fire_wall");
        var name = "Wall of Flames";
        var description = "Creates a wall of fire, lasting {cloud_duration} seconds, dealing up to {damage} fire spell damage continuously to enemies passing thru.";

        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 4;
        spell.school = SpellSchools.FIRE;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Release.visuals(spell,
                "spell_engine:one_handed_area_release_ground_left_to_right",
                null, null);

        spell.deliver.type = Spell.Delivery.Type.CLOUD;

        var cloud = new Spell.Delivery.Cloud();
        cloud.volume.radius = 0.9F;
        cloud.volume.area.vertical_range_multiplier = 4F;
        cloud.volume.sound = new Sound(WizardsSounds.FIRE_SCORCH_IMPACT.id());
        cloud.delay_ticks = 0;
        cloud.impact_tick_interval = 8;
        cloud.time_to_live_seconds = 8;
        cloud.spawn = new Spell.Delivery.Cloud.Spawn();
        cloud.spawn.sound = new Sound(WizardsSounds.FIRE_WALL_IGNITE.id());
        cloud.spawn.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.flame.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        15, 0.1F, 0.5F)
        };
        cloud.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud.client_data.light_level = 15;
        cloud.client_data.particles = new ParticleBatch[] {
                new ParticleBatch(SpellEngineParticles.flame_ground.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        2, 0, 0),
                new ParticleBatch(SpellEngineParticles.flame_medium_a.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        3, 0.02F, 0.3F),
                new ParticleBatch(SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        3, 0.01F, 0.35F),
                new ParticleBatch(SpellEngineParticles.flame_spark.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        4, 0.05F, 0.3F),
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        0.1F, 0.05F, 0.1F),
        };

        cloud.placement = SpellBuilder.Deliver.placementByLook(4.4f, -64, 0);
        cloud.additional_placements = List.of(
                SpellBuilder.Deliver.placementByLook(2.8f, -45, 4),
                SpellBuilder.Deliver.placementByLook(2f, 0, 4),
                SpellBuilder.Deliver.placementByLook(2.8f, 45, 4),
                SpellBuilder.Deliver.placementByLook(4.4f, 64, 4)
        );

        spell.deliver.clouds = List.of(cloud);

        var damage = SpellBuilder.Impacts.damage(0.8F, 0.4F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("smoke",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.01F, 0.1F),
                new ParticleBatch("flame",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.01F, 0.1F)
        };
        damage.sound = new Sound(WizardsSounds.FIRE_SCORCH_IMPACT.id());
        var fire = SpellBuilder.Impacts.fire(2);
        spell.impacts = List.of(damage, fire);

        SpellBuilder.Cost.cooldown(spell, 24);
        SpellBuilder.Cost.item(spell, "runes:fire_stone", 1);
        SpellBuilder.Cost.exhaust(spell, 0.4F);

        return new Entry(id, spell, name, description, null);
    }
}
