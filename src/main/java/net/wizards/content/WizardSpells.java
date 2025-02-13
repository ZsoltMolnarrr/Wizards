package net.wizards.content;

import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_power.api.SpellSchools;
import net.wizards.WizardsMod;
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

    private static Spell activeSpellBase() {
        var spell = new Spell();
        spell.type = Spell.Type.ACTIVE;
        spell.active = new Spell.Active();
        spell.active.cast = new Spell.Active.Cast();
        return spell;
    }

    private static ParticleBatch arcaneCastingParticles() {
        return new ParticleBatch(
                SpellEngineParticles.getMagicParticleVariant(
                        SpellEngineParticles.ARCANE,
                        SpellEngineParticles.MagicParticleFamily.Shape.SPELL,
                        SpellEngineParticles.MagicParticleFamily.Motion.ASCEND
                ).id().toString(),
                ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                1, 0.05F, 0.1F);
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
                        SpellEngineParticles.getMagicParticleVariant(
                                SpellEngineParticles.ARCANE,
                                SpellEngineParticles.MagicParticleFamily.Shape.SPELL,
                                SpellEngineParticles.MagicParticleFamily.Motion.ASCEND
                        ).id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0.05F, 0.1F, 0.0F, 0F)
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "wizards:projectile/arcane_bolt";
        projectile.client_data.model.scale = 0.5F;
        spell.deliver.projectile.projectile = projectile;

        var damage = damage(0.7F, 0.6F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.getMagicParticleVariant(
                                SpellEngineParticles.ARCANE,
                                SpellEngineParticles.MagicParticleFamily.Shape.IMPACT,
                                SpellEngineParticles.MagicParticleFamily.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        null, 20, 0.2F, 0.7F, 0.0F, 0F)
        };
        damage.sound = new Sound(WizardsSounds.ARCANE_MISSILE_IMPACT.id());
        spell.impacts = List.of(damage);

        configureArcaneRuneCost(spell);

        return new Entry(id, spell, "", "", null);
    }

//    public static Entry arcane_missile = add(arcane_missile());
//    private static Entry arcane_missile() {
//        var id = Identifier.of(WizardsMod.ID, "arcane_missile");
//        var spell = activeSpellBase();
//        spell.school = SpellSchools.ARCANE;
//        spell.tier = 2;
//        spell.group = PRIMARY_GROUP;
//        spell.range = 64;
//
//        spell.active.cast.duration = 4;
//        spell.active.cast.channel_ticks = 6;
//        spell.active.cast.animation = "spell_engine:two_handed_channeling";
//        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_ARCANE_CASTING.id(), 0);
//        spell.active.cast.particles = new ParticleBatch[] { arcaneCastingParticles() };
//
//        spell.release = new Spell.Release();
//        spell.release.animation = "spell_engine:one_handed_projectile_release";
//        spell.release.sound = new Sound(WizardsSounds.ARCANE_MISSILE_RELEASE.id());
//
//        spell.target.type = Spell.Target.Type.AIM;
//        spell.target.aim = new Spell.Target.Aim();
//
//        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
//        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
//        var projectile = new Spell.ProjectileData();
//        projectile.homing_angle = 1F;
//        projectile.client_data = new Spell.ProjectileData.Client();
//        projectile.client_data.light_level = 10;
//        projectile.client_data.travel_particles = new ParticleBatch[] {
//                new ParticleBatch(
//                        SpellEngineParticles.getMagicParticleVariant(
//                                SpellEngineParticles.ARCANE,
//                                SpellEngineParticles.MagicParticleFamily.Shape.SPELL,
//                                SpellEngineParticles.MagicParticleFamily.Motion.ASCEND
//                        ).id().toString(),
//                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
//                        ParticleBatch.Rotation.LOOK, 1, 0.05F, 0.1F, 0.0F, 0F)
//        };
//        projectile.client_data.model = new Spell.ProjectileModel();
//        projectile.client_data.model.model_id = "wizards:projectile/arcane_missile";
//        projectile.client_data.model.scale = 0.5F;
//        spell.deliver.projectile.projectile = projectile;
//
//        var damage = damage(1.0F, 0.6F);
//        damage.particles = new ParticleBatch[] {
//                new ParticleBatch(
//                        SpellEngineParticles.getMagicParticleVariant(
//                                SpellEngineParticles.ARCANE,
//                                SpellEngineParticles.MagicParticleFamily.Shape
//        }
//    }
}
