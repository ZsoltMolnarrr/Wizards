package net.wizards.config;

import java.util.HashMap;
import java.util.List;

public class LootConfig {
    public HashMap<String, ItemGroup> item_groups = new HashMap<>();
    public static class ItemGroup { public ItemGroup() { }
        public List<String> ids = List.of();
        public int weight = 1;

        public ItemGroup(List<String> ids, int weight) {
            this.ids = ids;
            this.weight = weight;
        }
    }
    public HashMap<String, List<String>> loot_tables = new HashMap<>();
}
