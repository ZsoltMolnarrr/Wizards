package net.wizards.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class EntityConfig {
    public LinkedHashMap<String, Entry> entries = new LinkedHashMap<>();

    public static class Entry {
        public CommonAttributes common = new CommonAttributes();
        public List<CustomAttribute> custom = new ArrayList<>();
    }

    public static class CommonAttributes {
        public double max_health = 20;
        public double movement_speed = 0.25;
        public double attack_damage = 2;
        public double follow_range = 32;

        public CommonAttributes() {}

        public CommonAttributes(double max_health, double movement_speed, double attack_damage) {
            this.max_health = max_health;
            this.movement_speed = movement_speed;
            this.attack_damage = attack_damage;
        }
    }

    public static class CustomAttribute {
        public String id = "";
        public double value = 0;

        public CustomAttribute() {}

        public CustomAttribute(String id, double value) {
            this.id = id;
            this.value = value;
        }
    }
}
