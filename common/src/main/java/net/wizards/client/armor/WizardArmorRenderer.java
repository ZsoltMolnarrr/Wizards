package net.wizards.client.armor;

import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelibarmor.common.render.layer.AzArmorTrimLayer;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

public class WizardArmorRenderer extends AzArmorRenderer {
    private static final String TrimTextureT1 = "wizard_robe_generic";
    private static final String TrimTextureT2 = "spec_robe_generic";
    private static final String TrimTextureT3 = "netherite_spec_robe_generic";
    public static WizardArmorRenderer wizard() {
        return new WizardArmorRenderer("wizard_robes", "wizard_robe", TrimTextureT1);
    }
    public static WizardArmorRenderer arcane() {
        return new WizardArmorRenderer("wizard_robes", "arcane_robe", TrimTextureT2);
    }
    public static WizardArmorRenderer fire() {
        return new WizardArmorRenderer("wizard_robes", "fire_robe", TrimTextureT2);
    }
    public static WizardArmorRenderer frost() {
        return new WizardArmorRenderer("wizard_robes", "frost_robe", TrimTextureT2);
    }
    public static WizardArmorRenderer netheriteArcane() {
        return new WizardArmorRenderer("wizard_robes", "netherite_arcane_robe", TrimTextureT3);
    }
    public static WizardArmorRenderer netheriteFire() {
        return new WizardArmorRenderer("wizard_robes", "netherite_fire_robe", TrimTextureT3);
    }
    public static WizardArmorRenderer netheriteFrost() {
        return new WizardArmorRenderer("wizard_robes", "netherite_frost_robe", TrimTextureT3);
    }

    public WizardArmorRenderer(String modelName, String textureName, String trimTextureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(WizardsMod.ID, "geo/" + modelName + ".geo.json"),
                Identifier.of(WizardsMod.ID, "textures/armor/" + textureName + ".png"))
                .addRenderLayer(new AzArmorTrimLayer(Identifier.of(WizardsMod.ID, "armor/trim/" + trimTextureName), false))
                .build()
        );
    }
}