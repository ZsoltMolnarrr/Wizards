package net.wizards.client.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.wizards.WizardsMod;
import net.wizards.entity.FrostElementalEntity;

public class FrostElementalRenderer
        extends MobEntityRenderer<FrostElementalEntity, SummonedEntityRenderState, FrostElementalModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/frost_elemental.png");

    private static final float FLOAT_AMPLITUDE = 0.1F;
    private static final float FLOAT_FREQUENCY = (float)(Math.PI / 20.0); // 2-second cycle (40 ticks)

    public FrostElementalRenderer(EntityRendererFactory.Context context) {
        super(context, new FrostElementalModel(context.getPart(FrostElementalModel.TEXTURE)), 0.75f);
        this.addFeature(new FrostElementalGlowFeatureRenderer(this));
    }

    @Override
    public SummonedEntityRenderState createRenderState() {
        return new SummonedEntityRenderState();
    }

    @Override
    public void updateRenderState(FrostElementalEntity entity, SummonedEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.copyFrom(entity);
        state.attackAnimationSpeed = entity.getAttackAnimationSpeed(
                FrostElementalModel.attackAnimationLengthTicks(state.attackVariant));
        state.spellReleaseAnimationSpeed = entity.getSpellReleaseAnimationSpeed(
                FrostElementalModel.spellReleaseAnimationLengthTicks(state.spellReleaseVariant));
    }

    @Override
    protected void setupTransforms(SummonedEntityRenderState state, MatrixStack matrices, float bodyYaw, float baseHeight) {
        super.setupTransforms(state, matrices, bodyYaw, baseHeight);
        matrices.translate(0.0F, MathHelper.sin(state.age * FLOAT_FREQUENCY) * FLOAT_AMPLITUDE, 0.0F);
    }

    @Override
    public Identifier getTexture(SummonedEntityRenderState state) {
        return TEXTURE;
    }
}
