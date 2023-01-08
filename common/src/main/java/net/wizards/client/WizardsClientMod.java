package net.wizards.client;

import net.minecraft.util.Identifier;
import net.spell_engine.api.client.CustomModels;
import net.spell_engine.api.status_effect.CustomModelStatusEffect;
import net.spell_engine.api.status_effect.CustomParticleStatusEffect;
import net.wizards.WizardsMod;
import net.wizards.effect.Effects;
import net.wizards.effect.client.FrostShieldRenderer;
import net.wizards.effect.client.FrozenRenderer;
import net.wizards.item.Armors;
import net.wizards.item.armor.client.WizardArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.List;

public class WizardsClientMod {
    public static void initialize() {
        GeoArmorRenderer.registerArmorRenderer(new WizardArmorRenderer(),
                Armors.wizardRobeSet.head,
                Armors.wizardRobeSet.chest,
                Armors.wizardRobeSet.legs,
                Armors.wizardRobeSet.feet);
        CustomModels.registerModelIds(List.of(
                new Identifier(WizardsMod.ID, "arcane_missile"),
                new Identifier(WizardsMod.ID, "fireball_projectile"),
                new Identifier(WizardsMod.ID, "frostbolt_projectile"),
                FrozenRenderer.modelId,
                FrostShieldRenderer.modelId_base,
                FrostShieldRenderer.modelId_overlay
        ));

        CustomModelStatusEffect.register(Effects.frozen, new FrozenRenderer());
        CustomParticleStatusEffect.register(Effects.frozen, new FrozenRenderer());
        CustomModelStatusEffect.register(Effects.frostShield, new FrostShieldRenderer());
    }
}
