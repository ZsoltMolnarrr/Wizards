package net.wizards.client.entity;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

public class FireHydraGlowFeatureRenderer
        extends FeatureRenderer<SummonedEntityRenderState, FireHydraModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/fire_hydra_glow.png");
    private static final RenderLayer LAYER = RenderLayers.eyes(TEXTURE);

    public FireHydraGlowFeatureRenderer(FeatureRendererContext<SummonedEntityRenderState, FireHydraModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                       SummonedEntityRenderState state, float limbAngle, float limbDistance) {
        queue.submitModel(
                this.getContextModel(),
                state,
                matrices,
                LAYER,
                LightmapTextureManager.MAX_LIGHT_COORDINATE,
                LivingEntityRenderer.getOverlay(state, 0.0F),
                -1,
                null,
                state.outlineColor,
                null);
    }
}
