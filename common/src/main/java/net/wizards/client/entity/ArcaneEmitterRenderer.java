package net.wizards.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;
import net.wizards.entity.ArcaneEmitterEntity;

public class ArcaneEmitterRenderer extends MobEntityRenderer<ArcaneEmitterEntity, ArcaneEmitterModel> {
    public static final Identifier TEXTURE =
            new Identifier(WizardsMod.ID, "textures/entity/arcane_emitter.png");
    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW_TRANSLUCENT, false);
    // RenderLayer.getEntityTranslucentEmissive(TEXTURE);

    public ArcaneEmitterRenderer(EntityRendererFactory.Context context) {
        super(context, new ArcaneEmitterModel(context.getPart(ArcaneEmitterModel.LAYER)), 0f);
    }

    @Override
    protected void setupTransforms(ArcaneEmitterEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        // 1.20.1 `LivingEntityRenderer#setupTransforms` has no trailing `float scale` parameter.
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        // Shift the model up so it renders centred on the entity's bounding box (height / 2)
        matrices.translate(0.0, entity.getHeight() / 2.0, 0.0);
    }

    @Override
    public Identifier getTexture(ArcaneEmitterEntity entity) {
        return TEXTURE;
    }

    @Override
    protected RenderLayer getRenderLayer(ArcaneEmitterEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        if (showOutline) {
            return RenderLayer.getOutline(TEXTURE);
        }
        return renderLayer;
    }
}
