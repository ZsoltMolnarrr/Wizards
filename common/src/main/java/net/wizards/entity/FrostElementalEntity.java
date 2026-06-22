package net.wizards.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spell_engine.entity.SummonedEntity;
import net.wizards.WizardsMod;

public class FrostElementalEntity extends SummonedEntity {
    public static final Identifier ID = Identifier.of(WizardsMod.ID, "frost_elemental");
    public static EntityType<FrostElementalEntity> TYPE;

    public FrostElementalEntity(EntityType<? extends FrostElementalEntity> entityType, World world) {
        super(entityType, world);
    }
}
