package net.wizards.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.wizards.WizardsMod;

public class FrostElementalEntity extends SummonedEntity {
    public static final Identifier ID = Identifier.of(WizardsMod.ID, "frost_elemental");
    public static EntityType<FrostElementalEntity> TYPE;

    public FrostElementalEntity(EntityType<? extends FrostElementalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        var cfg = WizardsMod.entityConfig.value.entries.get(ID.getPath());
        var builder = LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, cfg.common.follow_range)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, cfg.common.max_health)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, cfg.common.movement_speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, cfg.common.attack_damage);
        for (var custom : cfg.custom) {
            Registries.ATTRIBUTE.getEntry(Identifier.of(custom.id))
                    .ifPresent(entry -> builder.add(entry, custom.value));
        }
        return builder;
    }
}
