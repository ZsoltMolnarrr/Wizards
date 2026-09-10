package net.wizards.forge.client;

import net.minecraft.client.util.math.MatrixStack;
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
    /// The camera-rotation pose stack of the frame being rendered, captured from an in-`render` stage
    /// so AFTER_LEVEL can use it (see below). Render thread only; cleared on use.
    private static MatrixStack viewPoseStack = null;

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

        // Replay deferred Fire Hydra rendering once the whole world render is done (see
        // FireHydraRenderer for why no earlier stage can work). AFTER_LEVEL is dispatched from
        // GameRenderer, immediately after `WorldRenderer#render` returns - the same instruction Fabric's
        // WorldRenderEvents.END is injected at, and past clouds, weather and the fabulous-graphics
        // transparency compositing.
        //
        // AFTER_LEVEL's own `getPoseStack()` cannot be used: GameRenderer hands that dispatch its
        // *projection* stack, not the camera-rotation stack every in-`render` stage gets. So the view
        // stack is picked up from an earlier stage of the same frame - it is the one object
        // `GameRenderer#renderWorld` created and passed into `render`, and its push/pops are balanced by
        // the time the call returns, so it still carries exactly the camera rotation (including any roll
        // a ViewportEvent applied).
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RenderLevelStageEvent.class, render -> {
            var stage = render.getStage();
            if (stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                viewPoseStack = render.getPoseStack();
            } else if (stage == RenderLevelStageEvent.Stage.AFTER_LEVEL && viewPoseStack != null) {
                var matrices = viewPoseStack;
                viewPoseStack = null;
                FireHydraRenderer.renderAfterWorld(matrices, render.getCamera(), render.getPartialTick());
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
