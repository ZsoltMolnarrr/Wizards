package net.wizards.client.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;
import net.wizards.entity.FireHydraEntity;

import java.util.ArrayList;
import java.util.List;

public class FireHydraRenderer
        extends MobEntityRenderer<FireHydraEntity, SummonedEntityRenderState, FireHydraModel> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/fire_hydra.png");

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
    public static void renderAfterTranslucent(MatrixStack matrices, Camera camera, float tickDelta) {
        if (deferredQueue.isEmpty()) {
            return;
        }
        var vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        Vec3d cam = camera.getCameraPos();

        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);
        for (Deferred deferred : deferredQueue) {
            var state = deferred.state();
            matrices.push();
            matrices.translate(state.x, state.y, state.z);
            drawBody(deferred.model(), state, matrices, vertexConsumers.getBuffer(renderLayer));
            matrices.pop();
        }
        matrices.pop();
        vertexConsumers.draw();
        deferredQueue.clear();
    }

    /// Reproduces `LivingEntityRenderer#render`'s transform chain for the model draw only (the
    /// sleeping / riptide / upside-down branches never apply to this summon).
    private static void drawBody(FireHydraModel model, SummonedEntityRenderState state,
                                 MatrixStack matrices, VertexConsumer vertices) {
        matrices.push();
        float scale = state.baseScale;
        matrices.scale(scale, scale, scale);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - state.bodyYaw));
        matrices.scale(-1.0F, -1.0F, 1.0F);
        matrices.translate(0.0F, -1.501F, 0.0F);
        model.setAngles(state);
        model.render(matrices, vertices, state.light, LivingEntityRenderer.getOverlay(state, 0.0F), -1);
        matrices.pop();
    }

    /// Set for the duration of one `render` call so `getRenderLayer` — which the superclass calls
    /// from inside it — knows whether this frame's body is deferred. Render is single-threaded.
    private boolean deferBody = false;

    public FireHydraRenderer(EntityRendererFactory.Context context) {
        super(context, new FireHydraModel(context.getPart(FireHydraModel.LAYER)), 0.75f);
        //this.addFeature(new FireHydraGlowFeatureRenderer(this));
    }

    @Override
    public SummonedEntityRenderState createRenderState() {
        return new SummonedEntityRenderState();
    }

    @Override
    public void updateRenderState(FireHydraEntity entity, SummonedEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.copyFrom(entity);
        state.attackAnimationSpeed = entity.getAttackAnimationSpeed(FireHydraModel.attackAnimationLengthTicks());
        state.spellReleaseAnimationSpeed = entity.getSpellReleaseAnimationSpeed(
                FireHydraModel.spellReleaseAnimationLengthTicks(state.spellReleaseVariant));
    }

    @Override
    public void render(SummonedEntityRenderState state, MatrixStack matrices,
                       OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        // A dying hydra is drawn inline (the death animation is short and ordering hardly matters),
        // matching the 1.21.1 behaviour of only deferring live entities.
        this.deferBody = state.deathTime <= 0.0F && !state.invisible;
        super.render(state, matrices, queue, cameraState);
        if (this.deferBody) {
            deferredQueue.add(new Deferred(this.getModel(), state));
        }
        this.deferBody = false;
    }

    @Override
    public Identifier getTexture(SummonedEntityRenderState state) {
        return TEXTURE;
    }

//    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW_TRANSLUCENT, false);
    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW, true);

    @Override
    protected RenderLayer getRenderLayer(SummonedEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        if (showOutline) {
            return RenderLayers.outlineNoCull(TEXTURE);
        }
        return this.deferBody ? null : renderLayer;
    }
}
