package net.wizards.content;

import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ModelEffect;
import net.spell_engine.api.spell.fx.ModelEffectBuilder;
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
import net.wizards.entity.WizardSummons;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WizardSpells {
    public enum WeaponGroup { WIZARD_STAFF, ARCANE_STAFF, FIRE_STAFF, FROST_STAFF }
    public enum Book {
        ARCANE("Tome of Arcane", "Arcane Spell Scroll",
                "Spell Book of Arcane Wizards, using pure energy to focus powerful attacks on single targets\n- Strengths: Focused magical damage against individual enemies\n- Weaknesses: Very low defense\n- Equipment: Lightly armored"),
        FIRE("Tome of Fire", "Fire Spell Scroll",
                "Spell Book of Fire Wizards, using fiery magic to defeat enemies both near and far\n- Strengths: Magical damage dealt at large areas\n- Weaknesses: Low defense and mobility\n- Equipment: Lightly armored"),
        FROST("Tome of Frost", "Frost Spell Scroll",
                "Spell Book of Frost Wizards, using cold magic to slow enemies and control the battlefield\n- Strengths: Magical damage that slows and freezes enemies\n- Weaknesses: Low defense and mobility\n- Equipment: Lightly armored");

        /** Display name of the generated spell book item. Source for {@code item.wizards.spell_book/<book>}. */
        public final String bookName;
        /** Display name of the generated spell scroll item. Source for {@code item.wizards.spell_scroll/<book>}. */
        public final String scrollName;
        /** Spell binding tooltip. Source for {@code item.wizards.spell_book/<book>.spell_binding.description}. */
        public final String bindingDescription;
        Book(String bookName, String scrollName, String bindingDescription) {
            this.bookName = bookName;
            this.scrollName = scrollName;
            this.bindingDescription = bindingDescription;
        }
    }
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
    private static final Color ARCANE_COLOR_LIGHT = Color.from(0xFF99FF);
    private static final Color ARCANE_COLOR_VERY_LIGHT = Color.from(0xFFCCFF);
    private static final Color FIRE_COLOR = Color.from(SpellSchools.FIRE.color);
    private static final Color FROST_COLOR = Color.from(SpellSchools.FROST.color);

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
        var name = "Arcane Bolt";
        var description = "Shoots a bolt of energy, causing {damage} arcane spell damage.";
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.ARCANE;
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

        SpellBuilder.Cost.item(spell, "runes:arcane_stone");

        return new Entry(id, spell, name, description);
    }

    public static Entry arcane_blast = add(arcane_blast());
    private static Entry arcane_blast() {
        var id = Identifier.of(WizardsMod.ID, "arcane_blast");
        var name = "Arcane Blast";
        var description = "Blasts the target, causing {damage} arcane damage. Grants Arcane Charge, stacking up to {effect_amplifier_cap} times.";
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 1;
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

        SpellBuilder.Cost.item(spell, "runes:arcane_stone");

        return new Entry(id, spell, name, description).weaponGroup(WeaponGroup.ARCANE_STAFF).weaponGroup(WeaponGroup.WIZARD_STAFF);
    }

    public static Entry arcane_missile = add(arcane_missile());
    private static Entry arcane_missile() {
        var id = Identifier.of(WizardsMod.ID, "arcane_missile");
        var name = "Arcane Missiles";
        var description = "Continuously shoots bolts of energy piercing thru {pierce} targets, causing {damage} arcane damage every second.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 2;
        spell.order = 1;
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

        SpellBuilder.Cost.item(spell, "runes:arcane_stone");
        SpellBuilder.Cost.cooldown(spell, 2);
        spell.cost.cooldown.proportional = true;

        return new Entry(id, spell, name, description).book(Book.ARCANE);
    }

    public static Entry arcane_explosion = add(arcane_explosion());
    private static Entry arcane_explosion() {
        var id = Identifier.of(WizardsMod.ID, "arcane_explosion");
        var name = "Arcane Explosion";
        var description = "Creates a magical explosion around you, causing {damage} arcane damage to nearby enemies.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 2;
        spell.order = 2;
        spell.range = 6;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_area_charge");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_ARCANE_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { arcaneCastingParticles() };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_area_release");
        spell.release.sound = new Sound(WizardsSounds.ARCANE_EXPLOSION_RELEASE.id());
        spell.release.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        80, 0.7F, 0.7F)
                        .color(ARCANE_COLOR_LIGHT.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        80, 0.7F, 0.7F)
                        .color(ARCANE_COLOR.toRGBA()).preSpawnTravel(2)
        };
        spell.release.particles_scaled_with_ranged = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.area_effect_574.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1, 0, 0)
                        .scale(0.8F)
                        .color(ARCANE_COLOR_LIGHT.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.aura_effect_574.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1, 0, 0)
                        .scale(0.8F)
                        .color(ARCANE_COLOR_LIGHT.toRGBA())
        };

        var damage = SpellBuilder.Impacts.damage(0.9F, 0.8F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPELL,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        30, 0.2F, 0.7F)
                        .color(ARCANE_COLOR.toRGBA())
        };
        damage.sound = new Sound(WizardsSounds.ARCANE_BLAST_IMPACT.id());

        spell.impacts = List.of(damage);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.item(spell, "runes:arcane_stone");
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, name, description).book(Book.ARCANE);
    }

    public static Entry arcane_beam = add(arcane_beam());
    private static Entry arcane_beam() {
        var id = Identifier.of(WizardsMod.ID, "arcane_beam");
        var name = "Arcane Beam";
        var description = "Channels a beam of energy, dealing {damage} arcane damage every second. Consumes all Arcane Charges.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 3;
        spell.order = 1;
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
        SpellBuilder.Cost.item(spell, "runes:arcane_stone");
        SpellBuilder.Cost.cooldown(spell, 10);
        spell.cost.cooldown.proportional = true;

        return new Entry(id, spell, name, description).book(Book.ARCANE);
    }

    public static Entry arcane_barrage = add(arcane_barrage());
    private static Entry arcane_barrage() {
        var name = "Arcane Barrage";
        var description = "TODO.";
        var id = Identifier.of(WizardsMod.ID, "arcane_barrage");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 3;
        spell.order = 2;
        spell.range = 16;

        spell.learn = new Spell.Learn();

        var impact = new Spell.Impact();
        impact.action = new Spell.Impact.Action();
        impact.action.type = Spell.Impact.Action.Type.SUMMON;
        impact.action.summon = WizardSummons.arcaneEmitter();
        spell.impacts = List.of(impact);

        return new Entry(id, spell, name, description).book(Book.ARCANE);
    }

    public static Entry arcane_evocation = add(arcane_evocation());
    private static Entry arcane_evocation() {
        var id = Identifier.of(WizardsMod.ID, "arcane_evocation");
        var name = "Evocation";
        var description = "Channel to gain Evocation effect, stacking up to {effect_amplifier_cap}, lasting {effect_duration} seconds. Each stack increases spell critical strike chance and spell haste by {bonus_1}, but also increases any damage you take by {bonus_3}.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 4;
        spell.order = 2;
        spell.range = 0;

        spell.learn = new Spell.Learn();

        var stacks = 10;
        var effect = WizardsEffects.evocation;

        SpellBuilder.Casting.channel(spell, 5, stacks);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_levitate_channel");
        spell.active.cast.movement_speed = 0F;
        spell.active.cast.start_sound = new Sound(WizardsSounds.ARCANE_EVOCATION_START.id());
        spell.active.cast.sound = new Sound(WizardsSounds.ARCANE_EVOCATION_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        8, 0.2F, 0.3F)
                        .preSpawnTravel(6)
                        .invert()
                        .color(ARCANE_COLOR_LIGHT.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        8, 0.2F, 0.3F)
                        .preSpawnTravel(6)
                        .invert()
                        .color(ARCANE_COLOR.toRGBA())
        };

        spell.release = new Spell.Release();
        spell.release.sound = new Sound(WizardsSounds.ARCANE_EVOCATION_RELEASE.id());

        spell.target.type = Spell.Target.Type.CASTER;

        var evocationEffect = SpellBuilder.Impacts.effectAdd(effect.id.toString(), 10, 1, stacks-1);
        spell.impacts = List.of(evocationEffect);

        SpellBuilder.Cost.exhaust(spell, 0.1F);
        SpellBuilder.Cost.item(spell, "runes:arcane_stone");
        SpellBuilder.Cost.cooldown(spell, 45);
        spell.cost.cooldown.proportional = true;

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var values = effect.config.attributes().stream().map(m ->
                SpellTooltip.bonus(m.value, m.operation)
            ).toList();
            return SpellTooltip.replaceTokens(args.description(), "bonus", values);
        };
        return new Entry(id, spell, name, description).book(Book.ARCANE).mutator(mutator);
    }

    public static Entry arcane_blink = add(arcane_blink());
    private static Entry arcane_blink() {
        var id = Identifier.of(WizardsMod.ID, "arcane_blink");
        var name = "Blink";
        var description = "Teleports you forwards for {teleport_distance} blocks.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.tier = 4;
        spell.order = 1;
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
        SpellBuilder.Cost.item(spell, "runes:arcane_stone");
        SpellBuilder.Cost.cooldown(spell, 12);

        return new Entry(id, spell, name, description).book(Book.ARCANE);
    }

    public static Entry fire_scorch = add(fire_scorch());
    private static Entry fire_scorch() {
        var id = Identifier.of(WizardsMod.ID, "fire_scorch");
        var name = "Scorch";
        var description = "Scorches the target, causing {damage} fire spell damage and setting it on fire.";
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FIRE;
        spell.tier = 0;
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

        return new Entry(id, spell, name, description);
    }

    public static Entry fireball = add(fireball());
    private static Entry fireball() {
        var id = Identifier.of(WizardsMod.ID, "fireball");
        var name = "Fireball";
        var description = "Launches an ball of fire, causing up to {damage} fire spell and setting the target on fire.";
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FIRE;
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

        return new Entry(id, spell, name, description);
    }

    public static Entry fire_blast = add(fire_blast());
    private static Entry fire_blast() {
        var id = Identifier.of(WizardsMod.ID, "fire_blast");
        var name = "Pyroblast";
        var description = "Launches an explosive ball of fire, causing up to {damage} fire spell damage in {impact_range} blocks radius.";
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FIRE;
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

        return new Entry(id, spell, name, description).weaponGroup(WeaponGroup.FIRE_STAFF).weaponGroup(WeaponGroup.WIZARD_STAFF);
    }

    public static Entry fire_breath = add(fire_breath());
    private static Entry fire_breath() {
        var id = Identifier.of(WizardsMod.ID, "fire_breath");
        var name = "Fire Breath";
        var description = "Incinerates targets in front, dealing up to {damage} fire spell damage every second.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FIRE;
        spell.tier = 2;
        spell.order = 1;
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

        return new Entry(id, spell, name, description).book(Book.FIRE);
    }

    public static Entry fire_slash = add(fire_slash());
    private static Entry fire_slash() {
        var id = Identifier.of(WizardsMod.ID, "fire_slash");
        var name = "Flame Slash";
        var description = "Launches a wide slash of fiery wave, causing up to {damage} fire spell damage in front.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FIRE;
        spell.tier = 2;
        spell.order = 2;
        spell.range = 16;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.instant(spell);

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_area_release");
        spell.release.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_RELEASE.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 0.5F;

        var projectile = new Spell.ProjectileData();
        projectile.perks.pierce = 9999;
        projectile.hitbox = new Spell.ProjectileData.HitBox(3F, 0.25F);
        projectile.hitbox.length = 1F;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 6, 0.15F, 0.2F, 0),
                new ParticleBatch(
                        "smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 3, 0.15F, 0.2F, 0)
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:spell_projectile/fire_wave";
        projectile.client_data.model.rotate_degrees_per_tick = 0;
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.8F, 0.8F);
        damage.particles = fireImpactParticles();
        damage.sound = new Sound(WizardsSounds.FIRE_SCORCH_IMPACT.id());

        var fire = SpellBuilder.Impacts.fire(3);
        spell.impacts = List.of(damage, fire);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        configureFireRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 8);

        return new Entry(id, spell, name, description).book(Book.FIRE);
    }

    public static Entry fire_meteor = add(fire_meteor());
    private static Entry fire_meteor() {
        var id = Identifier.of(WizardsMod.ID, "fire_meteor");
        var name = "Meteor";
        var description = "Crashes a meteors on the target, each causing up to {damage} fire spell damage within {impact_range} blocks.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FIRE;
        spell.tier = 3;
        spell.order = 1;
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

        return new Entry(id, spell, name, description).book(Book.FIRE);
    }

    public static Entry firestorm = add(firestorm());
    private static Entry firestorm() {
        var id = Identifier.of(WizardsMod.ID, "fire_storm");
        var name = "Firestorm";
        var description = "Incinerates targets around you, dealing up to {damage} fire spell damage every second.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FIRE;
        spell.tier = 3;
        spell.order = 2;
        spell.range = 4;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.channel(spell, 5, 4);
        spell.active.cast.channel.release_fx = true;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
        spell.active.cast.start_sound = new Sound(WizardsSounds.FIRE_BREATH_START.id());
        spell.active.cast.sound = new Sound(WizardsSounds.FIRE_BREATH_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { fireCastingParticles() };
        spell.active.cast.movement_speed = 1F;


        spell.release = new Spell.Release();
        spell.release.sound = Sound.withVolume(WizardsSounds.FIREBALL_IMPACT.id(), 1.2F);
        spell.release.particles = new ParticleBatch[] {
                new ParticleBatch("lava",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.GROUND,
                        90, 1.5F, 5F),
                new ParticleBatch(
                        SpellEngineParticles.fire_explosion.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        6, 0.5F, 0.3F)
        };
        spell.release.particles_scaled_with_ranged = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.area_effect_748.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1, 0, 0)
                        .scale(0.8F)
                        .color(0xFF4400FFL),
        };
        spell.release.model_fx = ModelEffectBuilder.forEach(
                ModelEffectBuilder.Preset.orbiters(
                        "wizards:spell_projectile/fire_wave", 3, 2.0F, -360F, 20, ModelEffect.Easing.EASE_IN_OUT_CUBIC),
                e -> { e
                        .initialScale(0)
                        .scaleIn(0, 5, ModelEffect.Easing.EASE_IN_OUT_CUBIC)
                        .translate(0, 0, 0.5F, 0, 5, ModelEffect.Easing.EASE_IN_CUBIC)
                        .scaleOut(15, 20, ModelEffect.Easing.EASE_IN_OUT_CUBIC)
                        .translate(0, 0,-0.5F, 15, 20, ModelEffect.Easing.EASE_IN_CUBIC)
                    ;
                }
        );


        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.target.area.vertical_range_multiplier = 0.5F;

        var damage = SpellBuilder.Impacts.damage(1.5F, 1.2F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.fire_explosion.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        3, 0.2F, 0.3F)
        };
        damage.sound = new Sound(WizardsSounds.FIRE_BREATH_IMPACT.id());
        spell.impacts = List.of(damage);

        SpellBuilder.Cost.exhaust(spell, 0.4F);
        configureFireRuneCost(spell);
        SpellBuilder.Cost.cooldown(spell, 20);
        spell.cost.cooldown.proportional = true;

        return new Entry(id, spell, name, description).book(Book.FIRE);
    }

    public static Entry fire_wall = add(fire_wall());
    private static Entry fire_wall() {
        var id = Identifier.of(WizardsMod.ID, "fire_wall");
        var name = "Wall of Flames";
        var description = "Creates a wall of fire, lasting {cloud_duration} seconds, dealing up to {damage} fire spell damage continuously to enemies passing thru.";

        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 4;
        spell.order = 1;
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

        // A row of 5 fire clouds, 2 blocks apart, 2 blocks in front of the caster, laid out left to
        // right (matching the release animation). The leftmost ignites immediately; the rest follow
        // 4 ticks later.
        var wall = SpellBuilder.Placements.line(5, 2F, 2F, SpellBuilder.Placements.LineOrder.LEFT_TO_RIGHT, SpellBuilder.Placements.template());
        for (int i = 1; i < wall.size(); i++) {
            wall.get(i).delay_ticks = 4;
        }
        cloud.placement = wall.get(0);
        cloud.additional_placements = List.copyOf(wall.subList(1, wall.size()));

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

    public static Entry fire_hydra = add(fire_hydra());
    private static Entry fire_hydra() {
        var name = "Fire Hydra";
        var description = "Conjure fire hydra heads...";
        var id = Identifier.of(WizardsMod.ID, "fire_hydra");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FIRE;
        spell.tier = 4;
        spell.order = 2;
        spell.range = 16;

        spell.learn = new Spell.Learn();

        var impact = new Spell.Impact();
        impact.action = new Spell.Impact.Action();
        impact.action.type = Spell.Impact.Action.Type.SUMMON;
        impact.action.summon = WizardSummons.fireHydra();
        spell.impacts = List.of(impact);

        return new Entry(id, spell, name, description).book(Book.FIRE);
    }

    public static Entry frost_shard = add(frost_shard());
    private static Entry frost_shard() {
        var id = Identifier.of(WizardsMod.ID, "frost_shard");
        var name = "Frost Shard";
        var description = "Launches a frost shard that may bounce of walls, causing {damage} frost spell damage on impact.";
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FROST;
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

        return new Entry(id, spell, name, description);
    }

    public static Entry frostbolt = add(frostbolt());
    private static Entry frostbolt() {
        var id = Identifier.of(WizardsMod.ID, "frostbolt");
        var name = "Frostbolt";
        var description = "Launches a ball of frost ricocheting to {ricochet} additional nearby targets, causing {damage} frost spell damage and slowing the target on impact.";
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FROST;
        spell.tier = 1;
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

        return new Entry(id, spell, name, description).weaponGroup(WeaponGroup.FROST_STAFF).weaponGroup(WeaponGroup.WIZARD_STAFF);
    }

    public static Entry frost_nova = add(frost_nova());
    private static Entry frost_nova() {
        var id = Identifier.of(WizardsMod.ID, "frost_nova");
        var name = "Frost Nova";
        var description = "Freezes targets around you for {effect_duration} seconds, causing {damage} frost spell damage and blocking their movement. Frozen targets are vulnerable to frost magic.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FROST;
        spell.tier = 2;
        spell.order = 1;
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

        return new Entry(id, spell, name, description).book(Book.FROST);
    }

    public static Entry frost_shield = add(frost_shield());
    private static Entry frost_shield() {
        var id = Identifier.of(WizardsMod.ID, "frost_shield");
        var name = "Frost Shield";
        var description = "Protects you from attacks, projectiles and fire for {effect_duration} seconds, but also slows down your movement.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FROST;
        spell.tier = 3;
        spell.order = 1;
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

        return new Entry(id, spell, name, description).book(Book.FROST);
    }

    public static Entry ice_lance = add(ice_lance());
    private static Entry ice_lance() {
        var name = "Ice Lance";
        var description = "Launches lance of ice...";
        var id = Identifier.of(WizardsMod.ID, "ice_lance");
        var spell = SpellBuilder.createWeaponSpell();
        spell.school = SpellSchools.FROST;
        spell.tier = 3;
        spell.range = 64;
        spell.order = 2;

        spell.learn = new Spell.Learn();

        // Charged cast: the longer it is held, the harder it hits, the bigger/faster the lance,
        // and the further it flies (the charge bonus is scaled by the curved release ratio).
        var charge = SpellBuilder.Casting.charge(spell, 1.5F, Spell.Active.Cast.Charge.Curve.EASE_IN_QUART);
        charge.min_release_ratio = 0.2F;
        var bonus = charge.bonus;
        bonus.power_modifier = new Spell.Impact.Modifier();
        bonus.power_modifier.power_multiplier = 1.5F;   // up to +150% impact power at full charge
        bonus.projectile_scale_multiply = 1.0F;         // up to 2x projectile render + hitbox size
        bonus.projectile_launch = new Spell.LaunchProperties();
        bonus.projectile_launch.velocity = 0.8F;        // faster projectile at full charge
        bonus.range_add = 32F;                          // flies further at full charge

        spell.active.cast.animation = PlayerAnimation.of("spell_engine:weapon_spearthrow_ready");
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_FROST_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[] { frostCastingParticles() };

        spell.release = new Spell.Release();
        spell.release.pitch_shift = 0.75F;
        spell.release.sound = new Sound(SpellEngineSounds.GENERIC_FROST_RELEASE.id());
        spell.release.animation = PlayerAnimation.of("spell_engine:weapon_spearthrow_toss");

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.2F;
        // spell.deliver.projectile.launch_properties.sound = new Sound(SpellEngineSounds.GENERIC_FROST_RELEASE.id());

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 2F;
        projectile.perks.pierce = 1; // a lance pierces (no ricochet/bounce, unlike Frostbolt)
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
        projectile.client_data.model.model_id = "wizards:spell_projectile/frostbolt"; // same model for now
        projectile.client_data.model.scale = 0.5F;
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(1.0F, 1.5F);
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

        return new Entry(id, spell, name, description).book(Book.FROST);
    }

    public static Entry frost_blizzard = add(frost_blizzard());
    private static Entry frost_blizzard() {
        var id = Identifier.of(WizardsMod.ID, "frost_blizzard");
        var name = "Blizzard";
        var description = "Channels a rain of frost shards down onto your target and nearby enemies, dealing up to {damage} frost spell damage slowing the target, in {impact_range} blocks radius.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FROST;
        spell.tier = 4;
        spell.order = 1;
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

        return new Entry(id, spell, name, description).book(Book.FROST);
    }

    public static Entry frost_elemental = add(frost_elemental());
    private static Entry frost_elemental() {
        var name = "Frost Elemental";
        var description = "Summons a frost elemental to fight for you.";
        var id = Identifier.of(WizardsMod.ID, "frost_elemental");
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FROST;
        spell.tier = 4;
        spell.order = 2;
        spell.range = 16;

        spell.learn = new Spell.Learn();

        var impact = new Spell.Impact();
        impact.action = new Spell.Impact.Action();
        impact.action.type = Spell.Impact.Action.Type.SUMMON;
        impact.action.summon = WizardSummons.frostElemental();
        spell.impacts = List.of(impact);

        return new Entry(id, spell, name, description).book(Book.FROST);
    }
}
