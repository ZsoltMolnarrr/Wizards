package net.wizards.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.spell_engine.entity.SummonedEntity;

public class ArcaneEmitterEntity extends SummonedEntity {

    public ArcaneEmitterEntity(EntityType<? extends ArcaneEmitterEntity> entityType, World world) {
        super(entityType, world);
    }
}
