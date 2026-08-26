package net.wizards.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;
import net.wizards.entity.ArcaneEmitterEntity;

public class ArcaneEmitterRenderer
        extends MobRenderer<ArcaneEmitterEntity, SummonedEntityRenderState, ArcaneEmitterModel> {
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "textures/entity/arcane_emitter.png");
    public static final RenderType renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW_TRANSLUCENT, false);
    // RenderLayer.getEntityTranslucentEmissive(TEXTURE);

    public ArcaneEmitterRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneEmitterModel(context.bakeLayer(ArcaneEmitterModel.LAYER)), 0f);
    }

    @Override
    public SummonedEntityRenderState createRenderState() {
        return new SummonedEntityRenderState();
    }

    @Override
    public void extractRenderState(ArcaneEmitterEntity entity, SummonedEntityRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.copyFrom(entity);
    }

    @Override
    protected void setupRotations(SummonedEntityRenderState state, PoseStack matrices, float bodyYaw, float baseHeight) {
        super.setupRotations(state, matrices, bodyYaw, baseHeight);
        // Shift the model up so it renders centred on the entity's bounding box (height / 2)
        matrices.translate(0.0F, state.boundingBoxHeight / 2.0F, 0.0F);
    }

    @Override
    public Identifier getTextureLocation(SummonedEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    protected RenderType getRenderType(SummonedEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        if (showOutline) {
            return RenderTypes.outline(TEXTURE);
        }
        return renderLayer;
    }
}
