package net.wizards.config;

import java.util.ArrayList;

public class WorldGenConfig {
    public ArrayList<Entry> entries = new ArrayList<>();
    public static class Entry { public Entry() { }
        public String pool;
        public String structure;
        public int weight = 1;

        public Entry(String pool, String structure, int weight) {
            this.pool = pool;
            this.structure = structure;
            this.weight = weight;
        }
    }
}
