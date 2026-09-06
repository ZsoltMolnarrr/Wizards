package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.wizards.WizardsMod;
import net.wizards.fabric.village.FabricVillageStructures;
import net.wizards.villager.WizardVillagers;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup.
        WizardsMod.init();
        // StructurePoolAPI is Fabric-only on 1.20.1 — install the village injector before the villagers
        // are registered (WizardVillagers.register() calls it).
        FabricVillageStructures.install();
        WizardsMod.registerEntities();
        WizardsMod.registerSounds();
        WizardsMod.registerItems();
        WizardsMod.registerEffects();

        // Villager POI + trades — Fabric API registration (loader-specific; Forge does its own).
        PointOfInterestHelper.register(WizardVillagers.POI_ID,
                WizardVillagers.POI_TICKET_COUNT, WizardVillagers.POI_SEARCH_DISTANCE,
                WizardVillagers.poiBlockStates());
        WizardsMod.registerVillagers(); // registers the profession + builds WizardVillagers.TRADES
        WizardVillagers.TRADES.forEach((tier, factories) ->
                TradeOfferHelper.registerVillagerOffers(WizardVillagers.PROFESSION, tier,
                        list -> list.addAll(factories)));
    }
}
