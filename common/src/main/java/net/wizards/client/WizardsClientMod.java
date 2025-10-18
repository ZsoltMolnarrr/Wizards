package net.wizards.client;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererRegistry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.render.CustomModels;
import net.wizards.WizardsMod;
import net.wizards.client.armor.WizardArmorRenderer;
import net.wizards.client.effect.FrostShieldRenderer;
import net.wizards.client.effect.FrozenParticles;
import net.wizards.client.effect.FrozenRenderer;
import net.wizards.client.effect.ArcaneChargeRenderer;
import net.wizards.effect.WizardsEffects;
import net.wizards.item.WizardArmors;

import java.util.List;
import java.util.function.Supplier;

public class WizardsClientMod {
    public static void init() {
        CustomModels.registerModelIds(List.of(
                Identifier.of(WizardsMod.ID, "projectile/arcane_bolt"),
                Identifier.of(WizardsMod.ID, "projectile/arcane_missile"),
                Identifier.of(WizardsMod.ID, "projectile/fireball"),
                Identifier.of(WizardsMod.ID, "projectile/fire_blast"),
                Identifier.of(WizardsMod.ID, "projectile/fire_meteor"),
                Identifier.of(WizardsMod.ID, "projectile/frost_shard"),
                Identifier.of(WizardsMod.ID, "projectile/frostbolt"),
                ArcaneChargeRenderer.modelId,
                FrozenRenderer.modelId,
                FrostShieldRenderer.modelId_base,
                FrostShieldRenderer.modelId_overlay
        ));

        CustomModelStatusEffect.register(WizardsEffects.arcaneCharge.effect, new ArcaneChargeRenderer());
        CustomParticleStatusEffect.register(WizardsEffects.frostSlowness.effect, new FrozenParticles(1));
        CustomParticleStatusEffect.register(WizardsEffects.frozen.effect, new FrozenParticles(2));
        CustomModelStatusEffect.register(WizardsEffects.frozen.effect, new FrozenRenderer());
        CustomModelStatusEffect.register(WizardsEffects.frostShield.effect, new FrostShieldRenderer());
        registerArmorRenderer(WizardArmors.wizardRobeSet, WizardArmorRenderer::wizard);
        registerArmorRenderer(WizardArmors.arcaneRobeSet, WizardArmorRenderer::arcane);
        registerArmorRenderer(WizardArmors.fireRobeSet, WizardArmorRenderer::fire);
        registerArmorRenderer(WizardArmors.frostRobeSet, WizardArmorRenderer::frost);
        registerArmorRenderer(WizardArmors.netherite_arcane, WizardArmorRenderer::netheriteArcane);
        registerArmorRenderer(WizardArmors.netherite_fire, WizardArmorRenderer::netheriteFire);
        registerArmorRenderer(WizardArmors.netherite_frost, WizardArmorRenderer::netheriteFrost);
    }

    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
}
