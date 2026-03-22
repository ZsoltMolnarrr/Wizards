package net.wizards.client.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;
import net.wizards.entity.FrostElementalEntity;

public class FrostElementalRenderer extends MobEntityRenderer<FrostElementalEntity, FrostElementalModel<FrostElementalEntity>> {
    public static final Identifier TEXTURE =
            Identifier.of(WizardsMod.ID, "textures/entity/frost_elemental.png");

    public FrostElementalRenderer(EntityRendererFactory.Context context) {
        super(context, new FrostElementalModel<>(context.getPart(FrostElementalModel.MANTIS)), 0.75f);
    }

    @Override
    public Identifier getTexture(FrostElementalEntity entity) {
        return TEXTURE;
    }
}
