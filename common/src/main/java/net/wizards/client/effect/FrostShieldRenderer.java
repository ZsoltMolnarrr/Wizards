package net.wizards.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;

public class FrostShieldRenderer implements CustomModelStatusEffect.Renderer {
    public static final Identifier modelId_base = Identifier.fromNamespaceAndPath(WizardsMod.ID, "spell_effect/ice_block");
    public static final Identifier modelId_overlay = Identifier.fromNamespaceAndPath(WizardsMod.ID, "spell_effect/ice_block");

    private static final RenderType BASE_RENDER_LAYER = CustomLayers.spellObject(LightEmission.GLOW_TRANSLUCENT);
            // CustomLayers.spellEffect(LightEmission.RADIATE, true);
            //RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    private static final RenderType OVERLAY_RENDER_LAYER = CustomLayers.spellEffect(LightEmission.RADIATE, false);

    @Override
    public void renderEffect(long appliedAtWorldTime, int amplifier, LivingEntity livingEntity, float delta, PoseStack matrixStack, SubmitNodeCollector queue, int light) {
        float yOffset = 1.15F; // y + 0.01 to avoid Y fighting
        matrixStack.pushPose();
        matrixStack.translate(0, yOffset, 0); // y + 0.01 to avoid Y fighting
        CustomModels.render(BASE_RENDER_LAYER, modelId_base,
                matrixStack, queue, light, livingEntity.getId());
        matrixStack.popPose();

//        float overlayScale = 1.05F;
//        matrixStack.push();
//        matrixStack.translate(0, yOffset, 0); // y + 0.01 to avoid Y fighting
//        matrixStack.scale(overlayScale, overlayScale, overlayScale);
//        CustomModels.render(OVERLAY_RENDER_LAYER, MinecraftClient.getInstance().getItemRenderer(), modelId_overlay,
//                matrixStack, vertexConsumers, light, livingEntity.getId());
//        matrixStack.pop();
    }
}
