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
        /// What kind of target the entity auto-acquires when nothing else is set.
        ///   NONE     — no auto-targeting; relies on revenge / attack_with_owner only
        ///   HOSTILE  — acquires nearby hostile mobs (attacker pets)
        ///   FRIENDLY — acquires nearby wounded allies, including the owner (healer pets)
        ///   BOTH     — installs both goals; friendly takes priority (heal first, then fight)
        public AutoTarget automatic_targeting = AutoTarget.NONE;
        public boolean look_around = true;

        public enum AutoTarget { NONE, HOSTILE, FRIENDLY, BOTH }

        /// Ordered list of target-clear conditions evaluated at trigger time
        /// (an action completes, or each tick once the time threshold elapses).
        /// The first condition whose trigger fires rolls its `chance`; on success
        /// the entity's target is nulled and the iteration stops. Subsequent
        /// conditions are not consulted, so put more specific patterns first.
        ///
        /// Empty list (default) preserves the prior behaviour — actions never
        /// clear the target on their own.
        public List<ClearCondition> clear_conditions = List.of();

        /// A `chance` paired with one trigger configuration. Exactly one of the
        /// per-trigger sub-blocks should be non-null:
        ///
        ///   `on_action_completed` — fires when a goal reports an action just
        ///                           finished; the inner `ActionMatch` narrows
        ///                           the match by type and (for spells) spell id.
        ///   `after_ticks`         — fires each tick once `ticks` ticks have
        ///                           elapsed since the current target was first
        ///                           acquired (i.e. the entity has held this same
        ///                           target for at least that long).
        ///
        /// Leaving both null disables the condition. New trigger kinds slot in as
        /// additional sub-blocks without disturbing existing fields.
        public static class ClearCondition {
            /// Probability in `[0..1]` rolled when the trigger fires. Default 1
            /// always clears on a triggered match; values below 1 produce
            /// stochastic "sometimes drop the target" behaviour. 0 is treated
            /// as "match but never roll true" — still stops iteration, so a
            /// `chance=0` condition acts as an exclusion before broader rules.
            public float chance = 1F;
            @Nullable public OnActionCompleted on_action_completed = null;
            @Nullable public AfterTicks after_ticks = null;

            /// Trigger that fires when an action ends after running to completion
            /// (melee swing reaches its full duration; spell cast reaches release).
            /// Both fields null = match any completed action.
            public static class OnActionCompleted {
                /// Action type to match. `null` = any type.
                @Nullable public Action.Type action_type = null;
                /// Spell id to match. `null` = any spell. Ignored when the
                /// completed action is not a `SPELL_CAST`.
                @Nullable public String spell_id = null;
            }

            /// Trigger that fires every tick once the entity has held its current
            /// target for at least `ticks` ticks. Acquisition is reset whenever
            /// `setTarget` switches to a different non-null target.
            public static class AfterTicks {
                public int ticks = 0;
            }
        }
    }

    // --- Lifespan ---

    public Lifespan lifespan = new Lifespan();
    public static class Lifespan {
        /** Ticks the entity spends in the spawning phase (inactionable). */
        public int spawn_ticks = 10;
        /** Seconds the entity spends in the active phase. Total entity lifespan in ticks is
         *  `spawn_ticks + active_seconds * 20 + despawn_ticks`. */
        public int active_seconds = 60;
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
        public enum Type {
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
            public float max_range = 3;
            /// Coefficient controlling how much the entity's scale extends `max_range`.
            /// Effective max_range = `max_range * (1 + entityScale * attack_range_scaling)`.
            public float attack_range_scaling = 0.5F;
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
            /// Pool of animation variant numbers to choose from on each swing. One is picked
            /// uniformly at random and synced to the client via the entity's variant tracker;
            /// the model picks the matching animation (falling back to variant 1 when its
            /// own animation set doesn't have a match).
            public List<Integer> animation_variants = List.of(1);

            public final transient Supplier<SoundEvent> swingEvent  = Suppliers.memoize(() -> parseSoundId(swing_sound));
            public final transient Supplier<SoundEvent> impactEvent = Suppliers.memoize(() -> parseSoundId(impact_sound));
        }

        public static Entry spell(SpellCast spell_cast) {
            var e = new Entry();
            e.type = Type.SPELL_CAST;
            e.spell_cast = spell_cast;
            return e;
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
            /// Target-distance band the goal engages in. All-zero defaults preserve the
            /// original behaviour (`max = spell.range`, `min = 0`, `preferred = max × 0.75`).
            public Range range = new Range();
            /// Pool of cast-animation variant numbers to choose from when a cast begins.
            /// See `MeleeAttack.animation_variants` for selection / fallback semantics.
            public List<Integer> cast_animation_variants = List.of(1);
            /// Pool of release-animation variant numbers to choose from when the spell fires.
            /// Independently rolled from `cast_animation_variants`.
            public List<Integer> release_animation_variants = List.of(1);
            /// Ticks the release animation plays for. Drives client-side auto-stop.
            public int release_animation_duration = 15;

            public SpellCast() {}

            public SpellCast(String spell_id, int cooldown) {
                this.spell_id = spell_id;
                this.cooldown = cooldown;
            }

            /// Engagement-distance configuration for a SpellCast action.
            ///
            /// All fields are FRACTIONS of the spell's effective range — `SpellHelper.getRange(caster, spell)`,
            /// which folds in caster-level modifiers (gear, attributes, etc.). For a spell whose
            /// effective range works out to 16 blocks, `min = 0.5` means "minimum 8 blocks".
            ///
            /// The goal only starts (and only keeps running) when the target sits inside
            /// `[min, max] × effectiveRange`. Once running, the entity navigates to
            /// `preferred × effectiveRange` from the target, and the cast counter only
            /// advances while inside that radius.
            ///
            /// Compose these knobs across multiple SpellCast actions to express layered
            /// behaviour — e.g. a long-range spell with `min` set, plus a short-range
            /// spell with `max > 1` (engage from beyond its own theoretical range), makes
            /// the entity walk in from far to use the short-range option.
            public static class Range {
                /// Minimum engagement distance, as a fraction of effective range. Targets
                /// closer than `min × effectiveRange` make `canStart` fail — useful for
                /// handing close-range fights off to a shorter-range action lower in the
                /// priority list. Default 0 = no minimum.
                public float min = 0F;
                /// Maximum engagement distance, as a fraction of effective range. Targets
                /// farther than `max × effectiveRange` make `canStart` fail. Default 1 =
                /// full effective range. Set above 1 to "walk in from far" — the goal
                /// becomes eligible at a distance the spell can't yet reach, and the
                /// navigation logic closes the gap to `preferred × effectiveRange` before
                /// the cast counter starts advancing.
                public float max = 1F;
                /// Preferred standoff distance, as a fraction of effective range. The
                /// entity navigates to `preferred × effectiveRange` blocks from the target,
                /// and the cast counter only advances while inside that radius. Default 0.75.
                public float preferred = 0.75F;
            }
        }
    }
}
