package net.wizards.client.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.wizards.WizardsMod;
import net.wizards.entity.FrostElementalEntity;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class FrostElementalModel extends SinglePartEntityModel<FrostElementalEntity> {
	private final ModelPart elemental;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart tail_end;
	private final ModelPart chest;
	private final ModelPart right_arm;
	private final ModelPart right_forearm;
	private final ModelPart right_finger_2;
	private final ModelPart right_finger_1;
	private final ModelPart right_finger_3;
	private final ModelPart left_arm;
	private final ModelPart left_forearm;
	private final ModelPart left_finger_2;
	private final ModelPart left_finger_1;
	private final ModelPart left_finger_3;
	private final ModelPart head;

	public FrostElementalModel(ModelPart root) {
		this.elemental = root.getChild("elemental");
		this.body = this.elemental.getChild("body");
		this.tail = this.body.getChild("tail");
		this.tail_end = this.tail.getChild("tail_end");
		this.chest = this.body.getChild("chest");
		this.right_arm = this.chest.getChild("right_arm");
		this.right_forearm = this.right_arm.getChild("right_forearm");
		this.right_finger_2 = this.right_forearm.getChild("right_finger_2");
		this.right_finger_1 = this.right_forearm.getChild("right_finger_1");
		this.right_finger_3 = this.right_forearm.getChild("right_finger_3");
		this.left_arm = this.chest.getChild("left_arm");
		this.left_forearm = this.left_arm.getChild("left_forearm");
		this.left_finger_2 = this.left_forearm.getChild("left_finger_2");
		this.left_finger_1 = this.left_forearm.getChild("left_finger_1");
		this.left_finger_3 = this.left_forearm.getChild("left_finger_3");
		this.head = this.chest.getChild("head");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData elemental = modelPartData.addChild("elemental", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 12.0F, 0.0F));

		ModelPartData body = elemental.addChild("body", ModelPartBuilder.create().uv(0, 35).cuboid(-5.0F, -10.0F, -4.0F, 10.0F, 10.0F, 6.0F, new Dilation(0.0F))
		.uv(96, 21).cuboid(-5.0F, -10.0F, -4.0F, 10.0F, 10.0F, 6.0F, new Dilation(0.3F)), ModelTransform.pivot(0.0F, 2.0F, 2.0F));

		ModelPartData tail = body.addChild("tail", ModelPartBuilder.create().uv(0, 51).cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 9.0F, 4.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, -3.0F));

		ModelPartData tail_end = tail.addChild("tail_end", ModelPartBuilder.create().uv(20, 51).cuboid(-3.0F, -2.0F, 0.0F, 6.0F, 3.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.1585F, 1.7696F));

		ModelPartData chest = body.addChild("chest", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -9.0F, -2.0F));

		ModelPartData cube_r1 = chest.addChild("cube_r1", ModelPartBuilder.create().uv(28, 3).mirrored().cuboid(-8.2F, -8.2F, 6.4F, 4.0F, 4.0F, 7.0F, new Dilation(0.1F)).mirrored(false)
		.uv(28, 3).cuboid(4.2F, -8.2F, 6.4F, 4.0F, 4.0F, 7.0F, new Dilation(0.1F))
		.uv(74, 0).cuboid(-8.0F, -8.0F, -5.0F, 16.0F, 10.0F, 11.0F, new Dilation(0.3F))
		.uv(0, 14).cuboid(-8.0F, -8.0F, -5.0F, 16.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 1.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData cube_r2 = chest.addChild("cube_r2", ModelPartBuilder.create().uv(32, 37).mirrored().cuboid(-8.4F, -9.4924F, 2.2213F, 4.0F, 4.0F, 8.0F, new Dilation(0.11F)).mirrored(false)
		.uv(32, 37).cuboid(4.0F, -9.4924F, 2.2213F, 4.0F, 4.0F, 8.0F, new Dilation(0.11F)), ModelTransform.of(0.2F, -1.0F, 1.0F, 1.1781F, 0.0F, 0.0F));

		ModelPartData right_arm = chest.addChild("right_arm", ModelPartBuilder.create().uv(54, 10).mirrored().cuboid(-3.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
		.uv(80, 48).mirrored().cuboid(-3.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.3F)).mirrored(false)
		.uv(56, 0).mirrored().cuboid(-3.0F, -2.0F, 2.6F, 5.0F, 4.0F, 6.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.pivot(-10.0F, -8.0F, -0.5F));

		ModelPartData right_forearm = right_arm.addChild("right_forearm", ModelPartBuilder.create().uv(54, 26).mirrored().cuboid(-5.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
		.uv(54, 45).mirrored().cuboid(-5.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.pivot(0.0F, 10.0F, 0.0F));

		ModelPartData right_finger_2 = right_forearm.addChild("right_finger_2", ModelPartBuilder.create().uv(0, 14).mirrored().cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-4.0F, 11.0F, 1.5F));

		ModelPartData right_finger_1 = right_forearm.addChild("right_finger_1", ModelPartBuilder.create().uv(0, 14).mirrored().cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-4.0F, 11.0F, -2.5F));

		ModelPartData right_finger_3 = right_forearm.addChild("right_finger_3", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 11.0F, -0.5F));

		ModelPartData left_arm = chest.addChild("left_arm", ModelPartBuilder.create().uv(54, 10).cuboid(-2.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.0F))
		.uv(80, 48).cuboid(-2.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.3F))
		.uv(56, 0).cuboid(-2.0F, -2.0F, 2.6F, 5.0F, 4.0F, 6.0F, new Dilation(0.3F)), ModelTransform.pivot(10.0F, -8.0F, -0.5F));

		ModelPartData left_forearm = left_arm.addChild("left_forearm", ModelPartBuilder.create().uv(54, 26).cuboid(-1.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new Dilation(0.0F))
		.uv(54, 45).cuboid(-1.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new Dilation(0.3F)), ModelTransform.pivot(0.0F, 10.0F, 0.0F));

		ModelPartData left_finger_2 = left_forearm.addChild("left_finger_2", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, 0.0F, -1.25F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, 11.0F, 1.25F));

		ModelPartData left_finger_1 = left_forearm.addChild("left_finger_1", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, 11.0F, -2.5F));

		ModelPartData left_finger_3 = left_forearm.addChild("left_finger_3", ModelPartBuilder.create().uv(0, 14).mirrored().cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 11.0F, -0.5F));

		ModelPartData head = chest.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.5F, -3.0F, -5.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F))
		.uv(99, 44).cuboid(-4.5F, -3.0F, -6.0F, 9.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, -5.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	// Hand-written section

	public static final EntityModelLayer TEXTURE = new EntityModelLayer(Identifier.of(WizardsMod.ID, "frost_elemental"), "main");

	@Override
	public void setAngles(FrostElementalEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float netHeadPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(netHeadYaw, netHeadPitch);
		this.animateMovement(FrostElementalAnimations.Walk, limbSwing, limbSwingAmount, 2F, 2.5F);
		this.updateAnimation(entity.spawnAnimationState,   FrostElementalAnimations.Spawn,   ageInTicks, 1F);
		this.updateAnimation(entity.despawnAnimationState, FrostElementalAnimations.Despawn, ageInTicks, 1F);
		this.updateAnimation(entity.idleAnimationState,    FrostElementalAnimations.idle,    ageInTicks, 1F);
		this.updateAnimation(entity.moveAnimationState,    FrostElementalAnimations.Walk,    ageInTicks, 1F);
		this.updateAnimation(entity.attackAnimationState,  FrostElementalAnimations.Attack,  ageInTicks, 1F);
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -60, 60);
		headPitch = MathHelper.clamp(headPitch, -60, 60);
		head.yaw = headYaw * 0.017453292F;
		head.pitch = headPitch * 0.017453292F;
	}

	@Override
	public ModelPart getPart() {
		return elemental;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		elemental.render(matrices, vertices, light, overlay, color);
	}
}