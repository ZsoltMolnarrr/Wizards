package net.wizards.config;

import net.wizards.item.Armors;
import net.wizards.item.Weapons;

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
        lootConfig.item_groups.put("trash_wands", new LootConfig.ItemGroup(List.of(
                Weapons.noviceWand.id().toString()),
                1
        ));
        lootConfig.item_groups.put("basic_wands", new LootConfig.ItemGroup(List.of(
                Weapons.arcaneWand.id().toString(),
                Weapons.fireWand.id().toString(),
                Weapons.frostWand.id().toString()),
                1
        ));
        lootConfig.item_groups.put("basic_staves", new LootConfig.ItemGroup(List.of(
                Weapons.arcaneStaff.id().toString(),
                Weapons.fireStaff.id().toString(),
                Weapons.frostStaff.id().toString()),
                1
        ));
        lootConfig.item_groups.put("wizard_robes", new LootConfig.ItemGroup(joinLists(
                Armors.wizardRobeSet.idStrings()),
                1
        ));

        List.of("minecraft:chests/abandoned_mineshaft",
                        "minecraft:chests/pillager_outpost",
                        "minecraft:chests/igloo_chest",
                        "minecraft:chests/ruined_portal",
                        "minecraft:chests/buried_treasure",
                        "minecraft:chests/shipwreck_supply",
                        "minecraft:chests/jungle_temple")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("trash_wands")));

        List.of("minecraft:chests/desert_pyramid",
                        "minecraft:chests/bastion_bridge",
                        "minecraft:chests/jungle_temple",
                        "minecraft:chests/pillager_outpost",
                        "minecraft:chests/simple_dungeon",
                        "minecraft:chests/stronghold_crossing")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("basic_wands")));

        List.of("minecraft:chests/bastion_other",
                        "minecraft:chests/nether_bridge",
                        "minecraft:chests/underwater_ruin_small")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("basic_staves")));

        List.of("minecraft:chests/ancient_city",
                        "minecraft:chests/stronghold_corridor",
                        "minecraft:chests/shipwreck_treasure")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("wizard_robes")));

        List.of("minecraft:chests/end_city_treasure",
                        "minecraft:chests/bastion_treasure",
                        "minecraft:chests/stronghold_library",
                        "minecraft:chests/underwater_ruin_big",
                        "minecraft:chests/woodland_mansion")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("basic_staves", "wizard_robes")));

        worldGenConfig = new WorldGenConfig();
        worldGenConfig.entries.addAll(List.of(
                new WorldGenConfig.Entry("minecraft:village/desert/houses", "wizards:village/desert/wizard_tower", 3),
                new WorldGenConfig.Entry("minecraft:village/savanna/houses", "wizards:village/savanna/wizard_tower", 3),
                new WorldGenConfig.Entry("minecraft:village/plains/houses", "wizards:village/plains/wizard_tower", 4),
                new WorldGenConfig.Entry("minecraft:village/taiga/houses", "wizards:village/taiga/wizard_tower", 4),
                new WorldGenConfig.Entry("minecraft:village/snowy/houses", "wizards:village/snowy/wizard_tower", 3)
        ));
    }

    @SafeVarargs
    private static <T> List<T> joinLists(List<T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
