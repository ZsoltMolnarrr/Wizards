package net.wizards.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.wizards.client.WizardsClientMod;
import net.wizards.client.entity.FrostElementalModel;
import net.wizards.client.entity.FrostElementalRenderer;
import net.wizards.entity.FrostElementalEntity;
import net.wizards.entity.WizardEntities;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WizardsClientMod.init();

        EntityModelLayerRegistry.registerModelLayer(FrostElementalModel.MANTIS, FrostElementalModel::getTexturedModelData);
        EntityRendererRegistry.register(FrostElementalEntity.TYPE, FrostElementalRenderer::new);
    }
}
