package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.wizards.WizardsMod;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup.
        WizardsMod.init();
        WizardsMod.registerSounds();
        WizardsMod.registerItems();
        WizardsMod.registerEffects();
        WizardsMod.registerPOI();
        WizardsMod.registerVillagers();
    }
}
