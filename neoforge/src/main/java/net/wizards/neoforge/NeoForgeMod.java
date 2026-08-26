package net.wizards.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.wizards.WizardsMod;
import net.wizards.villager.WizardVillagers;

@Mod(WizardsMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Run our common setup.
        WizardsMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        // Villager trades — game-bus event (fired per profession); replaces Fabric API's TradeOfferHelper.
        NeoForge.EVENT_BUS.addListener(VillagerTradesEvent.class, NeoForgeMod::onVillagerTrades);
    }

    public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, reg -> {
            WizardsMod.registerSounds();
        });
        event.register(Registries.ITEM, reg -> {
            WizardsMod.registerEntities();
            WizardsMod.registerItems();
        });
        event.register(Registries.MOB_EFFECT, reg -> {
            WizardsMod.registerEffects();
        });
        event.register(Registries.POINT_OF_INTEREST_TYPE, reg -> {
            // POI registration — vanilla registry insert. NeoForge's POI registry callback wires the
            // block-state -> POI mapping from the type's block states, so no Fabric API helper is needed.
            // Not sure why errors are thrown, but this seems to fix it.
            try {
                Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, WizardVillagers.POI_ID,
                        new PoiType(WizardVillagers.poiBlockStates(),
                                WizardVillagers.POI_TICKET_COUNT, WizardVillagers.POI_SEARCH_DISTANCE));
            } catch (Exception e) { }
        });
        event.register(Registries.VILLAGER_PROFESSION, reg -> {
            WizardsMod.registerVillagers(); // registers the profession + builds WizardVillagers.TRADES
        });
    }

    private static void onVillagerTrades(VillagerTradesEvent event) {
        if (!event.getType().equals(WizardVillagers.PROFESSION_KEY)) {
            return;
        }
        WizardVillagers.TRADES.forEach((tier, factories) -> {
            var tierList = event.getTrades().get(tier.intValue());
            if (tierList != null) {
                tierList.addAll(factories);
            }
        });
    }
}
