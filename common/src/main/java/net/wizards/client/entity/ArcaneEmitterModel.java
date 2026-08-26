package net.wizards.client.entity;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.wizards.WizardsMod;

public class ArcaneEmitterModel extends EntityModel<SummonedEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(WizardsMod.ID, "arcane_emitter"), "main");

	private final ModelPart portal_part_1;
	private final ModelPart portal_part_2;
	private final ModelPart portal_part_3;
	private final ModelPart portal_part_4;
	private final ModelPart portal_part_5;
	private final ModelPart portal_part_6;
	// Keyframe clips bound to this instance's parts (1.21.2+ `AnimationDefinition` → `Animation`).
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation spawnAnimation;

	public ArcaneEmitterModel(ModelPart root) {
		super(root.getChild("arcane_missile_small_portal"));
		var main = this.root;
		this.portal_part_1 = main.getChild("portal_part_1");
		this.portal_part_2 = main.getChild("portal_part_2");
		this.portal_part_3 = main.getChild("portal_part_3");
		this.portal_part_4 = main.getChild("portal_part_4");
		this.portal_part_5 = main.getChild("portal_part_5");
		this.portal_part_6 = main.getChild("portal_part_6");
		this.idleAnimation = ArcaneEmitterAnimations.idle.bake(main);
		this.spawnAnimation = ArcaneEmitterAnimations.spawn.bake(main);
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition arcane_missile_small_portal = modelPartData.addOrReplaceChild("arcane_missile_small_portal", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition portal_part_1 = arcane_missile_small_portal.addOrReplaceChild("portal_part_1", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -14.0F, 0.0F, 28.0F, 28.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition portal_part_2 = arcane_missile_small_portal.addOrReplaceChild("portal_part_2", CubeListBuilder.create().texOffs(0, 28).addBox(-10.0F, -10.0F, 1.0F, 20.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.05F));

		PartDefinition portal_part_3 = arcane_missile_small_portal.addOrReplaceChild("portal_part_3", CubeListBuilder.create().texOffs(0, 28).addBox(-10.0F, -10.0F, -2.0F, 20.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.05F, 0.0F, 0.0F, 0.3927F));

		PartDefinition portal_part_4 = arcane_missile_small_portal.addOrReplaceChild("portal_part_4", CubeListBuilder.create().texOffs(0, 48).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.05F));

		PartDefinition portal_part_5 = arcane_missile_small_portal.addOrReplaceChild("portal_part_5", CubeListBuilder.create().texOffs(40, 28).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.1F, 0.0F, 0.0F, -0.3927F));

		PartDefinition portal_part_6 = arcane_missile_small_portal.addOrReplaceChild("portal_part_6", CubeListBuilder.create().texOffs(40, 28).addBox(-4.0F, -4.0F, 2.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.3927F));
		return LayerDefinition.create(modelData, 64, 64);
	}
	@Override
	public void setupAnim(SummonedEntityRenderState state) {
		super.setupAnim(state);
		this.spawnAnimation.apply(state.spawnAnimationState,   state.ageInTicks,  1F);
		this.idleAnimation.apply(state.idleAnimationState,     state.ageInTicks,  1F);
		this.spawnAnimation.apply(state.despawnAnimationState, state.ageInTicks, -1F);
	}
}
