package net.wizards.client.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;
import net.wizards.entity.FireHydraEntity;

import java.util.ArrayList;
import java.util.List;

public class FireHydraRenderer extends MobEntityRenderer<FireHydraEntity, FireHydraModel> {
    public static final Identifier TEXTURE =
            new Identifier(WizardsMod.ID, "textures/entity/fire_hydra.png");

    // The Fire Hydra's texture is uniformly 50% alpha - it is meant to be seen through - so its layer
    // blends and deliberately does NOT write depth (`writeMaskState(COLOR_MASK)`, see
    // `CustomLayers.spellObject(.., translucent = true)`). A pass that writes no depth leaves nothing
    // behind for later passes to test against: wherever the model covers sky rather than terrain, the
    // depth buffer still holds the far plane there, so every world pass drawn after it paints straight
    // over the model.
    //
    // That is why chasing the render stage never fixed this. Drawing in the normal entity pass let
    // translucent terrain (water) paint over it; moving after translucent terrain handed the problem to
    // particles; moving after particles handed it to clouds and weather, which are always last. The only
    // stable answer for a non-depth-writing model is to draw it after *everything* the world renderer
    // draws: we queue the entity during the normal pass and replay the full model render once the world
    // render is complete. It is still depth-tested, so terrain, water and clouds genuinely in front of it
    // still occlude it - it simply can no longer be overpainted by a pass that comes later.
    private static final List<Deferred> deferredQueue = new ArrayList<>();
    private boolean inDeferredPass = false;

    private record Deferred(FireHydraEntity entity, float yaw, float tickDelta, int light) {}

    /// Replays the queued Fire Hydra renders once the world render has finished. Loader-neutral - each
    /// platform's client entrypoint calls this from the event that fires at that exact point: Fabric
    /// `WorldRenderEvents.END` (injected at the RETURN of `WorldRenderer#render`), Forge
    /// `RenderLevelStageEvent.Stage.AFTER_LEVEL` (dispatched immediately after that same call returns).
    /// Both sit after clouds, weather and the fabulous-graphics transparency compositing, and before the
    /// depth buffer is cleared for the held item - so the two loaders draw at the same instruction.
    public static void renderAfterWorld(MatrixStack matrices, Camera camera, float tickDelta) {
        if (deferredQueue.isEmpty()) {
            return;
        }
        var client = MinecraftClient.getInstance();
        var dispatcher = client.getEntityRenderDispatcher();
        var vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
        Vec3d cam = camera.getPos();

        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);
        for (Deferred d : deferredQueue) {
            FireHydraEntity entity = d.entity();
            if (!(dispatcher.getRenderer(entity) instanceof FireHydraRenderer renderer)) {
                continue;
            }
            double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
            double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
            double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
            matrices.push();
            matrices.translate(x, y, z);
            renderer.inDeferredPass = true;
            renderer.render(entity, d.yaw(), d.tickDelta(), matrices, vertexConsumers, d.light());
            renderer.inDeferredPass = false;
            matrices.pop();
        }
        matrices.pop();
        vertexConsumers.draw();
        deferredQueue.clear();
    }

    public FireHydraRenderer(EntityRendererFactory.Context context) {
        super(context, new FireHydraModel(context.getPart(FireHydraModel.LAYER)), 0.75f);
        //this.addFeature(new FireHydraGlowFeatureRenderer(this));
    }

    @Override
    public void render(FireHydraEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!inDeferredPass && entity.isAlive()) {
            // Queue for the end-of-world pass instead of drawing now.
            deferredQueue.add(new Deferred(entity, yaw, tickDelta, light));
            return;
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(FireHydraEntity entity) {
        return TEXTURE;
    }

//    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW_TRANSLUCENT, false);
    public static final RenderLayer renderLayer = CustomLayers.spellObject(TEXTURE, LightEmission.GLOW, true);
    @Override
    protected RenderLayer getRenderLayer(FireHydraEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        if (showOutline) {
            return RenderLayer.getOutline(TEXTURE);
        }
        return renderLayer;
    }
}
