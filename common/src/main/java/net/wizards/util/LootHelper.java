package net.wizards.util;

import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.wizards.config.LootConfig;
import net.wizards.item.WizardItems;

public class LootHelper {
    public static void configure(Identifier id, LootTable.Builder tableBuilder, LootConfig config) {
        var groups = config.loot_tables.get(id.toString());
        if (groups != null) {
            for(var groupName: groups) {
                var group = config.item_groups.get(groupName);
                if (group == null || group.ids.isEmpty()) { continue; }
                LootPool.Builder lootPoolBuilder = LootPool.builder();
                lootPoolBuilder.rolls(BinomialLootNumberProvider.create(1, 1F / group.ids.size()));
                lootPoolBuilder.bonusRolls(ConstantLootNumberProvider.create(1.2F));

                for (var entryId: group.ids) {
//                    switch (group.kind) {
//                        case ONE_OF -> {
//                            var tag = itemTagKey(entryId);
//                            if (tag == null) { continue; }
//                            tableBuilder.modifyPools(pool -> pool.with(TagEntry.builder(tag).weight(group.weight)) );
//                        }
//                        case ALL_OF -> {
//                            var item = WizardItems.entries.get(entryId);
//                            if (item == null) { continue; }
//                            tableBuilder.modifyPools(pool -> pool.with(ItemEntry.builder(item).weight(group.weight)) );
//                        }
//                    }
                    var item = WizardItems.entries.get(entryId);
                    if (item == null) { continue; }
                    lootPoolBuilder.with(ItemEntry.builder(item).weight(group.weight));
                }
                tableBuilder.pool(lootPoolBuilder.build());
            }
        }
    }

    private static TagKey<Item> itemTagKey(String id) {
        return TagKey.of(Registry.ITEM_KEY, new Identifier(id));
    }
}
