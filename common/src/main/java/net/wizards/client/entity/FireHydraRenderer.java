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
import net.spell_engine.client.compatibility.ShaderCompatibility;
import net.wizards.WizardsMod;
import net.wizards.entity.FireHydraEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jspecify.annotations.Nullable;

public class FireHydraRenderer
        extends MobRenderer<FireHydraEntity, SummonedEntityRenderState, FireHydraModel> {
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "textures/entity/fire_hydra.png");

    // The Fire Hydra body is translucent. Submitted normally it lands in the `translucentModels` phase,
    // which vanilla executes before translucent terrain and before the translucent particle pass, so
    // water/distant terrain and the hydra's own puddle particles paint over it. To avoid that, the body
    // is not submitted during the entity pass: `getRenderType` returns null and the model is submitted
    // by hand into the `afterTerrain` phase, which runs after `renderGroup(TRANSLUCENT)` (see
    // `LevelRenderer#addMainPass`). Labels, shadow and the outline (glowing) pass still take the normal
    // path.
    //
    // Two ordering details inside that phase (26.2 in-game review, 2026-09-04):
    // - Translucent particles are submitted into `afterTerrain` of order bucket 0, and within one bucket
    //   the feature groups execute in feature-type registration order, which puts particles after a
    //   model. The body therefore goes into order bucket `BODY_ORDER` (> 0): `executeTranslucentAfterTerrain`
    //   walks the buckets ascending, so the body is drawn after every order-0 particle and blends over the
    //   puddle instead of being covered by it.
    // - Without a shader pack the body layer writes depth (`CustomLayers.beam(…, true)`: beacon-beam program,
    //   alpha blend, no cull, depth write). Clouds are a separate pass after the main pass, depth-tested
    //   against the main target; a non-depth-writing body leaves nothing for them to test against and they
    //   paint straight over it. Quads are sorted back to front on upload, so the depth write does not hide
    //   the body's own farther parts. With a shader pack the non-depth-writing GLOW layer stays: the pack's
    //   compositing was reviewed and approved on it, and Iris draws its own clouds.
    //
    // 26.2: `MultiBufferSource` is gone, so the former "replay the model from a world render event"
    // hack is both impossible and unnecessary — the submit-node phases express the ordering directly.
    // `SubmitNodeCollector#order(int)` and `SubmitNodeCollection#afterTerrain` are public, so no
    // loader-specific API is needed.
    private static final int BODY_ORDER = 1;

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
        SubmitNodeCollection collection = queue.order(BODY_ORDER) instanceof SubmitNodeCollection c ? c : null;
        this.deferBody = collection != null && state.deathTime <= 0.0F && !state.isInvisible;
        this.activePoseStack = matrices;
        this.deferredPose = null;
        super.submit(state, matrices, queue, cameraState);
        if (this.deferBody && this.deferredPose != null) {
            collection.afterTerrain.submit(new ModelFeatureRenderer.Submit<>(
                    bodyLayer(),
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

    /// Shader pack: translucent GLOW spell-object layer (no depth write). No pack: the depth-writing translucent
    /// beam layer, see the class comment. Both are memoized in `CustomLayers`.
    private static RenderType bodyLayer() {
        return ShaderCompatibility.isShaderPackInUse()
                ? CustomLayers.spellObject(TEXTURE, LightEmission.GLOW, true)
                : CustomLayers.beam(TEXTURE, false, true);
    }

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
        return this.deferBody ? null : bodyLayer();
    }
}
