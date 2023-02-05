package net.wizards.client;

import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.CustomModels;
import net.wizards.WizardsMod;
import net.wizards.client.armor.WizardArmorRenderer;
import net.wizards.client.effect.FrostShieldRenderer;
import net.wizards.client.effect.FrozenRenderer;
import net.wizards.effect.Effects;
import net.wizards.item.Armors;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.List;

public class WizardsClientMod {
    public static void initialize() {
        var wizardArmorRenderer = new WizardArmorRenderer();
        for (var entry: Armors.entries) {
            GeoArmorRenderer.registerArmorRenderer(wizardArmorRenderer,
                    entry.armorSet().head,
                    entry.armorSet().chest,
                    entry.armorSet().legs,
                    entry.armorSet().feet);
        }
        CustomModels.registerModelIds(List.of(
                new Identifier(WizardsMod.ID, "projectile/arcane_missile"),
                new Identifier(WizardsMod.ID, "projectile/fireball_projectile"),
                new Identifier(WizardsMod.ID, "projectile/fire_meteor"),
                new Identifier(WizardsMod.ID, "projectile/frostbolt_projectile"),
                FrozenRenderer.modelId,
                FrostShieldRenderer.modelId_base,
                FrostShieldRenderer.modelId_overlay
        ));
        CustomModelStatusEffect.register(Effects.frozen, new FrozenRenderer());
        CustomParticleStatusEffect.register(Effects.frozen, new FrozenRenderer());
        CustomModelStatusEffect.register(Effects.frostShield, new FrostShieldRenderer());
    }
}
