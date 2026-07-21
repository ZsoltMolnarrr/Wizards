package net.wizards.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.spell_engine.entity.SummonedEntity;

public class FireHydraEntity extends SummonedEntity {

    public FireHydraEntity(EntityType<? extends FireHydraEntity> entityType, World world) {
        super(entityType, world);
    }
}
