package net.wizards.effect;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.CustomLayers;
import net.wizards.WizardsMod;

import java.util.List;

public class ArcaneChargeRenderer extends OrbitingEffectRenderer {
    public static final Identifier modelId = new Identifier(WizardsMod.ID, "effect/arcane_charge");
    private static final RenderLayer GLOWING_RENDER_LAYER =
            CustomLayers.projectile(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true);

    public ArcaneChargeRenderer() {
        super(List.of(new Model(GLOWING_RENDER_LAYER, modelId)), 0.5F, 0.6F);
    }
}
