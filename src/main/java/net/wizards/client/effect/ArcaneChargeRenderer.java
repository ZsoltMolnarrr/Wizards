package net.wizards.client.effect;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.render.OrbitingEffectRenderer;
import net.wizards.WizardsMod;

import java.util.List;

public class ArcaneChargeRenderer extends OrbitingEffectRenderer {
    public static final Identifier modelId = new Identifier(WizardsMod.ID, "effect/arcane_charge");
    private static final RenderLayer GLOWING_RENDER_LAYER =
            CustomLayers.spellEffect(LightEmission.GLOW, true);

    public ArcaneChargeRenderer() {
        super(List.of(new Model(GLOWING_RENDER_LAYER, modelId)), 0.5F, 0.6F);
    }
}
