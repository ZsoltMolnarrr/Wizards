package net.wizards.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.wizards.mixin.worldgen.StructurePoolAccessor;

import java.util.ArrayList;
import java.util.Map;

public class WizardWorldgen {
    public static void init(MinecraftServer server) {
        Map<String, String> config = Map.ofEntries(
            // Map.entry("minecraft:village/desert/houses", "wizards:village/desert/wizard_tower")
            Map.entry("minecraft:village/desert/houses", "wizards:desert_wizard_tower")
        );
        for(var entry: config.entrySet()) {
            System.out.println("Adding structure: " + entry.getValue() + " to: " + entry.getKey());
            addToStructurePool(server, new Identifier(entry.getKey()), new Identifier(entry.getValue()), 10000);
        }
    }

    private static final RegistryKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(
            Registry.STRUCTURE_PROCESSOR_LIST_KEY, new Identifier("minecraft", "empty"));


    public static void addToStructurePool(MinecraftServer server, Identifier poolId, Identifier structureId, int weight) {

        RegistryEntry<StructureProcessorList> emptyProcessorList = server.getRegistryManager()
                .get(Registry.STRUCTURE_PROCESSOR_LIST_KEY)
                .entryOf(EMPTY_PROCESSOR_LIST_KEY);

        var poolGetter = server.getRegistryManager()
                .get(Registry.STRUCTURE_POOL_KEY)
                .getOrEmpty(poolId);

        if (poolGetter.isEmpty()) {
            System.err.println("Wizards: cannot add to " + poolId + " as it cannot be found!");
            return;
        }

        var pool = poolGetter.get();

        var pieceList = ((StructurePoolAccessor) pool).getElements();
        var piece = StructurePoolElement.ofProcessedSingle(structureId.toString(), emptyProcessorList)
                .apply(StructurePool.Projection.RIGID);

        var list = new ArrayList<>(((StructurePoolAccessor) pool).getElementCounts());
        list.add(Pair.of(piece, weight));
        ((StructurePoolAccessor) pool).setElementCounts(list);

        for (int i = 0; i < weight; ++i) {
            pieceList.add(piece);
        }
    }
}
