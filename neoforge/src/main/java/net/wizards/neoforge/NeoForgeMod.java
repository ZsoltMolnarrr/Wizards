package net.wizards.neoforge;

import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import net.neoforged.neoforge.registries.RegisterEvent;
import net.wizards.WizardsMod;

@Mod(WizardsMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Run our common setup.
        WizardsMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            WizardsMod.registerSounds();
        });
        event.register(RegistryKeys.ITEM, reg -> {
            WizardsMod.registerItems();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            WizardsMod.registerEffects();
        });
        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, reg -> {
            // Not sure why errors are thrown, but this seems to fix it.
            try {
                WizardsMod.registerPOI();
            } catch (Exception e) { }
        });
        event.register(RegistryKeys.VILLAGER_PROFESSION, reg -> {
            WizardsMod.registerVillagers();
        });
    }
}
