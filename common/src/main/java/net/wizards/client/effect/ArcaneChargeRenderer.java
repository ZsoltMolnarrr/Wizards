package net.wizards.client.effect;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.render.OrbitingEffectRenderer;
import net.wizards.WizardsMod;

import java.util.List;

public class ArcaneChargeRenderer extends OrbitingEffectRenderer {
    public static final Identifier modelId = Identifier.fromNamespaceAndPath(WizardsMod.ID, "spell_effect/arcane_charge");
    private static final RenderType GLOWING_RENDER_LAYER =
            CustomLayers.spellEffect(LightEmission.GLOW, true);

    public ArcaneChargeRenderer() {
        super(List.of(new Model(GLOWING_RENDER_LAYER, modelId)), 0.5F, 0.6F);
    }
}
