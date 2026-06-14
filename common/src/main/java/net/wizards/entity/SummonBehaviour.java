package net.wizards.entity;

import com.google.common.base.Suppliers;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SummonBehaviour {

    public int timeToLive = 60;
    public boolean is_attackable = true;

    /// Parses a sound id string into a SoundEvent. Blank / unparseable → null.
    /// Used by the lazy accessors in `Sounds` and `Action.MeleeAttack`.
    @Nullable
    static SoundEvent parseSoundId(String soundId) {
        if (soundId == null || soundId.isBlank()) return null;
        Identifier id = Identifier.tryParse(soundId);
        return id != null ? SoundEvent.of(id) : null;
    }

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
                /// Flat amount added before the owner-scaled term. Final contribution is
                /// `base + ownerValue * coefficient`.
                public double base = 0.0;
                public double coefficient = 1.0;

                public OwnerModifier() {}

                public OwnerModifier(String attribute_id, EntityAttributeModifier.Operation operation, double coefficient) {
                    this(attribute_id, operation, 0.0, coefficient);
                }

                public OwnerModifier(String attribute_id, EntityAttributeModifier.Operation operation, double base, double coefficient) {
                    this.attribute_id = attribute_id;
                    this.operation = operation;
                    this.base = base;
                    this.coefficient = coefficient;
                }
            }
        }
    }

    // --- Dimensions ---

    /// Optional override for the entity's bounding-box size. `null` (the default) means
    /// "inherit from the EntityType" — i.e. use whatever value was passed to
    /// `FabricEntityTypeBuilder.dimensions(...)` at registration. Assign a non-null
    /// `Dimensions` to override per-summon.
    @Nullable public Dimensions dimensions = null;
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
        public CollisionMode collision = CollisionMode.ENEMIES;
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

    // --- Sounds ---

    public Sounds sounds = new Sounds();
    public static class Sounds {
        /// Played once when the entity is summoned. Empty for none.
        public String spawn = "";
        /// Played once when the entity enters its despawn phase. Empty for none.
        public String despawn = "";
        /// Returned from `getHurtSound`. Empty falls back to the vanilla generic hurt sound.
        public String hurt = "";
        /// Returned from `getDeathSound`. Empty falls back to the vanilla generic death sound.
        public String death = "";
        /// Returned from `getAmbientSound` — vanilla's periodic mob-idle sound. Empty disables
        /// it (vanilla MobEntity default is also null, so no ambient noise plays).
        public String ambient = "";
        /// Played on each footstep from `playStepSound`. Empty falls back to the block's
        /// step sound (vanilla behaviour: stone-step on stone, wood-step on planks, etc.).
        public String step = "";

        // Lazy SoundEvent for each id. The lambda reads the instance field each time the
        // supplier is first invoked, so any value Gson installs into `spawn` / `despawn` /
        // ... is picked up. `transient` keeps Gson from serializing the Suppliers themselves.
        public final transient Supplier<SoundEvent> spawnEvent   = Suppliers.memoize(() -> parseSoundId(spawn));
        public final transient Supplier<SoundEvent> despawnEvent = Suppliers.memoize(() -> parseSoundId(despawn));
        public final transient Supplier<SoundEvent> hurtEvent    = Suppliers.memoize(() -> parseSoundId(hurt));
        public final transient Supplier<SoundEvent> deathEvent   = Suppliers.memoize(() -> parseSoundId(death));
        public final transient Supplier<SoundEvent> ambientEvent = Suppliers.memoize(() -> parseSoundId(ambient));
        public final transient Supplier<SoundEvent> stepEvent    = Suppliers.memoize(() -> parseSoundId(step));
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
        public static Entry attack(MeleeAttack melee_attack) {
            var e = new Entry();
            e.type = Type.MELEE_ATTACK;
            e.melee_attack = melee_attack;
            return e;
        }
        public static class MeleeAttack {
            /// If greater than 0, swing is only initiated while the target is within this range.
            public float max_range = 0;
            /// Attack speed, in attacks per second. The cooldown between swing starts is
            /// max(duration, 20 / speed) ticks.
            public float speed = 1.2F;
            /// Pathfinding speed multiplier used while approaching the target.
            public float movement_speed = 1.0F;
            /// Total length of one swing, in ticks.
            public int duration = 20;
            /// Fraction of `duration` after which the impact lands (0 = on swing start,
            /// 0.5 = midway, 1 = on the final tick). The impact tick is `round(duration * windup)`.
            public float windup = 0.5F;
            /// Multiplier applied to `movement_speed` while the swing is in progress (0 = stop).
            public float movement_modifier = 0.5F;
            /// Radius around the primary target in which additional entities are also struck.
            /// 0 = single-target.
            public float radius = 0;
            /// Sound played at swing start (e.g. `"minecraft:entity.player.attack.sweep"`).
            /// Empty / blank disables it. Resolved via `SoundEvent.of(Identifier)`, so any
            /// sound id present in a loaded sounds.json works.
            public String swing_sound = "";
            /// Sound played once on a successful impact (when the swing reaches its windup
            /// tick with the target still in range). AoE radius hits do not retrigger it.
            /// Empty / blank disables it.
            public String impact_sound = "";

            public final transient Supplier<SoundEvent> swingEvent  = Suppliers.memoize(() -> parseSoundId(swing_sound));
            public final transient Supplier<SoundEvent> impactEvent = Suppliers.memoize(() -> parseSoundId(impact_sound));
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
