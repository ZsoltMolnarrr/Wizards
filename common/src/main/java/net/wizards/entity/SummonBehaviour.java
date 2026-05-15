package net.wizards.entity;

import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class SummonBehaviour {

    public int timeToLive = 60;
    public boolean is_attackable = true;

    // --- Attribute Scaling ---

    public AttributeScaling attribute_scaling = new AttributeScaling();

    public static class AttributeScaling {
        public List<Entry> entries = new ArrayList<>();

        public static class Entry {
            public String attribute_id = "";
            public List<OwnerModifier> modifiers = new ArrayList<>();

            public static class OwnerModifier {
                public String attribute_id = "";
                public EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.ADD_VALUE;
                public double coefficient = 1.0;

                public OwnerModifier() {}

                public OwnerModifier(String attribute_id, EntityAttributeModifier.Operation operation, double coefficient) {
                    this.attribute_id = attribute_id;
                    this.operation = operation;
                    this.coefficient = coefficient;
                }
            }
        }
    }

    // --- Dimensions ---

    public Dimensions dimensions = new Dimensions();
    public static class Dimensions {
        public float width  = 0.6F;
        public float height = 1.8F;
    }

    // --- Movement ---

    public Movement movement = new Movement();
    public static class Movement {
        /// Stationary if false
        public boolean can_move = true;
        public boolean affected_by_gravity = true;

        public Wander wander = new Wander();
        public static class Wander {
            public double speed = 1.0;
            /// Chance per tick to start wandering (0.0–1.0).
            public float probability = 0.001F;
        }

        public Follow follow;
        public static class Follow {
            /// Distance at which the entity begins moving toward the owner.
            public float start_distance = 10F;
            /// Distance at which the entity stops moving toward the owner.
            public float stop_distance = 4.0f;
            /// If greater than 0, teleports to the owner when the distance exceeds this value.
            public float teleport_after_distance = 12F;
        }

        public boolean is_pushable = true;
        public CollisionMode collision = CollisionMode.NONE;
        public enum CollisionMode {
            NONE, ALL, ENEMIES
        }
    }

    // --- Targeting ---

    public Targeting targeting = new Targeting();
    public static class Targeting {
        public boolean revenge = true;
        public boolean attack_with_owner = true;
        public boolean automatic_targeting = false;
        public boolean look_around = true;
    }

    // --- Spawn / Despawn ---

    public SpawnDespawn spawn_despawn = new SpawnDespawn();
    public static class SpawnDespawn {
        /** Ticks the entity spends in the spawning phase (inactionable). */
        public int spawn_ticks = 10;
        /** Ticks the entity spends in the despawning phase (inactionable) before being discarded. */
        public int despawn_ticks = 10;
    }

    // --- Actions ---

    public List<Action.Entry> actions = List.of();
    public static class Action {
        enum Type {
            MELEE_ATTACK, SPELL_CAST
        }
        public static class Entry {
            public Type type;
            public MeleeAttack melee_attack;
            public SpellCast spell_cast;
        }

        public static Entry attack(float max_range, float speed) {
            var a = new MeleeAttack();
            a.max_range = max_range;
            a.speed = speed;
            var e = new Entry();
            e.type = Type.MELEE_ATTACK;
            e.melee_attack = a;
            return e;
        }
        public static class MeleeAttack {
            /// If greater than 0, the entity will perform a melee attack every attack_cooldown ticks.
            public float max_range = 0;
            public float speed = 1.2F;
        }

        public static Entry spell(String spell_id, int cooldown) {
            var s = new SpellCast();
            s.spell_id = spell_id;
            s.cooldown = cooldown;
            var e = new Entry();
            e.type = Type.SPELL_CAST;
            e.spell_cast = s;
            return e;
        }
        public static class SpellCast {
            public String spell_id = "";
            public int cooldown = 20;

            public SpellCast() {}

            public SpellCast(String spell_id, int cooldown) {
                this.spell_id = spell_id;
                this.cooldown = cooldown;
            }
        }
    }
}
