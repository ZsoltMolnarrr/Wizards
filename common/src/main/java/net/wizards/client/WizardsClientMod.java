package net.wizards.client;

import net.wizards.item.Armors;
import net.wizards.item.armor.client.WizardArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class WizardsClientMod {
    public static void initialize() {
        GeoArmorRenderer.registerArmorRenderer(new WizardArmorRenderer(),
                Armors.wizardRobeSet.head,
                Armors.wizardRobeSet.chest,
                Armors.wizardRobeSet.legs,
                Armors.wizardRobeSet.feet);
    }
}
