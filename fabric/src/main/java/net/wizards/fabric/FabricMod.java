package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.wizards.item.Armors;
import net.wizards.item.Group;
import net.wizards.item.WizardItems;
import net.wizards.util.SoundHelper;

public class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        preInit();
        WizardsMod.init();
        SoundHelper.registerSounds();
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
                        System.out.println("XXX Adding to table: " + id.toString() + " item: " + itemId + " weight: " + group.weight);
                        tableBuilder.modifyPools(pool -> pool.with(ItemEntry.builder(item).weight(group.weight)));
                        // tableBuilder.pool(poolBuilder);
                    }
                }
            }
        });
    }

    // Loader framework specific pre-init
    private void preInit() {
        Group.WIZARDS = FabricItemGroupBuilder.build(
                new Identifier(WizardsMod.ID, "general"),
                () -> new ItemStack(Armors.wizardRobeSet.head));
    }
}