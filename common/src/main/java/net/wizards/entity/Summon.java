package net.wizards.entity;

import net.spell_engine.api.spell.Spell;

import java.util.ArrayList;
import java.util.List;

/// Declarative definition of a spell-summoned entity.
///
/// Bundles everything needed to spawn and configure a summon: the entity type to spawn, its full
/// runtime {@link SummonBehaviour}, and where/how it is placed in the world (reusing SpellEngine's
/// {@link Spell.EntityPlacement}).
///
/// This mirrors SpellEngine's built-in {@link Spell.Impact.Action.Spawn} so it can later be lifted
/// into SpellEngine as a first-class impact data definition (e.g. `Spell.Impact.Action.Summon`).
/// Until SpellEngine's CUSTOM impact carries a JSON payload, instances live in the Wizards project
/// and are looked up by the custom-impact handler id.
///
/// Note: unlike {@link Spell.Impact.Action.Spawn}, time-to-live is not a separate field here — it is
/// part of {@link SummonBehaviour#lifespan}.
public class Summon {
    /// Registry id of the entity type to spawn. The entity type must implement {@link SpellSummoned}.
    public String entity_type_id;

    /// Full runtime behaviour: lifespan, movement, targeting, actions, sounds, attribute scaling.
    public SummonBehaviour behaviour = new SummonBehaviour();

    /// Spawn-location slots, reusing SpellEngine's placement type. {@link #spawn_count} entities are
    /// spawned, cycling through this list in order and wrapping around (slot `i % placements.size()`).
    public List<Spell.EntityPlacement> placements = new ArrayList<>();

    /// How many entities to spawn. Each one takes the next placement slot, wrapping around the
    /// {@link #placements} list. Defaults to 1.
    public int spawn_count = 1;

    public Summon() {}

    public Summon(String entity_type_id, SummonBehaviour behaviour, List<Spell.EntityPlacement> placements, int spawn_count) {
        this.entity_type_id = entity_type_id;
        this.behaviour = behaviour;
        this.placements = placements;
        this.spawn_count = spawn_count;
    }
}
