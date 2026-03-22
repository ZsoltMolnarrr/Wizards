package net.wizards.client.entity;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.wizards.entity.FrostElementalEntity;

public class FrostElementalRenderer extends EntityRenderer<FrostElementalEntity> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/frost_elemental.png");

    public FrostElementalRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(FrostElementalEntity entity) {
        return TEXTURE;
    }
}
