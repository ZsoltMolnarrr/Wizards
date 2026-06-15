package net.wizards.client.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;
import net.wizards.entity.ArcaneEmitterEntity;
import net.wizards.entity.FireHydraEntity;

public class FireHydraRenderer extends MobEntityRenderer<FireHydraEntity, FireHydraModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/fire_hydra.png");


    public FireHydraRenderer(EntityRendererFactory.Context context) {
        super(context, new FireHydraModel(context.getPart(FireHydraModel.LAYER)), 0.75f);
        //this.addFeature(new FireHydraGlowFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(FireHydraEntity entity) {
        return TEXTURE;
    }

//    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW_TRANSLUCENT, false);
public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW, false);
    @Override
    protected RenderLayer getRenderLayer(FireHydraEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        if (showOutline) {
            return RenderLayer.getOutline(TEXTURE);
        }
        return renderLayer;
    }
}
