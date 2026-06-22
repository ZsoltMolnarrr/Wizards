package net.wizards.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spell_engine.entity.SummonedEntity;
import net.wizards.WizardsMod;

public class FireHydraEntity extends SummonedEntity {
    public static final Identifier ID = Identifier.of(WizardsMod.ID, "fire_hydra");
    public static EntityType<FireHydraEntity> TYPE;

    public FireHydraEntity(EntityType<? extends FireHydraEntity> entityType, World world) {
        super(entityType, world);
    }
}
