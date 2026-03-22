package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.wizards.WizardsMod;
import net.wizards.entity.FrostElementalEntity;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup.
        WizardsMod.init();
        FrostElementalEntity.TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                FrostElementalEntity.ID,
                FabricEntityTypeBuilder.<FrostElementalEntity>create(SpawnGroup.MISC, FrostElementalEntity::new)
                        .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(3)
                        .build()
        );
        FabricDefaultAttributeRegistry.register(FrostElementalEntity.TYPE, FrostElementalEntity.createMobAttributes().build());
        WizardsMod.entityConfig.save();
        WizardsMod.registerSounds();
        WizardsMod.registerItems();
        WizardsMod.registerEffects();
        WizardsMod.registerPOI();
        WizardsMod.registerVillagers();
    }
}
