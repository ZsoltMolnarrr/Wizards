package net.wizards.armor.client;

import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.wizards.armor.WizardArmor;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WizardArmorModel extends AnimatedGeoModel<WizardArmor> {
    @Override
    public Identifier getModelResource(WizardArmor object) {
        return new Identifier(WizardsMod.ID, "geo/wizard_robes_gecko.geo.json");
    }

    @Override
    public Identifier getTextureResource(WizardArmor object) {
        return new Identifier(WizardsMod.ID, "textures/armor/blue_armor.png");
    }

    @Override
    public Identifier getAnimationResource(WizardArmor animatable) {
        return new Identifier(WizardsMod.ID, "animations/armor_idle.json");
    }
}
