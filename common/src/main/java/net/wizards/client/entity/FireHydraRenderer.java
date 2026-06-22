package net.wizards.client.entity;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
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
            Identifier.of(WizardsMod.ID, "textures/entity/fire_hydra.png");

    // The Fire Hydra uses a translucent, non-depth-writing render layer. During the normal entity
    // pass (which runs before translucent terrain, particles and clouds) the later passes paint over
    // it, so parts of the model appear behind water/clouds/distant terrain. To fix this we don't draw
    // during the normal pass: we queue the entity and replay the full model render AFTER_TRANSLUCENT,
    // so it rasterizes on top. Mirrors Paladins' BarrierEntityRenderer.
    private static final List<Deferred> deferredQueue = new ArrayList<>();
    private boolean inDeferredPass = false;

    private record Deferred(FireHydraEntity entity, float yaw, float tickDelta, int light) {}

    public static void setup() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (deferredQueue.isEmpty()) {
                return;
            }
            var client = MinecraftClient.getInstance();
            var dispatcher = client.getEntityRenderDispatcher();
            var vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
            var matrices = context.matrixStack();
            Vec3d cam = context.camera().getPos();
            float tickDelta = context.tickCounter().getTickDelta(true);

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
        });
    }

    public FireHydraRenderer(EntityRendererFactory.Context context) {
        super(context, new FireHydraModel(context.getPart(FireHydraModel.LAYER)), 0.75f);
        //this.addFeature(new FireHydraGlowFeatureRenderer(this));
    }

    @Override
    public void render(FireHydraEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!inDeferredPass && entity.isAlive()) {
            // Queue for the AFTER_TRANSLUCENT pass instead of drawing now.
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
