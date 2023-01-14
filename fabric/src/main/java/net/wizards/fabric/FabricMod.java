package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;
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
        Group.WIZARDS = FabricItemGroupBuilder.build(
                new Identifier(WizardsMod.ID, "general"),
                () -> new ItemStack(Armors.wizardRobeSet.head));
    }

    private void subscribeEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(WizardWorldGen::init);
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            var config = WizardsMod.lootConfig.value;
            var groups = config.loot_tables.get(id.toString());
            if (groups != null) {
                for(var groupName: groups) {
                    var group = config.item_groups.get(groupName);
                    if (group == null) { continue; }
                    for (var itemId: group.items) {
                        var item = WizardItems.entries.get(itemId);
                        if (item == null) { continue; }
                        // System.out.println("XXX Adding to table: " + id.toString() + " item: " + itemId + " weight: " + group.weight);
                        tableBuilder.modifyPools(pool -> pool.with(ItemEntry.builder(item).weight(group.weight)));
                        // tableBuilder.pool(poolBuilder); // This is suggested by Fabric wiki, but adds the loot with 100% chance
                    }
                }
            }
        });
    }
}