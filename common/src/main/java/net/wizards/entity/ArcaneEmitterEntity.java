package net.wizards.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.spell_engine.entity.SummonedEntity;

public class ArcaneEmitterEntity extends SummonedEntity {

    public ArcaneEmitterEntity(EntityType<? extends ArcaneEmitterEntity> entityType, Level world) {
        super(entityType, world);
    }
}
