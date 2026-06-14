package net.wizards.client.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.wizards.WizardsMod;
import net.wizards.entity.FrostElementalEntity;

public class FrostElementalRenderer extends MobEntityRenderer<FrostElementalEntity, FrostElementalModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/frost_elemental.png");

    private static final float FLOAT_AMPLITUDE = 0.1F;
    private static final float FLOAT_FREQUENCY = (float)(Math.PI / 20.0); // 2-second cycle (40 ticks)

    public FrostElementalRenderer(EntityRendererFactory.Context context) {
        super(context, new FrostElementalModel(context.getPart(FrostElementalModel.TEXTURE)), 0.75f);
        this.addFeature(new FrostElementalGlowFeatureRenderer(this));
    }

    @Override
    protected void setupTransforms(FrostElementalEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta, scale);
        matrices.translate(0.0, MathHelper.sin(animationProgress * FLOAT_FREQUENCY) * FLOAT_AMPLITUDE, 0.0);
    }

    @Override
    public Identifier getTexture(FrostElementalEntity entity) {
        return TEXTURE;
    }
}
