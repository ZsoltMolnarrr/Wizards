package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.wizards.WizardsMod;
import net.wizards.villager.WizardVillagers;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup.
        WizardsMod.init();
        WizardsMod.registerEntities();
        WizardsMod.registerSounds();
        WizardsMod.registerItems();
        WizardsMod.registerEffects();

        // Villager POI — Fabric API registration (loader-specific; NeoForge does its own).
        // 26.1: `PointOfInterestHelper` was renamed `PoiHelper` (same signatures).
        PoiHelper.register(WizardVillagers.POI_ID,
                WizardVillagers.POI_TICKET_COUNT, WizardVillagers.POI_SEARCH_DISTANCE,
                WizardVillagers.poiBlockStates());
        // Trades are data driven since 26.1 (`data/wizards/{villager_trade,trade_set}`); the profession
        // carries the per-level trade-set keys, so there is nothing left to register here.
        WizardsMod.registerVillagers();
    }
}
