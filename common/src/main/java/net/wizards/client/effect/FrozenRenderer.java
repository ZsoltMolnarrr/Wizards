package net.wizards.client.effect;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.render.LightEmission;
import net.wizards.WizardsMod;

public class FrozenRenderer implements CustomModelStatusEffect.Renderer {

    // MARK: Renderer
    private static final RenderLayer RENDER_LAYER = CustomLayers.spellEffect(LightEmission.RADIATE, false);

    public static final Identifier modelId = Identifier.of(WizardsMod.ID, "spell_effect/frost_trap");
    @Override
    public void renderEffect(long appliedAtWorldTime, int amplifier, LivingEntity livingEntity, float delta, MatrixStack matrixStack, OrderedRenderCommandQueue queue, int light) {
        matrixStack.push();
        matrixStack.translate(0, 0.5, 0);
        CustomModels.render(RENDER_LAYER, modelId,
                matrixStack, queue, light, livingEntity.getId());
        matrixStack.pop();
    }
}
