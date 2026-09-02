package net.wizards.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.wizards.client.WizardsClientMod;
import net.wizards.client.entity.ArcaneEmitterModel;
import net.wizards.client.entity.ArcaneEmitterRenderer;
import net.wizards.client.entity.FireHydraModel;
import net.wizards.client.entity.FireHydraRenderer;
import net.wizards.client.entity.FrostElementalModel;
import net.wizards.client.entity.FrostElementalRenderer;
import net.wizards.entity.WizardEntities;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WizardsClientMod.init();

        ModelLayerRegistry.registerModelLayer(FrostElementalModel.TEXTURE, FrostElementalModel::getTexturedModelData);
        EntityRendererRegistry.register(WizardEntities.FROST_ELEMENTAL.type, FrostElementalRenderer::new);
        ModelLayerRegistry.registerModelLayer(ArcaneEmitterModel.LAYER, ArcaneEmitterModel::getTexturedModelData);
        EntityRendererRegistry.register(WizardEntities.ARCANE_EMITTER.type, ArcaneEmitterRenderer::new);
        ModelLayerRegistry.registerModelLayer(FireHydraModel.LAYER, FireHydraModel::getTexturedModelData);
        EntityRendererRegistry.register(WizardEntities.FIRE_HYDRA.type, FireHydraRenderer::new);
    }
}
