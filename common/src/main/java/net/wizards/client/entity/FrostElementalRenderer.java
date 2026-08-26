package net.wizards.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.wizards.WizardsMod;
import net.wizards.entity.FrostElementalEntity;

public class FrostElementalRenderer
        extends MobRenderer<FrostElementalEntity, SummonedEntityRenderState, FrostElementalModel> {
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "textures/entity/frost_elemental.png");

    private static final float FLOAT_AMPLITUDE = 0.1F;
    private static final float FLOAT_FREQUENCY = (float)(Math.PI / 20.0); // 2-second cycle (40 ticks)

    public FrostElementalRenderer(EntityRendererProvider.Context context) {
        super(context, new FrostElementalModel(context.bakeLayer(FrostElementalModel.TEXTURE)), 0.75f);
        this.addLayer(new FrostElementalGlowFeatureRenderer(this));
    }

    @Override
    public SummonedEntityRenderState createRenderState() {
        return new SummonedEntityRenderState();
    }

    @Override
    public void extractRenderState(FrostElementalEntity entity, SummonedEntityRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.copyFrom(entity);
        state.attackAnimationSpeed = entity.getAttackAnimationSpeed(
                FrostElementalModel.attackAnimationLengthTicks(state.attackVariant));
        state.spellReleaseAnimationSpeed = entity.getSpellReleaseAnimationSpeed(
                FrostElementalModel.spellReleaseAnimationLengthTicks(state.spellReleaseVariant));
    }

    @Override
    protected void setupRotations(SummonedEntityRenderState state, PoseStack matrices, float bodyYaw, float baseHeight) {
        super.setupRotations(state, matrices, bodyYaw, baseHeight);
        matrices.translate(0.0F, Mth.sin(state.ageInTicks * FLOAT_FREQUENCY) * FLOAT_AMPLITUDE, 0.0F);
    }

    @Override
    public Identifier getTextureLocation(SummonedEntityRenderState state) {
        return TEXTURE;
    }
}
