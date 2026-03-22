package net.wizards.entity;

import java.util.ArrayList;
import java.util.List;

public class SummonBehaviour {

    public int timeToLive = 60;

    // --- Attribute Scaling ---

    public AttributeScaling attribute_scaling = new AttributeScaling();

    public static class AttributeScaling {
        public List<Entry> entries = new ArrayList<>();

        public static class Entry {
            public String attribute_id = "";
            public List<OwnerModifier> modifiers = new ArrayList<>();

            public static class OwnerModifier {
                public String attribute_id = "";
                public double coefficient = 1.0;

                public OwnerModifier() {}

                public OwnerModifier(String attribute_id, double coefficient) {
                    this.attribute_id = attribute_id;
                    this.coefficient = coefficient;
                }
            }
        }
    }

    // --- Collision ---

    public boolean canBePushed = false;
    public CollisionMode collision_mode = CollisionMode.ALLIES_PASSTHROUGH;
    public enum CollisionMode {
        PASSTHROUGH, ALLIES_PASSTHROUGH, ENEMIES_BUMP
    }

    // --- Movement ---

    public Movement movement = Movement.FOLLOW;
    public enum Movement {
        STATIONARY, FOLLOW
    }

    // --- Targeting ---

    public Targeting targeting = new Targeting();
    public static class Targeting {
        public boolean revenge = true;
        public boolean attack_with_owner = true;
        public boolean automatic_targeting = false;
    }

    // --- Actions ---

    public Actions actions = new Actions();
    public static class Actions {
        public boolean melee_attack = false;
        public static class SpellEntry {
            public String spell_id = "";
            public int cooldown = 20;

            public SpellEntry() {}

            public SpellEntry(String spell_id, int cooldown) {
                this.spell_id = spell_id;
                this.cooldown = cooldown;
            }
        }
    }
}
