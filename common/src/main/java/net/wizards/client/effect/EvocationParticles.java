package net.wizards.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.fx.SpellEngineParticles;

import java.util.List;

public class EvocationParticles implements CustomParticleStatusEffect.Spawner {
    private static final Color ARCANE_COLOR_LIGHT = Color.from(0xFF99FF);
    private static final Color ARCANE_COLOR_VERY_LIGHT = Color.from(0xFFCCFF);

    private final List<ParticleBatch> particles;

    public EvocationParticles() {
        this.particles = List.of(
                new ParticleBatch(
                    SpellEngineParticles.lightning_arc_A.id().toString(),
                    ParticleBatch.Shape.SPHERE,
                    ParticleBatch.Origin.CENTER,
                    null,
                    1,
                    0.05F,
                    0.1F,
                    0)
                    .color(ARCANE_COLOR_LIGHT.toRGBA())
                    .extent(0.5F),
                new ParticleBatch(
                    SpellEngineParticles.lightning_arc_B.id().toString(),
                    ParticleBatch.Shape.SPHERE,
                    ParticleBatch.Origin.CENTER,
                    null,
                    1,
                    0.05F,
                    0.1F,
                    0)
                    .color(ARCANE_COLOR_LIGHT.toRGBA())
                    .extent(0.5F)
        );
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        int clampedAmplifier = Math.min(amplifier + 1, 10);
        int interval = 20 - (clampedAmplifier * 16 / 10);
        if (livingEntity.age % interval != 0) {
            return;
        }
        for (var batch : particles) {
            ParticleHelper.play(livingEntity.getWorld(), livingEntity, batch);
        }
    }
}