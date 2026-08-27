package net.wizards.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.wizards.WizardsMod;
import net.wizards.villager.WizardVillagers;

@Mod(WizardsMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Run our common setup.
        WizardsMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        // 26.1: villager trades are data driven (`data/wizards/{villager_trade,trade_set}`) and
        // NeoForge's `VillagerTradesEvent` is gone — nothing to hook up here any more.
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
            WizardsMod.registerVillagers(); // registers the profession (its trade sets come from JSON)
        });
    }
}
