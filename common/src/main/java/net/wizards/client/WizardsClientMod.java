package net.wizards.client;

import net.wizards.armor.Armors;
import net.wizards.armor.client.WizardArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class WizardsClientMod {
    public static void initialize() {
        GeoArmorRenderer.registerArmorRenderer(new WizardArmorRenderer(),
                Armors.WizardRobes.head,
                Armors.WizardRobes.chest,
                Armors.WizardRobes.legs,
                Armors.WizardRobes.feet);
    }
}
