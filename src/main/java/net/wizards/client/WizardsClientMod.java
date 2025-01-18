package net.wizards.client;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
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
import net.wizards.effect.Effects;
import net.wizards.item.Armors;

import java.util.List;
import java.util.function.Supplier;

public class WizardsClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
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

        CustomModelStatusEffect.register(Effects.arcaneCharge.effect, new ArcaneChargeRenderer());
        CustomParticleStatusEffect.register(Effects.frostSlowness.effect, new FrozenParticles(1));
        CustomParticleStatusEffect.register(Effects.frozen.effect, new FrozenParticles(2));
        CustomModelStatusEffect.register(Effects.frozen.effect, new FrozenRenderer());
        CustomModelStatusEffect.register(Effects.frostShield.effect, new FrostShieldRenderer());
        registerArmorRenderer(Armors.wizardRobeSet, WizardArmorRenderer::wizard);
        registerArmorRenderer(Armors.arcaneRobeSet, WizardArmorRenderer::arcane);
        registerArmorRenderer(Armors.fireRobeSet, WizardArmorRenderer::fire);
        registerArmorRenderer(Armors.frostRobeSet, WizardArmorRenderer::frost);
        registerArmorRenderer(Armors.netherite_arcane, WizardArmorRenderer::netheriteArcane);
        registerArmorRenderer(Armors.netherite_fire, WizardArmorRenderer::netheriteFire);
        registerArmorRenderer(Armors.netherite_frost, WizardArmorRenderer::netheriteFrost);
    }

    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
}
