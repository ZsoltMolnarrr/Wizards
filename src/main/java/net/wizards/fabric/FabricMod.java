package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.spell_engine.api.loot.LootHelper;
import net.wizards.WizardsMod;
import net.wizards.item.Armors;
import net.wizards.item.Group;
import net.wizards.item.WizardItems;
import net.wizards.util.SoundHelper;
import net.wizards.worldgen.WizardWorldGen;

public class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        preInit();
        WizardsMod.init();
        SoundHelper.registerSounds();
        subscribeEvents();
    }

    private void preInit() {
        Group.WIZARDS = FabricItemGroup.builder()
                 .icon(() -> new ItemStack(Armors.wizardRobeSet.head))
                .displayName(Text.translatable("itemGroup.wizards.general"))
                .build();
    }

    private void subscribeEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(WizardWorldGen::init);
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            LootHelper.configure(id, tableBuilder, WizardsMod.lootConfig.value, WizardItems.entries);
        });
    }

}