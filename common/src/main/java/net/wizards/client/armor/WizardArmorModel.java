package net.wizards.client.armor;

import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.wizards.item.WizardArmor;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WizardArmorModel extends AnimatedGeoModel<WizardArmor> {
    @Override
    public Identifier getModelResource(WizardArmor object) {
        return new Identifier(WizardsMod.ID, "geo/wizard_robes.geo.json");
    }

    @Override
    public Identifier getTextureResource(WizardArmor armor) {
        var texture = armor.customMaterial.getName();
        System.out.println("Armor texture: "+ "textures/armor/" + texture + ".png");
        return new Identifier(WizardsMod.ID, "textures/armor/" + texture + ".png");
    }

    @Override
    public Identifier getAnimationResource(WizardArmor animatable) {
        return null; // new Identifier(WizardsMod.ID, "animations/armor_idle.json");
    }
}
