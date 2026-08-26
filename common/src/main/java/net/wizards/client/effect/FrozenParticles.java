package net.wizards.client.effect;

import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.fx.SpellEngineParticles;

public class FrozenParticles implements CustomParticleStatusEffect.Spawner {

    private final ParticleGroup particles;

    public FrozenParticles(int particleCount) {
        this.particles = ParticleGroupBuilder
                .magic(SpellEngineParticles.magic_frost, ParticleGroup.Motion.BURST, Color.FROST)
                .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                        .count(particleCount).speed(0.1F, 0.3F));
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = particles.copy();
        scaledParticles.batch.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.level(), livingEntity, scaledParticles);
    }
}
