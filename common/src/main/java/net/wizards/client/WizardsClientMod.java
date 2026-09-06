package net.wizards.client;

import net.minecraft.util.Identifier;
import net.rpg_foundation.armor_api.client.ArmorRenderers;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.render.ModelFxEffectRenderer;
import net.spell_engine.api.spell.fx.Easing;
import net.spell_engine.api.spell.fx.ModelEffect;
import net.spell_engine.rpg_series.item.Armor;
import net.wizards.WizardsMod;
import net.wizards.client.armor.WizardArmorRenderer;
import net.wizards.client.effect.*;
import net.wizards.content.WizardSpells;
import net.wizards.effect.WizardsEffects;
import net.wizards.item.WizardArmors;

import java.util.List;

public class WizardsClientMod {
    public static void init() {
        CustomModelStatusEffect.register(WizardsEffects.arcaneCharge.effect, new ArcaneChargeRenderer());
        CustomParticleStatusEffect.register(WizardsEffects.frostSlowness.effect, new FrozenParticles(1));
        CustomParticleStatusEffect.register(WizardsEffects.frozen.effect, new FrozenParticles(2));
        CustomModelStatusEffect.register(WizardsEffects.frozen.effect, frozenModelFxRenderer());
        CustomModelStatusEffect.register(WizardsEffects.frostShield.effect, new FrostShieldRenderer());
        registerArmorRenderer(WizardArmors.wizardRobeSet, WizardArmorRenderer.wizard());
        registerArmorRenderer(WizardArmors.arcaneRobeSet, WizardArmorRenderer.arcane());
        registerArmorRenderer(WizardArmors.fireRobeSet, WizardArmorRenderer.fire());
        registerArmorRenderer(WizardArmors.frostRobeSet, WizardArmorRenderer.frost());
        registerArmorRenderer(WizardArmors.netherite_arcane, WizardArmorRenderer.netheriteArcane());
        registerArmorRenderer(WizardArmors.netherite_fire, WizardArmorRenderer.netheriteFire());
        registerArmorRenderer(WizardArmors.netherite_frost, WizardArmorRenderer.netheriteFrost());

        CustomParticleStatusEffect.register(WizardsEffects.evocation.effect, new EvocationParticles());

        // Fire Hydra rendering is deferred to the world's after-translucent pass (so its translucent body
        // is not occluded by water, clouds or distant terrain) via FireHydraRenderer.renderAfterTranslucent,
        // which each platform's client entrypoint wires to its own loader-native render event.
    }

    private static ModelFxEffectRenderer frozenModelFxRenderer() {
        var translateInitial = new ModelEffect.Transform();
        translateInitial.operation = "translate";
        translateInitial.y = 0.5F;

        var scaleInitial = new ModelEffect.Transform();
        scaleInitial.operation = "scale";
        scaleInitial.x = -1F; scaleInitial.y = -1F; scaleInitial.z = -1F;

        var scaleUp = new ModelEffect.Animation();
        scaleUp.operation = "scale";
        scaleUp.start = 0; scaleUp.end = 40;
        scaleUp.x = 1F; scaleUp.y = 1F; scaleUp.z = 1F;
        scaleUp.easing = Easing.EASE_OUT_BACK;

        var effect = new ModelEffect();
        effect.model_id = new Identifier(WizardsMod.ID, "spell_effect/frost_trap").toString();
        effect.light_emission = LightEmission.GLOW_TRANSLUCENT;
        effect.duration = 40;
        effect.initial = List.of(translateInitial, scaleInitial);
        effect.animations = List.of(scaleUp);

        return new ModelFxEffectRenderer(List.of(effect), ModelFxEffectRenderer.Playback.ONCE)
                .entityScaling(ModelFxEffectRenderer.SizeAxis.WIDTH, 0.5F);
    }

    private static void registerArmorRenderer(Armor.Set set, GeoArmorRenderer renderer) {
        ArmorRenderers.register(renderer, set.head, set.chest, set.legs, set.feet);
    }
}
