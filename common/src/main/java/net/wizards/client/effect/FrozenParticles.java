package net.wizards.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.particle.ParticleHelper;

public class FrozenParticles implements CustomParticleStatusEffect.Spawner {

    private final ParticleBatch particles;

    public FrozenParticles(int particleCount) {
        this.particles = new ParticleBatch(
                "spell_engine:frost_hit",
                ParticleBatch.Shape.SPHERE,
                ParticleBatch.Origin.CENTER,
                null,
                particleCount,
                0.1F,
                0.3F,
                0);
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = new ParticleBatch(particles);
        scaledParticles.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.getWorld(), livingEntity, scaledParticles);
    }
}