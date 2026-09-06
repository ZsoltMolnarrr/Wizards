package net.wizards.forge.client;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.spell_engine.client.gui.ConfigMenuScreen;
import net.wizards.client.WizardsClientMod;
import net.wizards.client.entity.ArcaneEmitterModel;
import net.wizards.client.entity.ArcaneEmitterRenderer;
import net.wizards.client.entity.FireHydraModel;
import net.wizards.client.entity.FireHydraRenderer;
import net.wizards.client.entity.FrostElementalModel;
import net.wizards.client.entity.FrostElementalRenderer;
import net.wizards.entity.WizardEntities;

/// Client-only wiring for Forge 47; only touched from {@link net.wizards.forge.ForgeMod} behind a
/// `Dist.CLIENT` check. Mod-bus listeners are registered explicitly (Forge 47's `@EventBusSubscriber`
/// scanning is avoided so the class is never loaded on a dedicated server).
///
/// 1.20.1 port of the NeoForge client entrypoint: `IConfigScreenFactory` becomes
/// `ConfigScreenHandler.ConfigScreenFactory`, and `RenderLevelStageEvent.getPartialTick()` returns a
/// plain float (no `RenderTickCounter`).
public final class ForgeClientMod {
    public static void register(IEventBus modBus) {
        modBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, ForgeClientMod::onClientSetup);
        modBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterLayerDefinitions.class,
                ForgeClientMod::onRegisterLayerDefinitions);
        modBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterRenderers.class,
                ForgeClientMod::onRegisterRenderers);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        WizardsClientMod.init();
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new ConfigMenuScreen(parent)));

        // Replay deferred Fire Hydra rendering after the particle pass (see FireHydraRenderer).
        // NOTE: must be AFTER_PARTICLES, not AFTER_TRANSLUCENT_BLOCKS. Vanilla renders particles
        // *after* translucent terrain, so AFTER_TRANSLUCENT_BLOCKS fires before particles and the
        // hydra's own puddle particle would paint over the model. AFTER_PARTICLES matches where
        // Fabric's WorldRenderEvents.AFTER_TRANSLUCENT injects (just before clouds, after particles).
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RenderLevelStageEvent.class, render -> {
            if (render.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                FireHydraRenderer.renderAfterTranslucent(render.getPoseStack(), render.getCamera(),
                        render.getPartialTick());
            }
        });
    }

    private static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FrostElementalModel.TEXTURE, FrostElementalModel::getTexturedModelData);
        event.registerLayerDefinition(ArcaneEmitterModel.LAYER, ArcaneEmitterModel::getTexturedModelData);
        event.registerLayerDefinition(FireHydraModel.LAYER, FireHydraModel::getTexturedModelData);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WizardEntities.FROST_ELEMENTAL.type, FrostElementalRenderer::new);
        event.registerEntityRenderer(WizardEntities.ARCANE_EMITTER.type, ArcaneEmitterRenderer::new);
        event.registerEntityRenderer(WizardEntities.FIRE_HYDRA.type, FireHydraRenderer::new);
    }
}
