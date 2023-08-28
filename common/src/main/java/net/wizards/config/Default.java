package net.wizards.config;

import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.loot.LootConfig;
import net.wizards.item.Armors;
import net.wizards.item.Weapons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Default {
    public final static ItemConfig itemConfig;
    public final static LootConfig lootConfig;
    public final static WorldGenConfig worldGenConfig;
    static {
        itemConfig = new ItemConfig();
        for (var weapon: Weapons.entries) {
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }
        for (var armorSet: Armors.entries) {
            itemConfig.armor_sets.put(armorSet.name(), armorSet.defaults());
        }

        lootConfig = new LootConfig();
        lootConfig.item_groups.put("wands_tier_0", new LootConfig.ItemGroup(List.of(
                Weapons.noviceWand.id().toString()),
                0.25F,
                1F,
                1
        ).chance(0.3F));
        lootConfig.item_groups.put("wands_tier_1", new LootConfig.ItemGroup(List.of(
                Weapons.arcaneWand.id().toString(),
                Weapons.fireWand.id().toString(),
                Weapons.frostWand.id().toString()),
                1
        ).chance(0.3F));
        var staves_tier_1 = "staves_tier_1";
        lootConfig.item_groups.put(staves_tier_1, new LootConfig.ItemGroup(List.of(
                Weapons.wizardStaff.id().toString()),
                1
        ).chance(0.3F));
        var staves_tier_2 = "staves_tier_2";
        lootConfig.item_groups.put(staves_tier_2, new LootConfig.ItemGroup(List.of(
                Weapons.arcaneStaff.id().toString(),
                Weapons.fireStaff.id().toString(),
                Weapons.frostStaff.id().toString()),
                1
        ).chance(0.3F));
        lootConfig.item_groups.put("staves_tier_1_enchanted", new LootConfig.ItemGroup(
                new ArrayList(lootConfig.item_groups.get(staves_tier_1).ids),
                1
        ).chance(0.3F).enchant());
        lootConfig.item_groups.put("staves_tier_2_enchanted", new LootConfig.ItemGroup(
                new ArrayList(lootConfig.item_groups.get(staves_tier_2).ids),
                1
        ).chance(0.3F));

        var robes_tier_1 = "robes_tier_1";
        lootConfig.item_groups.put(robes_tier_1, new LootConfig.ItemGroup(joinLists(
                Armors.wizardRobeSet.idStrings()),
                1
        ).chance(0.25F));
        lootConfig.item_groups.put("robes_tier_1_enchanted", new LootConfig.ItemGroup(
                new ArrayList(lootConfig.item_groups.get(robes_tier_1).ids),
                1
        ).chance(0.25F).enchant());

        var robes_tier_2 = "robes_tier_2";
        lootConfig.item_groups.put(robes_tier_2, new LootConfig.ItemGroup(joinLists(
                Armors.arcaneRobeSet.idStrings(),
                Armors.fireRobeSet.idStrings(),
                Armors.frostRobeSet.idStrings()),
                1
        ).chance(0.5F));
        lootConfig.item_groups.put("robes_tier_2_enchanted", new LootConfig.ItemGroup(
                new ArrayList(lootConfig.item_groups.get(robes_tier_2).ids),
                1
        ).chance(0.5F).enchant());

        List.of("minecraft:chests/abandoned_mineshaft",
                        "minecraft:chests/igloo_chest",
                        "minecraft:chests/shipwreck_supply",
                        "minecraft:chests/jungle_temple")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("wands_tier_0")));

        List.of("minecraft:chests/desert_pyramid",
                        "minecraft:chests/bastion_bridge",
                        "minecraft:chests/jungle_temple",
                        "minecraft:chests/pillager_outpost",
                        "minecraft:chests/simple_dungeon",
                        "minecraft:chests/stronghold_crossing")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("wands_tier_1", "staves_tier_1")));

        List.of("minecraft:chests/shipwreck_treasure")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("robes_tier_1")));

        List.of("minecraft:chests/stronghold_library",
                        "minecraft:chests/underwater_ruin_big",
                        "minecraft:chests/woodland_mansion")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("staves_tier_1_enchanted", "robes_tier_1_enchanted")));

        List.of("minecraft:chests/bastion_other",
                        "minecraft:chests/nether_bridge",
                        "minecraft:chests/underwater_ruin_small")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("staves_tier_2")));

        List.of("minecraft:chests/end_city_treasure",
                        "minecraft:chests/bastion_treasure",
                        "minecraft:chests/ancient_city",
                        "minecraft:chests/stronghold_library")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("staves_tier_2_enchanted", "robes_tier_2")));

        worldGenConfig = new WorldGenConfig();
        worldGenConfig.entries.addAll(List.of(
                new WorldGenConfig.Entry("minecraft:village/desert/houses", "wizards:village/desert/wizard_tower", 1),
                new WorldGenConfig.Entry("minecraft:village/desert/houses", "wizards:village/desert/wizard_tower_2", 4),
                new WorldGenConfig.Entry("minecraft:village/savanna/houses", "wizards:village/savanna/wizard_tower", 3),
                new WorldGenConfig.Entry("minecraft:village/plains/houses", "wizards:village/plains/wizard_tower", 4),
                new WorldGenConfig.Entry("minecraft:village/taiga/houses", "wizards:village/taiga/wizard_tower", 4),
                new WorldGenConfig.Entry("minecraft:village/snowy/houses", "wizards:village/snowy/wizard_tower", 1),
                new WorldGenConfig.Entry("minecraft:village/snowy/houses", "wizards:village/snowy/wizard_tower_2", 4)
        ));
    }

    @SafeVarargs
    private static <T> List<T> joinLists(List<T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
