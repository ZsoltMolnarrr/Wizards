package net.wizards.fabric;

import net.wizards.WizardsMod;
import net.fabricmc.api.ModInitializer;

public class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        WizardsMod.init();
    }
}