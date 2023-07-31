package net.wizards.client.armor;

import net.wizards.item.WizardArmor;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WizardArmorRenderer extends GeoArmorRenderer<WizardArmor> {
    public WizardArmorRenderer() {
        super(new WizardArmorModel());

        //These values are what each bone name is in blockbench. So if your head bone is named "bone545",
        // make sure to do this.headBone = "bone545";

        // The default values are the ones that come with the default armor template in the geckolib blockbench plugin.
//        this.headBone = "armorHead";
//        this.bodyBone = "armorBody";
//        this.rightArmBone = "armorRightArm";
//        this.leftArmBone = "armorLeftArm";
//        this.rightLegBone = "armorRightLeg";
//        this.leftLegBone = "armorLeftLeg";
//        this.rightBootBone = "armorRightBoot";
//        this.leftBootBone = "armorLeftBoot";
    }

//    @Override
//    public RenderLayer getRenderType(WizardArmor animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
//        return RenderLayer.getEntityTranslucent(texture);
//    }
}
