package net.wizards.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.LightmapTextureManager;
import net.wizards.WizardsMod;

public class FrostElementalGlowFeatureRenderer
        extends FeatureRenderer<SummonedEntityRenderState, FrostElementalModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/frost_elemental_glow.png");
    private static final RenderLayer LAYER = RenderLayers.eyes(TEXTURE);

    public FrostElementalGlowFeatureRenderer(FeatureRendererContext<SummonedEntityRenderState, FrostElementalModel> context) {
        super(context);
    }

    /// 1.21.9+ rendering is queue-based: the glow pass is submitted as an extra model command
    /// on the eyes layer (fullbright) instead of writing into a VertexConsumer directly.
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
