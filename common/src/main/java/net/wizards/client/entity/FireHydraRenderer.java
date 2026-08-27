package net.wizards.client.entity;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;
import net.wizards.entity.FireHydraEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;

public class FireHydraRenderer
        extends MobRenderer<FireHydraEntity, SummonedEntityRenderState, FireHydraModel> {
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "textures/entity/fire_hydra.png");

    // The Fire Hydra uses a translucent, non-depth-writing render layer. During the normal entity
    // pass (which runs before translucent terrain, particles and clouds) the later passes paint over
    // it, so parts of the model appear behind water/clouds/distant terrain. To avoid that, the body
    // is not submitted during the entity pass: `getRenderLayer` returns null and the render state is
    // queued for a manual model draw AFTER translucent geometry, so it rasterizes on top. Labels,
    // shadow and the outline (glowing) pass still take the normal path.
    //
    // 1.21.9+ rendering is queue-based and a whole entity render can no longer simply be replayed
    // from a world-render event, so the deferred pass draws the model straight into the entity
    // vertex consumers — the same technique SpellEngine's BeamRenderer uses. Render states are
    // freshly allocated per entity per frame (`EntityRenderer#getAndUpdateRenderState`), so holding
    // on to them until the end of the frame is safe.
    private record Deferred(FireHydraModel model, SummonedEntityRenderState state) {}
    private static final List<Deferred> deferredQueue = new ArrayList<>();

    /// Draws the queued Fire Hydra bodies during the world's after-translucent pass. Loader-neutral —
    /// each platform's client entrypoint calls this from its own event (Fabric
    /// `WorldRenderEvents.END_MAIN`; NeoForge `RenderLevelStageEvent.AfterParticles`).
    public static void renderAfterTranslucent(PoseStack matrices, Camera camera, float tickDelta) {
        if (deferredQueue.isEmpty()) {
            return;
        }
        var vertexConsumers = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 cam = camera.position();

        matrices.pushPose();
        matrices.translate(-cam.x, -cam.y, -cam.z);
        for (Deferred deferred : deferredQueue) {
            var state = deferred.state();
            matrices.pushPose();
            matrices.translate(state.x, state.y, state.z);
            drawBody(deferred.model(), state, matrices, vertexConsumers.getBuffer(renderLayer));
            matrices.popPose();
        }
        matrices.popPose();
        vertexConsumers.endBatch();
        deferredQueue.clear();
    }

    /// Reproduces `LivingEntityRenderer#render`'s transform chain for the model draw only (the
    /// sleeping / riptide / upside-down branches never apply to this summon).
    private static void drawBody(FireHydraModel model, SummonedEntityRenderState state,
                                 PoseStack matrices, VertexConsumer vertices) {
        matrices.pushPose();
        float scale = state.scale;
        matrices.scale(scale, scale, scale);
        matrices.mulPose(Axis.YP.rotationDegrees(180.0F - state.bodyRot));
        matrices.scale(-1.0F, -1.0F, 1.0F);
        matrices.translate(0.0F, -1.501F, 0.0F);
        model.setupAnim(state);
        model.renderToBuffer(matrices, vertices, state.lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), -1);
        matrices.popPose();
    }

    /// Set for the duration of one `render` call so `getRenderLayer` — which the superclass calls
    /// from inside it — knows whether this frame's body is deferred. Render is single-threaded.
    private boolean deferBody = false;

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
        this.deferBody = state.deathTime <= 0.0F && !state.isInvisible;
        super.submit(state, matrices, queue, cameraState);
        if (this.deferBody) {
            deferredQueue.add(new Deferred(this.getModel(), state));
        }
        this.deferBody = false;
    }

    @Override
    public Identifier getTextureLocation(SummonedEntityRenderState state) {
        return TEXTURE;
    }

//    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW_TRANSLUCENT, false);
    public static final RenderType renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW, true);

    @Override
    protected RenderType getRenderType(SummonedEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        if (showOutline) {
            return RenderTypes.outline(TEXTURE);
        }
        return this.deferBody ? null : renderLayer;
    }
}
