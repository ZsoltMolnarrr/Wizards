package net.wizards.client.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

public class ArcaneEmitterModel extends EntityModel<SummonedEntityRenderState> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(WizardsMod.ID, "arcane_emitter"), "main");

	private final ModelPart portal_part_1;
	private final ModelPart portal_part_2;
	private final ModelPart portal_part_3;
	private final ModelPart portal_part_4;
	private final ModelPart portal_part_5;
	private final ModelPart portal_part_6;
	// Keyframe clips bound to this instance's parts (1.21.2+ `AnimationDefinition` → `Animation`).
	private final Animation idleAnimation;
	private final Animation spawnAnimation;

	public ArcaneEmitterModel(ModelPart root) {
		super(root.getChild("arcane_missile_small_portal"));
		var main = this.root;
		this.portal_part_1 = main.getChild("portal_part_1");
		this.portal_part_2 = main.getChild("portal_part_2");
		this.portal_part_3 = main.getChild("portal_part_3");
		this.portal_part_4 = main.getChild("portal_part_4");
		this.portal_part_5 = main.getChild("portal_part_5");
		this.portal_part_6 = main.getChild("portal_part_6");
		this.idleAnimation = ArcaneEmitterAnimations.idle.createAnimation(main);
		this.spawnAnimation = ArcaneEmitterAnimations.spawn.createAnimation(main);
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData arcane_missile_small_portal = modelPartData.addChild("arcane_missile_small_portal", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData portal_part_1 = arcane_missile_small_portal.addChild("portal_part_1", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -14.0F, 0.0F, 28.0F, 28.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData portal_part_2 = arcane_missile_small_portal.addChild("portal_part_2", ModelPartBuilder.create().uv(0, 28).cuboid(-10.0F, -10.0F, 1.0F, 20.0F, 20.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 1.05F));

		ModelPartData portal_part_3 = arcane_missile_small_portal.addChild("portal_part_3", ModelPartBuilder.create().uv(0, 28).cuboid(-10.0F, -10.0F, -2.0F, 20.0F, 20.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -0.05F, 0.0F, 0.0F, 0.3927F));

		ModelPartData portal_part_4 = arcane_missile_small_portal.addChild("portal_part_4", ModelPartBuilder.create().uv(0, 48).cuboid(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.05F));

		ModelPartData portal_part_5 = arcane_missile_small_portal.addChild("portal_part_5", ModelPartBuilder.create().uv(40, 28).cuboid(-4.0F, -4.0F, -2.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -2.1F, 0.0F, 0.0F, -0.3927F));

		ModelPartData portal_part_6 = arcane_missile_small_portal.addChild("portal_part_6", ModelPartBuilder.create().uv(40, 28).cuboid(-4.0F, -4.0F, 2.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.3927F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(SummonedEntityRenderState state) {
		super.setAngles(state);
		this.spawnAnimation.apply(state.spawnAnimationState,   state.age,  1F);
		this.idleAnimation.apply(state.idleAnimationState,     state.age,  1F);
		this.spawnAnimation.apply(state.despawnAnimationState, state.age, -1F);
	}
}
