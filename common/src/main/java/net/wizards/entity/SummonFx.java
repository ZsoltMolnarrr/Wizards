package net.wizards.entity;

import net.spell_engine.api.spell.fx.ModelEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// A one-shot FX bundle: a sound, particle batches, and model effects, emitted server-side at a
/// single moment (e.g. when a summon, or a group of summons, spawns). Mirrors SpellEngine's
/// {@link net.spell_engine.api.spell.Spell.Delivery.Cloud.Spawn}.
///
/// One-shot FX are emitted from the server (particles via a tracker packet, models as self-syncing
/// entities) — unlike continuous existence particles, which are spawned client-side to avoid
/// per-tick traffic.
public class SummonFx {
    @Nullable public Sound sound;
    public ParticleBatch[] particles = new ParticleBatch[]{};
    public List<ModelEffect> model_fx = List.of();
}
