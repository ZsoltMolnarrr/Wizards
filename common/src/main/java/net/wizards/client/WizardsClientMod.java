package net.wizards.client;

import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.CustomModels;
import net.wizards.WizardsMod;
import net.wizards.client.effect.FrostShieldRenderer;
import net.wizards.client.effect.FrozenRenderer;
import net.wizards.effect.ArcaneChargeRenderer;
import net.wizards.effect.Effects;

import java.util.List;

public class WizardsClientMod {
    public static void initialize() {
        CustomModels.registerModelIds(List.of(
                new Identifier(WizardsMod.ID, "projectile/arcane_missile"),
                new Identifier(WizardsMod.ID, "projectile/fireball_projectile"),
                new Identifier(WizardsMod.ID, "projectile/fire_meteor"),
                new Identifier(WizardsMod.ID, "projectile/frostbolt_projectile"),
                new Identifier(WizardsMod.ID, "projectile/ice_lance_projectile"),
                ArcaneChargeRenderer.modelId,
                FrozenRenderer.modelId,
                FrostShieldRenderer.modelId_base,
                FrostShieldRenderer.modelId_overlay
        ));
        CustomModelStatusEffect.register(Effects.frozen, new FrozenRenderer());
        CustomModelStatusEffect.register(Effects.arcaneCharge, new ArcaneChargeRenderer());
        CustomParticleStatusEffect.register(Effects.frozen, new FrozenRenderer());
        CustomModelStatusEffect.register(Effects.frostShield, new FrostShieldRenderer());
    }
}
