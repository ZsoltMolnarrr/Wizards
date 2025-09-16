package net.wizards.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.wizards.client.WizardsClientMod;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WizardsClientMod.init();
    }
}
