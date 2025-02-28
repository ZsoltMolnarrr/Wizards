package net.wizards.config;

import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.spell_engine.api.config.ConfigFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Default {
    public final static ConfigFile.Equipment itemConfig;
    public final static StructurePoolConfig villageConfig;
    static {
        itemConfig = new ConfigFile.Equipment();

        villageConfig = new StructurePoolConfig();
        var limit = 1;
        villageConfig.entries.addAll(List.of(
                new StructurePoolConfig.Entry("minecraft:village/desert/houses", new ArrayList<>(Arrays.asList(
                        new StructurePoolConfig.Entry.Structure("wizards:village/desert/wizard_tower", 1, limit),
                        new StructurePoolConfig.Entry.Structure("wizards:village/desert/wizard_tower_2", 3, limit))
                )),
                new StructurePoolConfig.Entry("minecraft:village/savanna/houses", "wizards:village/savanna/wizard_tower", 3, limit),

                new StructurePoolConfig.Entry("minecraft:village/plains/houses", "wizards:village/plains/wizard_tower", 3, limit),

                new StructurePoolConfig.Entry("minecraft:village/taiga/houses", "wizards:village/taiga/wizard_tower", 3, limit),

                new StructurePoolConfig.Entry("minecraft:village/snowy/houses", new ArrayList<>(Arrays.asList(
                        new StructurePoolConfig.Entry.Structure("wizards:village/snowy/wizard_tower", 1, limit),
                        new StructurePoolConfig.Entry.Structure("wizards:village/snowy/wizard_tower_2", 3, limit))
                ))
        ));
    }

    @SafeVarargs
    private static <T> List<T> joinLists(List<T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
