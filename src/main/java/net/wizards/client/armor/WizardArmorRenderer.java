package net.wizards.client.armor;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

public class WizardArmorRenderer extends AzArmorRenderer {
    public static WizardArmorRenderer wizard() {
        return new WizardArmorRenderer("wizard_robes", "wizard_robe");
    }
    public static WizardArmorRenderer arcane() {
        return new WizardArmorRenderer("wizard_robes", "arcane_robe");
    }
    public static WizardArmorRenderer fire() {
        return new WizardArmorRenderer("wizard_robes", "fire_robe");
    }
    public static WizardArmorRenderer frost() {
        return new WizardArmorRenderer("wizard_robes", "frost_robe");
    }
    public static WizardArmorRenderer netheriteArcane() {
        return new WizardArmorRenderer("wizard_robes", "netherite_arcane_robe");
    }
    public static WizardArmorRenderer netheriteFire() {
        return new WizardArmorRenderer("wizard_robes", "netherite_fire_robe");
    }
    public static WizardArmorRenderer netheriteFrost() {
        return new WizardArmorRenderer("wizard_robes", "netherite_frost_robe");
    }

    public WizardArmorRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(WizardsMod.ID, "geo/" + modelName + ".geo.json"),
                Identifier.of(WizardsMod.ID, "textures/armor/" + textureName + ".png")
        ).build());
    }
}