package net.wizards.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.spell_engine.entity.SummonedEntity;

public class FrostElementalEntity extends SummonedEntity {

    public FrostElementalEntity(EntityType<? extends FrostElementalEntity> entityType, Level world) {
        super(entityType, world);
    }
}
