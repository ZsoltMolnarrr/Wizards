package net.wizards.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.spell_engine.client.gui.ConfigMenuScreen;
import net.wizards.WizardsMod;
import net.wizards.client.WizardsClientMod;
import net.wizards.client.entity.ArcaneEmitterModel;
import net.wizards.client.entity.ArcaneEmitterRenderer;
import net.wizards.client.entity.FireHydraModel;
import net.wizards.client.entity.FireHydraRenderer;
import net.wizards.client.entity.FrostElementalModel;
import net.wizards.client.entity.FrostElementalRenderer;
import net.wizards.entity.WizardEntities;

@EventBusSubscriber(modid = WizardsMod.ID, value = Dist.CLIENT)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        WizardsClientMod.init();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, parent) -> new ConfigMenuScreen(parent));

        // Replay deferred Fire Hydra rendering after the particle pass (see FireHydraRenderer).
        // Game-bus event, subscribed here since this class is on the mod bus.
        // NOTE: must be AFTER_PARTICLES, not AFTER_TRANSLUCENT_BLOCKS. Vanilla renders particles
        // *after* translucent terrain, so AFTER_TRANSLUCENT_BLOCKS fires before particles and the
        // hydra's own puddle particle would paint over the model. AFTER_PARTICLES matches where
        // Fabric's WorldRenderEvents.AFTER_TRANSLUCENT injects (just before clouds, after particles).
        // 21.11: stages are event subclasses; camera + tick progress are no longer carried by the event.
        NeoForge.EVENT_BUS.addListener(RenderLevelStageEvent.AfterParticles.class, render -> {
            var client = net.minecraft.client.Minecraft.getInstance();
            FireHydraRenderer.renderAfterTranslucent(render.getPoseStack(), client.gameRenderer.getMainCamera(),
                    client.getDeltaTracker().getGameTimeDeltaPartialTick(true));
        });
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FrostElementalModel.TEXTURE, FrostElementalModel::getTexturedModelData);
        event.registerLayerDefinition(ArcaneEmitterModel.LAYER, ArcaneEmitterModel::getTexturedModelData);
        event.registerLayerDefinition(FireHydraModel.LAYER, FireHydraModel::getTexturedModelData);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WizardEntities.FROST_ELEMENTAL.type, FrostElementalRenderer::new);
        event.registerEntityRenderer(WizardEntities.ARCANE_EMITTER.type, ArcaneEmitterRenderer::new);
        event.registerEntityRenderer(WizardEntities.FIRE_HYDRA.type, FireHydraRenderer::new);
    }
}