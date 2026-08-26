package net.wizards.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.spell_engine.entity.SummonedEntity;

public class FireHydraEntity extends SummonedEntity {

    public FireHydraEntity(EntityType<? extends FireHydraEntity> entityType, Level world) {
        super(entityType, world);
    }
}
