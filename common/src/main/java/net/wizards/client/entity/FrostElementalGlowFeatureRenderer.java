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
import net.wizards.entity.FrostElementalEntity;

public class FrostElementalGlowFeatureRenderer extends FeatureRenderer<FrostElementalEntity, FrostElementalModel> {
    public static final Identifier TEXTURE =
            new Identifier(WizardsMod.ID, "textures/entity/frost_elemental_glow.png");
    private static final RenderLayer LAYER = RenderLayer.getEyes(TEXTURE);

    public FrostElementalGlowFeatureRenderer(FeatureRendererContext<FrostElementalEntity, FrostElementalModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, FrostElementalEntity entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                       float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
        // 1.20.1 has no 4-arg `Model#render` convenience overload — pass the colour channels explicitly.
        this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);
    }
}
