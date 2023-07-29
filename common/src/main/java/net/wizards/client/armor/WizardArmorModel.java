package net.wizards.client.armor;

import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.wizards.item.WizardArmor;
import software.bernie.geckolib.model.GeoModel;

public class WizardArmorModel extends GeoModel<WizardArmor> {
    @Override
    public Identifier getModelResource(WizardArmor object) {
        return new Identifier(WizardsMod.ID, "geo/wizard_robes.geo.json");
    }

    @Override
    public Identifier getTextureResource(WizardArmor armor) {
        var texture = armor.customMaterial.name();
        return new Identifier(WizardsMod.ID, "textures/armor/" + texture + ".png");
    }

    @Override
    public Identifier getAnimationResource(WizardArmor animatable) {
        return null; // new Identifier(WizardsMod.ID, "animations/armor_idle.json");
    }
}
