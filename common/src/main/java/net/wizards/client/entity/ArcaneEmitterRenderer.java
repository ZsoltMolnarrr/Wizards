package net.wizards.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.wizards.entity.ArcaneEmitterEntity;

public class ArcaneEmitterRenderer extends MobEntityRenderer<ArcaneEmitterEntity, ArcaneEmitterModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/arcane_emitter.png");

    public ArcaneEmitterRenderer(EntityRendererFactory.Context context) {
        super(context, new ArcaneEmitterModel(context.getPart(ArcaneEmitterModel.LAYER)), 0.5f);
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
        return RenderLayer.getEntityTranslucentEmissive(TEXTURE);
    }
}
