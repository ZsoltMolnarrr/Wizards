package net.wizards.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.wizards.WizardsMod;

public class FrostElementalGlowFeatureRenderer
        extends RenderLayer<SummonedEntityRenderState, FrostElementalModel> {
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "textures/entity/frost_elemental_glow.png");
    private static final RenderType LAYER = RenderTypes.eyes(TEXTURE);

    public FrostElementalGlowFeatureRenderer(RenderLayerParent<SummonedEntityRenderState, FrostElementalModel> context) {
        super(context);
    }

    /// 1.21.9+ rendering is queue-based: the glow pass is submitted as an extra model command
    /// on the eyes layer (fullbright) instead of writing into a VertexConsumer directly.
    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector queue, int light,
                       SummonedEntityRenderState state, float limbAngle, float limbDistance) {
        queue.submitModel(
                this.getParentModel(),
                state,
                matrices,
                LAYER,
                LightTexture.FULL_BRIGHT,
                LivingEntityRenderer.getOverlayCoords(state, 0.0F),
                -1,
                null,
                state.outlineColor,
                null);
    }
}
