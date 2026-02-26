package net.wizards.content;

import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.PlayerAnimation;
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
    public enum WeaponGroup { WIZARD_STAFF, ARCANE_STAFF, FIRE_STAFF, FROST_STAFF }
    public enum Book { ARCANE, FIRE, FROST }
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator,
                        @Nullable List<WeaponGroup> weaponGroups,
                        @Nullable Book book) {
        public Entry(Identifier id, Spell spell, String title, String description) {
            this(id, spell, title, description, null, List.of(), null);
        }
        public Entry mutator(SpellTooltip.DescriptionMutator mutator) {
            return new Entry(id, spell, title, description, mutator, weaponGroups, book);
        }
        public Entry weaponGroup(WeaponGroup weaponGroup) {
            var newGroups = new ArrayList<>(weaponGroups != null ? weaponGroups : List.of());
            newGroups.add(weaponGroup);
            return new Entry(id, spell, title, description, mutator, newGroups, book);
        }
        public Entry book(Book book) {
            return new Entry(id, spell, title, description, mutator, weaponGroups, book);
        }
    }

    public static final List<Entry> entries = new ArrayList<>();
    private static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static final String PRIMARY_GROUP = "primary";
    private static final float BASIC_PROJECTILE_RANGE = 48F;
    private static final Color ARCANE_COLOR = Color.from(SpellSchools.ARCANE.color);
    private static final Color FIRE_COLOR = Color.from(SpellSchools.FIRE.color);
    private static final Color FROST_COLOR = Color.from(SpellSchools.FROST.color);

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

    // Fire spell helpers
    private static ParticleBatch fireCastingParticles() {
        return new ParticleBatch(
                SpellEngineParticles.flame.id().toString(),
                ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                1, 0.05F, 0.1F);
    }

    private static void configureFireRuneCost(Spell spell) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        spell.cost.item = new Spell.Cost.Item();
        spell.cost.item.id = "runes:fire_stone";
    }

    private static ParticleBatch[] fireImpactParticles() {
        return new ParticleBatch[] {
                new ParticleBatch("smoke",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.01F, 0.1F),
                new ParticleBatch("flame",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.01F, 0.1F)
        };
    }

    // Frost spell helpers
    private static ParticleBatch frostCastingParticles() {
        return new ParticleBatch(
                SpellEngineParticles.snowflake.id().toString(),
                ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.CENTER,
                0.5F, 0.1F, 0.2F);
    }

    private static void configureFrostRuneCost(Spell spell) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        spell.cost.item = new Spell.Cost.Item();
        spell.cost.item.id = "runes:frost_stone";
    }

    private static ParticleBatch[] frostImpactParticles() {
        return new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.FROST,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        50, 0.2F, 0.7F)
                        .color(FROST_COLOR.toRGBA())
        };
    }

    public static Entry arcane_bolt = add(arcane_bolt());
    private static Entry arcane_bolt() {
        var id = Identifier.of(WizardsMod.ID, "arcane_bolt");
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.ARCANE;
        spell.group = PRIMARY_GROUP;
        spell.tier = 0;
        spell.range = BASIC_PROJECTILE_RANGE;
        spell.active.cast.duration = 1;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_ARCANE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { arcaneCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");
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
        projectile.client_data.model.model_id = "wizards:spell_projectile/arcane_bolt";
        projectile.client_data.model.scale = 0.5F;
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.7F, 0.6F);
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

        SpellBuilder.Cost.cooldownGroup(spell, "weapon");

        configureArcaneRuneCost(spell);

        return new Entry(id, spell, "", "");
    }

    public static Entry arcane_blast = add(arcane_blast());
    private static Entry arcane_blast() {
        var id = Identifier.of(WizardsMod.ID, "arcane_blast");
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.ARCANE;
        spell.group = PRIMARY_GROUP;
        spell.tier = 1;
        spell.sub_tier = 2;
        spell.range = 16;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_ARCANE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { arcaneCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");
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

        SpellBuilder.Cost.cooldownGroup(spell, "weapon");

        configureArcaneRuneCost(spell);

        return new Entry(id, spell, "", "").weaponGroup(WeaponGroup.ARCANE_STAFF).weaponGroup(WeaponGroup.WIZARD_STAFF);
    }

    public static Entry arcane_missile = add(arcane_missile());
    private static Entry arcane_missile() {
        var id = Identifier.of(WizardsMod.ID, "arcane_missile");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 2;
        spell.range = 64;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.channel(spell, 4, 12);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
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
        projectile.client_data.model.model_id = "wizards:spell_projectile/arcane_missile";
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

        return new Entry(id, spell, "", "").book(Book.ARCANE);
    }

    public static Entry arcane_beam = add(arcane_beam());
    private static Entry arcane_beam() {
        var id = Identifier.of(WizardsMod.ID, "arcane_beam");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 3;
        spell.range = 32;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.channel(spell, 5, 25);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
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

        return new Entry(id, spell, "", "").book(Book.ARCANE);
    }

    public static Entry arcane_blink = add(arcane_blink());
    private static Entry arcane_blink() {
        var id = Identifier.of(WizardsMod.ID, "arcane_blink");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 4;
        spell.range = 0;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.instant(spell);

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_area_release");
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

        return new Entry(id, spell, "", "").book(Book.ARCANE);
    }

    public static Entry fire_scorch = add(fire_scorch());
    private static Entry fire_scorch() {
        var id = Identifier.of(WizardsMod.ID, "fire_scorch");
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FIRE;
        spell.group = PRIMARY_GROUP;
        spell.tier = 0;
        spell.sub_tier = 0;
        spell.range = 16;

        spell.active.cast.duration = 1.2F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { fireCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");
        spell.release.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_RELEASE.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;
        spell.target.aim.sticky = true;

        var damage = SpellBuilder.Impacts.damage(0.6F, 0.6F);
        damage.particles = fireImpactParticles();
        damage.sound = new Sound(WizardsSounds.FIRE_SCORCH_IMPACT.id());

        var fire = SpellBuilder.Impacts.fire(3);
        spell.impacts = List.of(damage, fire);

        SpellBuilder.Cost.cooldownGroup(spell, "weapon");

        configureFireRuneCost(spell);

        return new Entry(id, spell, "", "");
    }

    public static Entry fireball = add(fireball());
    private static Entry fireball() {
        var id = Identifier.of(WizardsMod.ID, "fireball");
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FIRE;
        spell.group = PRIMARY_GROUP;
        spell.tier = 0;
        spell.range = 64;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { fireCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1F;
        spell.deliver.projectile.launch_properties.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_RELEASE.id());

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 1F;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.flame.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 3, 0, 0.1F, 0),
                new ParticleBatch(
                        "smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0, 0.1F, 0)
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:spell_projectile/fireball";
        projectile.client_data.model.scale = 0.5F;
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.8F, 0.8F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("smoke",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.01F, 0.1F),
                new ParticleBatch(
                        SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        15, 0.1F, 0.2F)
        };
        damage.sound = new Sound(WizardsSounds.FIRE_SCORCH_IMPACT.id());

        var fire = SpellBuilder.Impacts.fire(4);
        spell.impacts = List.of(damage, fire);

        SpellBuilder.Cost.cooldownGroup(spell, "weapon");

        configureFireRuneCost(spell);

        return new Entry(id, spell, "", "");
    }

    public static Entry fire_blast = add(fire_blast());
    private static Entry fire_blast() {
        var id = Identifier.of(WizardsMod.ID, "fire_blast");
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FIRE;
        spell.group = PRIMARY_GROUP;
        spell.tier = 1;
        spell.range = 64;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { fireCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.25F;
        spell.deliver.projectile.launch_properties.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_RELEASE.id());

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 1F;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.flame_spark.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 4, 0, 0.1F, 0),
                new ParticleBatch(
                        SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 3, 0, 0.1F, 0),
                new ParticleBatch(
                        "smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 2, 0, 0.1F, 0)
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:spell_projectile/fire_blast";
        projectile.client_data.model.scale = 0.9F;
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(1F, 1.1F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("lava",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        15, 0.5F, 3F),
                new ParticleBatch(
                        SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        10, 0.1F, 0.2F)
        };

        spell.impacts = List.of(damage);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 2.5F;
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.area_impact.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.fire_explosion.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        2, 0.2F, 0.5F)
        };
        spell.area_impact.sound = new Sound(WizardsSounds.FIREBALL_IMPACT.id());

        configureFireRuneCost(spell);

        SpellBuilder.Cost.cooldownGroup(spell, "weapon");

        return new Entry(id, spell, "", "").weaponGroup(WeaponGroup.FIRE_STAFF).weaponGroup(WeaponGroup.WIZARD_STAFF);
    }

    public static Entry fire_breath = add(fire_breath());
    private static Entry fire_breath() {
        var id = Identifier.of(WizardsMod.ID, "fire_breath");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FIRE;
        spell.tier = 2;
        spell.range = 10;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.channel(spell, 5, 25);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
        spell.active.cast.sound = new Sound(WizardsSounds.FIRE_BREATH_CASTING.id(), 0);
        spell.active.cast.start_sound = new Sound(WizardsSounds.FIRE_BREATH_START.id());
        spell.active.cast.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.flame.id().toString(),
                        ParticleBatch.Shape.CONE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK, 8, 1, 1, 30),
                new ParticleBatch(
                        SpellEngineParticles.flame_medium_a.id().toString(),
                        ParticleBatch.Shape.CONE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK, 4, 1, 1, 30),
                new ParticleBatch(
                        SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.CONE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK, 4, 1, 1, 30)
        };

        spell.release = new Spell.Release();
        spell.release.sound = new Sound(WizardsSounds.FIRE_BREATH_RELEASE.id());

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.target.area.angle_degrees = 40;

        var damage = SpellBuilder.Impacts.damage(0.9F, 0.9F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("lava",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        3, 0.5F, 3F)
        };
        damage.sound = new Sound(WizardsSounds.FIRE_BREATH_IMPACT.id());

        var fire = SpellBuilder.Impacts.fire(2);
        spell.impacts = List.of(damage, fire);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        configureFireRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 10);
        spell.cost.cooldown.proportional = true;

        return new Entry(id, spell, "", "").book(Book.FIRE);
    }

    public static Entry fire_meteor = add(fire_meteor());
    private static Entry fire_meteor() {
        var id = Identifier.of(WizardsMod.ID, "fire_meteor");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FIRE;
        spell.tier = 3;
        spell.range = 32;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { fireCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_area_release");
        spell.release.sound = new Sound(WizardsSounds.FIRE_METEOR_RELEASE.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = true;

        spell.deliver.type = Spell.Delivery.Type.METEOR;
        spell.deliver.meteor = new Spell.Delivery.Meteor();
        spell.deliver.meteor.launch_height = 10;
        spell.deliver.meteor.launch_radius = 4;
        spell.deliver.meteor.launch_properties.velocity = 0.8F;
        spell.deliver.meteor.launch_properties.extra_launch_count = 2;
        spell.deliver.meteor.launch_properties.extra_launch_delay = 5;

        var projectile = new Spell.ProjectileData();
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.flame.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 3, 0, 0.1F, 0),
                new ParticleBatch(
                        "smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 5, 0.1F, 0.3F, 0),
                new ParticleBatch(
                        "campfire_cosy_smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 6, 0, 0.05F, 0)
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:spell_projectile/fire_meteor";
        spell.deliver.meteor.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(1F, 2F);
        damage.action.damage.spell_power_coefficient = 1F;
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("lava",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        3, 0.5F, 3F)
        };
        damage.sound = new Sound(WizardsSounds.FIRE_BREATH_IMPACT.id());
        spell.impacts = List.of(damage);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 6;
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.area_impact.particles = new ParticleBatch[] {
                new ParticleBatch("lava",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        90, 1.5F, 6F),
                new ParticleBatch("flame",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        100, 0.2F, 0.4F),
                new ParticleBatch("smoke",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        90, 0.1F, 0.3F)
        };
        spell.area_impact.sound = Sound.withVolume(WizardsSounds.FIRE_METEOR_IMPACT.id(), 1.5F);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        configureFireRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, "", "").book(Book.FIRE);
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

        return new Entry(id, spell, name, description).book(Book.FIRE);
    }

    public static Entry frost_shard = add(frost_shard());
    private static Entry frost_shard() {
        var id = Identifier.of(WizardsMod.ID, "frost_shard");
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FROST;
        spell.group = PRIMARY_GROUP;
        spell.tier = 0;
        spell.range = 48;

        spell.active.cast.duration = 1F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_FROST_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { frostCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");
        spell.release.sound = new Sound(SpellEngineSounds.GENERIC_FROST_RELEASE.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.2F;

        var projectile = new Spell.ProjectileData();
        projectile.perks.bounce = 2;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.FROST,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0.1F, 0.2F, 0)
                        .color(FROST_COLOR.toRGBA())
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:spell_projectile/frost_shard";
        projectile.client_data.model.scale = 0.75F;
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.6F, 1F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.FROST,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.2F, 0.7F)
                        .color(FROST_COLOR.toRGBA())
        };
        damage.sound = new Sound(WizardsSounds.FROST_SHARD_IMPACT.id());
        spell.impacts = List.of(damage);

        SpellBuilder.Cost.cooldownGroup(spell, "weapon");

        configureFrostRuneCost(spell);

        return new Entry(id, spell, "", "");
    }

    public static Entry frostbolt = add(frostbolt());
    private static Entry frostbolt() {
        var id = Identifier.of(WizardsMod.ID, "frostbolt");
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FROST;
        spell.group = PRIMARY_GROUP;
        spell.tier = 1;
        spell.sub_tier = 2;
        spell.range = 64;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.1F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_FROST_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { frostCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.2F;
        spell.deliver.projectile.launch_properties.sound = new Sound(SpellEngineSounds.GENERIC_FROST_RELEASE.id());

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 2F;
        projectile.perks.ricochet = 2;
        projectile.perks.bounce = 2;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 4, 0, 0.1F, 0),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.FROST,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0.1F, 0.2F, 0)
                        .color(FROST_COLOR.toRGBA())
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:spell_projectile/frostbolt";
        projectile.client_data.model.scale = 0.5F;
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.8F, 1F);
        damage.particles = frostImpactParticles();
        damage.sound = new Sound(SpellEngineSounds.GENERIC_FROST_IMPACT.id());

        var slowness = SpellBuilder.Impacts.effectAdd(WizardsEffects.frostSlowness.id.toString(), 5, 0, 1);
        slowness.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        slowness.action.status_effect.apply_limit.health_base = 100;
        slowness.action.status_effect.apply_limit.spell_power_multiplier = 4;
        slowness.action.status_effect.show_particles = false;
        slowness.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.1F, 0.4F)
        };

        spell.impacts = List.of(damage, slowness);

        configureFrostRuneCost(spell);

        SpellBuilder.Cost.cooldownGroup(spell, "weapon");

        return new Entry(id, spell, "", "").weaponGroup(WeaponGroup.FROST_STAFF).weaponGroup(WeaponGroup.WIZARD_STAFF);
    }

    public static Entry frost_nova = add(frost_nova());
    private static Entry frost_nova() {
        var id = Identifier.of(WizardsMod.ID, "frost_nova");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FROST;
        spell.tier = 2;
        spell.range = 6;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 0.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_area_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_FROST_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { frostCastingParticles() };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_area_release");
        spell.release.sound = new Sound(WizardsSounds.FROST_NOVA_RELEASE.id());
        spell.release.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        130, 0.2F, 0.6F),
                new ParticleBatch(
                        SpellEngineParticles.frost_shard.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        130, 0.5F, 0.9F)
        };
        spell.release.particles_scaled_with_ranged = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.area_effect_293.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.GROUND,
                        1, 0, 0)
                        .scale(0.8F)
                        .color(0x99E6FFFFL)
        };

        var damage = SpellBuilder.Impacts.damage(0.5F, 0.8F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.FROST,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        30, 0.2F, 0.7F)
                        .color(FROST_COLOR.toRGBA())
        };
        damage.sound = new Sound(WizardsSounds.FROST_NOVA_DAMAGE_IMPACT.id());

        var frozen = SpellBuilder.Impacts.effectAdd(WizardsEffects.frozen.id.toString(), 6, 1, 9);
        frozen.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        frozen.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        frozen.action.status_effect.apply_limit.health_base = 60;
        frozen.action.status_effect.apply_limit.spell_power_multiplier = 4;
        frozen.action.status_effect.show_particles = false;
        frozen.sound = new Sound(WizardsSounds.FROST_NOVA_EFFECT_IMPACT.id());

        spell.impacts = List.of(damage, frozen);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        configureFrostRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, "", "").book(Book.FROST);
    }

    public static Entry frost_shield = add(frost_shield());
    private static Entry frost_shield() {
        var id = Identifier.of(WizardsMod.ID, "frost_shield");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FROST;
        spell.tier = 3;
        spell.range = 0;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.instant(spell);

        spell.release = new Spell.Release();
        spell.release.animation =  PlayerAnimation.of("spell_engine:one_handed_area_release");
        spell.release.sound = new Sound(WizardsSounds.FROST_SHIELD_RELEASE.id());
        spell.release.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        90, 0.1F, 0.35F),
                new ParticleBatch(
                        SpellEngineParticles.frost_shard.id().toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        50, 0.1F, 0.3F)
        };

        var shield = SpellBuilder.Impacts.effectSet(WizardsEffects.frostShield.id.toString(), 8, 0);
        shield.action.status_effect.show_particles = false;
        spell.impacts = List.of(shield);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        configureFrostRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 30);

        return new Entry(id, spell, "", "").book(Book.FROST);
    }

    public static Entry frost_blizzard = add(frost_blizzard());
    private static Entry frost_blizzard() {
        var id = Identifier.of(WizardsMod.ID, "frost_blizzard");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FROST;
        spell.tier = 4;
        spell.range = 32;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.channel(spell, 8, 12);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_sky_charge");
        spell.active.cast.sound = new Sound(WizardsSounds.FROST_BLIZZARD_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { frostCastingParticles() };

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = true;

        spell.deliver.type = Spell.Delivery.Type.METEOR;
        spell.deliver.meteor = new Spell.Delivery.Meteor();
        spell.deliver.meteor.launch_radius = 3;
        spell.deliver.meteor.launch_properties.velocity = 1F;
        spell.deliver.meteor.launch_properties.extra_launch_count = 3;
        spell.deliver.meteor.launch_properties.extra_launch_delay = 4;

        var projectile = new Spell.ProjectileData();
        projectile.divergence = 8;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 3, 0, 0.1F, 0),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.FROST,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0.1F, 0.2F, 0)
                        .color(FROST_COLOR.toRGBA())
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:spell_projectile/frost_shard";
        projectile.client_data.model.scale = 0.8F;
        spell.deliver.meteor.projectile = projectile;

        spell.release = new Spell.Release();
        spell.release.sound = new Sound(WizardsSounds.FIRE_BREATH_RELEASE.id());

        var damage = SpellBuilder.Impacts.damage(0.7F, 0.2F);
        damage.sound = new Sound(SpellEngineSounds.GENERIC_FROST_IMPACT.id());

        var slowness = SpellBuilder.Impacts.effectAdd(WizardsEffects.frostSlowness.id.toString(), 3, 0, 1);
        slowness.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        slowness.action.status_effect.apply_limit.health_base = 80;
        slowness.action.status_effect.apply_limit.spell_power_multiplier = 4;
        slowness.action.status_effect.show_particles = false;
        slowness.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.FROST,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.2F, 0.7F)
                        .color(FROST_COLOR.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5, 0.1F, 0.4F)
        };

        spell.impacts = List.of(damage, slowness);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 3;
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.area_impact.particles = new ParticleBatch[] {
                new ParticleBatch(SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        20, 0.1F, 0.3F),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.FROST,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.2F, 0.4F)
                        .color(FROST_COLOR.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.frost_shard.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.2F, 0.4F)
        };
        spell.area_impact.sound = Sound.withVolume(WizardsSounds.FROST_SHARD_IMPACT.id(), 1.5F);

        SpellBuilder.Cost.exhaust(spell, 0.4F);
        configureFrostRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 16);
        spell.cost.cooldown.proportional = true;

        return new Entry(id, spell, "", "").book(Book.FROST);
    }
}
