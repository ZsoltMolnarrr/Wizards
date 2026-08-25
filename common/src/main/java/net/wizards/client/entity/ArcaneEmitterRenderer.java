package net.wizards.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;
import net.wizards.entity.ArcaneEmitterEntity;

public class ArcaneEmitterRenderer
        extends MobEntityRenderer<ArcaneEmitterEntity, SummonedEntityRenderState, ArcaneEmitterModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/arcane_emitter.png");
    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW_TRANSLUCENT, false);
    // RenderLayer.getEntityTranslucentEmissive(TEXTURE);

    public ArcaneEmitterRenderer(EntityRendererFactory.Context context) {
        super(context, new ArcaneEmitterModel(context.getPart(ArcaneEmitterModel.LAYER)), 0f);
    }

    @Override
    public SummonedEntityRenderState createRenderState() {
        return new SummonedEntityRenderState();
    }

    @Override
    public void updateRenderState(ArcaneEmitterEntity entity, SummonedEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.copyFrom(entity);
    }

    @Override
    protected void setupTransforms(SummonedEntityRenderState state, MatrixStack matrices, float bodyYaw, float baseHeight) {
        super.setupTransforms(state, matrices, bodyYaw, baseHeight);
        // Shift the model up so it renders centred on the entity's bounding box (height / 2)
        matrices.translate(0.0F, state.height / 2.0F, 0.0F);
    }

    @Override
    public Identifier getTexture(SummonedEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    protected RenderLayer getRenderLayer(SummonedEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        if (showOutline) {
            return RenderLayers.outlineNoCull(TEXTURE);
        }
        return renderLayer;
    }
}
