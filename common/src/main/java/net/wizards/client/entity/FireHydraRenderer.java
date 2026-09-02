package net.wizards.client.entity;

import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;
import net.wizards.entity.FireHydraEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jspecify.annotations.Nullable;

public class FireHydraRenderer
        extends MobRenderer<FireHydraEntity, SummonedEntityRenderState, FireHydraModel> {
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "textures/entity/fire_hydra.png");

    // The Fire Hydra uses a translucent, non-depth-writing render layer. Submitted normally it lands in
    // the `translucentModels` phase, which vanilla executes before translucent terrain and before the
    // translucent particle pass, so water/clouds/distant terrain and the hydra's own puddle particles
    // paint over it. To avoid that, the body is not submitted during the entity pass: `getRenderType`
    // returns null and the model is submitted by hand into the `afterTerrain` phase, which runs after
    // `renderGroup(TRANSLUCENT)` (see `LevelRenderer#addMainPass`). Labels, shadow and the outline
    // (glowing) pass still take the normal path.
    //
    // 26.2: `MultiBufferSource` is gone, so the former "replay the model from a world render event"
    // hack is both impossible and unnecessary — the submit-node phases express the ordering directly.
    // `SubmitNodeCollector#order(0)` is the collection vanilla's own `submitModel` writes into, and
    // `SubmitNodeCollection#afterTerrain` is public, so no loader-specific API is needed.

    /// Set for the duration of one `submit` call so `getRenderType` — which the superclass calls from
    /// inside it, with the model transform already applied to the pose stack — knows whether this
    /// frame's body is deferred, and can hand back the pose to submit it with. Render is
    /// single-threaded, and render states are freshly allocated per entity per frame
    /// (`EntityRenderer#getAndUpdateRenderState`), so keeping the node until the frame is drawn is safe.
    private boolean deferBody = false;
    private @Nullable PoseStack activePoseStack = null;
    private PoseStack.@Nullable Pose deferredPose = null;

    public FireHydraRenderer(EntityRendererProvider.Context context) {
        super(context, new FireHydraModel(context.bakeLayer(FireHydraModel.LAYER)), 0.75f);
        //this.addFeature(new FireHydraGlowFeatureRenderer(this));
    }

    @Override
    public SummonedEntityRenderState createRenderState() {
        return new SummonedEntityRenderState();
    }

    @Override
    public void extractRenderState(FireHydraEntity entity, SummonedEntityRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.copyFrom(entity);
        state.attackAnimationSpeed = entity.getAttackAnimationSpeed(FireHydraModel.attackAnimationLengthTicks());
        state.spellReleaseAnimationSpeed = entity.getSpellReleaseAnimationSpeed(
                FireHydraModel.spellReleaseAnimationLengthTicks(state.spellReleaseVariant));
    }

    @Override
    public void submit(SummonedEntityRenderState state, PoseStack matrices,
                       SubmitNodeCollector queue, CameraRenderState cameraState) {
        // A dying hydra is drawn inline (the death animation is short and ordering hardly matters),
        // matching the 1.21.1 behaviour of only deferring live entities.
        SubmitNodeCollection collection = queue.order(0) instanceof SubmitNodeCollection c ? c : null;
        this.deferBody = collection != null && state.deathTime <= 0.0F && !state.isInvisible;
        this.activePoseStack = matrices;
        this.deferredPose = null;
        super.submit(state, matrices, queue, cameraState);
        if (this.deferBody && this.deferredPose != null) {
            collection.afterTerrain.submit(new ModelFeatureRenderer.Submit<>(
                    renderLayer,
                    this.deferredPose,
                    this.getModel(),
                    state,
                    state.lightCoords,
                    LivingEntityRenderer.getOverlayCoords(state, 0.0F),
                    -1,
                    null,
                    null));
        }
        this.deferBody = false;
        this.activePoseStack = null;
        this.deferredPose = null;
    }

    @Override
    public Identifier getTextureLocation(SummonedEntityRenderState state) {
        return TEXTURE;
    }

//    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW_TRANSLUCENT, false);
    public static final RenderType renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW, true);

    @Override
    protected RenderType getRenderType(SummonedEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        if (this.deferBody && this.activePoseStack != null) {
            // Called from `LivingEntityRenderer#submit` after scale / rotation / the -1.501 offset have
            // been applied — exactly the pose vanilla would have submitted the body with.
            this.deferredPose = this.activePoseStack.last().copy();
        }
        if (showOutline) {
            return RenderTypes.outline(TEXTURE);
        }
        return this.deferBody ? null : renderLayer;
    }
}
