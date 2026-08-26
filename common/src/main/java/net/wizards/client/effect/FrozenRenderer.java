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

public class FrozenRenderer implements CustomModelStatusEffect.Renderer {

    // MARK: Renderer
    private static final RenderType RENDER_LAYER = CustomLayers.spellEffect(LightEmission.RADIATE, false);

    public static final Identifier modelId = Identifier.fromNamespaceAndPath(WizardsMod.ID, "spell_effect/frost_trap");
    @Override
    public void renderEffect(long appliedAtWorldTime, int amplifier, LivingEntity livingEntity, float delta, PoseStack matrixStack, SubmitNodeCollector queue, int light) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0.5, 0);
        CustomModels.render(RENDER_LAYER, modelId,
                matrixStack, queue, light, livingEntity.getId());
        matrixStack.popPose();
    }
}
