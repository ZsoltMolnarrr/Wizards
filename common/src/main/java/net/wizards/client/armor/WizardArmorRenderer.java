package net.wizards.client.armor;

import net.minecraft.util.Identifier;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;
import net.wizards.WizardsMod;

public final class WizardArmorRenderer {
    private static final String TrimTextureT1 = "wizard_robe_generic";
    private static final String TrimTextureT2 = "spec_robe_generic";
    private static final String TrimTextureT3 = "netherite_spec_robe_generic";

    private WizardArmorRenderer() { }

    public static GeoArmorRenderer wizard() {
        return make("wizard_robes", "wizard_robe", TrimTextureT1);
    }
    public static GeoArmorRenderer arcane() {
        return make("wizard_robes", "arcane_robe", TrimTextureT2);
    }
    public static GeoArmorRenderer fire() {
        return make("wizard_robes", "fire_robe", TrimTextureT2);
    }
    public static GeoArmorRenderer frost() {
        return make("wizard_robes", "frost_robe", TrimTextureT2);
    }
    public static GeoArmorRenderer netheriteArcane() {
        return make("wizard_robes", "netherite_arcane_robe", TrimTextureT3);
    }
    public static GeoArmorRenderer netheriteFire() {
        return make("wizard_robes", "netherite_fire_robe", TrimTextureT3);
    }
    public static GeoArmorRenderer netheriteFrost() {
        return make("wizard_robes", "netherite_frost_robe", TrimTextureT3);
    }

    private static GeoArmorRenderer make(String modelName, String textureName, String trimTextureName) {
        return GeoArmorRenderer.of(
                new Identifier(WizardsMod.ID, "geo/" + modelName + ".geo.json"),
                new Identifier(WizardsMod.ID, "textures/armor/" + textureName + ".png"))
                .trim(new Identifier(WizardsMod.ID, "armor/trim/" + trimTextureName), false);
    }
}
