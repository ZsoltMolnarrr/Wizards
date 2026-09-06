package net.wizards.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
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

        EntityModelLayerRegistry.registerModelLayer(FrostElementalModel.TEXTURE, FrostElementalModel::getTexturedModelData);
        EntityRendererRegistry.register(WizardEntities.FROST_ELEMENTAL.type, FrostElementalRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ArcaneEmitterModel.LAYER, ArcaneEmitterModel::getTexturedModelData);
        EntityRendererRegistry.register(WizardEntities.ARCANE_EMITTER.type, ArcaneEmitterRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(FireHydraModel.LAYER, FireHydraModel::getTexturedModelData);
        EntityRendererRegistry.register(WizardEntities.FIRE_HYDRA.type, FireHydraRenderer::new);

        // Replay deferred Fire Hydra rendering after translucent terrain (see FireHydraRenderer).
        // 1.20.1 / Fabric API 0.92: WorldRenderContext exposes a plain `tickDelta()` (no RenderTickCounter).
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context ->
                FireHydraRenderer.renderAfterTranslucent(context.matrixStack(), context.camera(),
                        context.tickDelta()));
    }
}
