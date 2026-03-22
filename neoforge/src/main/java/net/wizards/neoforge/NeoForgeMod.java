package net.wizards.neoforge;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.wizards.WizardsMod;
import net.wizards.entity.FrostElementalEntity;

@Mod(WizardsMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Run our common setup.
        WizardsMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        modBus.addListener(EntityAttributeCreationEvent.class, event -> {
            event.put(FrostElementalEntity.TYPE, FrostElementalEntity.createMobAttributes().build());
        });
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.ENTITY_TYPE, reg -> {
            FrostElementalEntity.TYPE = Registry.register(
                    Registries.ENTITY_TYPE,
                    FrostElementalEntity.ID,
                    EntityType.Builder.<FrostElementalEntity>of(FrostElementalEntity::new, SpawnGroup.MISC)
                            .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
                            .clientTrackingRange(4)
                            .updateInterval(3)
                            .build()
            );
            WizardsMod.entityConfig.save();
        });
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
