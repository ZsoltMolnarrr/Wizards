package net.wizards.client.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.wizards.entity.FireHydraEntity;

public class FireHydraGlowFeatureRenderer extends FeatureRenderer<FireHydraEntity, FireHydraModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/fire_hydra_glow.png");
    private static final RenderLayer LAYER = RenderLayer.getEyes(TEXTURE);

    public FireHydraGlowFeatureRenderer(FeatureRendererContext<FireHydraEntity, FireHydraModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, FireHydraEntity entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                       float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
        this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV);
    }
}
