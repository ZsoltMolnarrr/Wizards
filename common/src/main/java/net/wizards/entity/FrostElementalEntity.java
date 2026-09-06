package net.wizards.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.spell_engine.entity.SummonedEntity;

public class FrostElementalEntity extends SummonedEntity {

    public FrostElementalEntity(EntityType<? extends FrostElementalEntity> entityType, World world) {
        super(entityType, world);
    }

    /// 1.20.1 `Tameable` declares an unmapped `EntityView method_48926()` (vanilla's `TameableEntity`
    /// implements it as `return this.getWorld()`). SpellEngine's `SummonedEntity` implements `Tameable`
    /// but leaves this abstract, so every summon subclass has to supply it.
    /// TODO: fold this into `SummonedEntity` in SpellEngine and delete the override here.
    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }
}
