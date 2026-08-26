package net.wizards.client.entity;

import net.minecraft.client.animation.AnimationDefinition;
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

public class FireHydraModel extends EntityModel<SummonedEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(WizardsMod.ID, "fire_hydra"), "main");

	private final ModelPart kneck_base;
	private final ModelPart kneck_part_1;
	private final ModelPart kneck_part_2;
	private final ModelPart kneck_part_3;
	private final ModelPart kneck_part_4;
	private final ModelPart head;
	private final ModelPart jaw;

	// Keyframe clips bound to this instance's parts (1.21.2+ `AnimationDefinition` → `Animation`).
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation attackAnimation;
	private final KeyframeAnimation fireballAnimation;
	private final KeyframeAnimation firebreathAnimation;
	private final KeyframeAnimation spawnAnimation;

	public FireHydraModel(ModelPart root) {
		super(root.getChild("root"));
		this.kneck_base = this.root.getChild("kneck_base");
		this.kneck_part_1 = this.kneck_base.getChild("kneck_part_1");
		this.kneck_part_2 = this.kneck_part_1.getChild("kneck_part_2");
		this.kneck_part_3 = this.kneck_part_2.getChild("kneck_part_3");
		this.kneck_part_4 = this.kneck_part_3.getChild("kneck_part_4");
		this.head = this.kneck_part_4.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.idleAnimation = FireHydraAnimations.idle.bake(this.root);
		this.attackAnimation = FireHydraAnimations.attack.bake(this.root);
		this.fireballAnimation = FireHydraAnimations.fireball.bake(this.root);
		this.firebreathAnimation = FireHydraAnimations.firebreath.bake(this.root);
		this.spawnAnimation = FireHydraAnimations.spawn.bake(this.root);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition root = modelPartData.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 2.0F));

		PartDefinition kneck_base = root.addOrReplaceChild("kneck_base", CubeListBuilder.create().texOffs(0, 46).addBox(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 12.0F, new CubeDeformation(0.01F))
		.texOffs(20, 15).mirror().addBox(-1.0F, -5.0F, -6.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(0.0F, -2.0F, -2.0F));

		PartDefinition kneck_part_1 = kneck_base.addOrReplaceChild("kneck_part_1", CubeListBuilder.create().texOffs(36, 49).addBox(-3.0F, -10.0F, -2.5F, 6.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(30, 13).addBox(-1.0F, -7.0F, 2.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -9.5F));

		PartDefinition kneck_part_2 = kneck_part_1.addOrReplaceChild("kneck_part_2", CubeListBuilder.create().texOffs(0, 33).addBox(-2.5F, -2.0229F, -0.0228F, 5.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(20, 9).mirror().addBox(-1.0F, 1.9771F, 2.4772F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -9.9771F, -0.4772F));

		PartDefinition kneck_part_3 = kneck_part_2.addOrReplaceChild("kneck_part_3", CubeListBuilder.create().texOffs(28, 34).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.01F))
		.texOffs(30, 13).addBox(-1.0F, -6.0F, 2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0229F, 8.9772F));

		PartDefinition kneck_part_4 = kneck_part_3.addOrReplaceChild("kneck_part_4", CubeListBuilder.create().texOffs(28, 34).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(30, 13).addBox(-1.0F, -6.0F, 2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition head = kneck_part_4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(24, 0).mirror().addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 11).addBox(-2.0F, -1.0F, -10.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -1.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(49, 0).addBox(-1.0F, -5.0F, 4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(38, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.0F, 1.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(49, 0).addBox(-1.0F, -5.0F, 5.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(38, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 20).addBox(-1.5F, -1.0F, -8.0F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -1.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void setupAnim(SummonedEntityRenderState state) {
		super.setupAnim(state);
		this.spawnAnimation.apply(state.spawnAnimationState,   state.ageInTicks,  1F);
		this.spawnAnimation.apply(state.despawnAnimationState, state.ageInTicks, -1F);

		var anyAction = false;
		if (state.spellReleaseAnimationState.isStarted()) {
			this.spellReleaseAnimationFor(state.spellReleaseVariant)
					.apply(state.spellReleaseAnimationState, state.ageInTicks, state.spellReleaseAnimationSpeed);
			anyAction = true;
		}
		if (state.attackAnimationState.isStarted()) {
			this.attackAnimation.apply(state.attackAnimationState, state.ageInTicks, state.attackAnimationSpeed);
			anyAction = true;
		}
		if (!anyAction) {
			this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks, 1F);
		}
	}

	private KeyframeAnimation spellReleaseAnimationFor(int variant) {
		return switch (variant) {
			case 2  -> firebreathAnimation;
			default -> fireballAnimation;
		};
	}

	/// Clip lengths (ticks) — the renderer turns the summon's configured durations into
	/// playback-speed multipliers, which needs the length of the clip picked for the variant.
	public static float attackAnimationLengthTicks() {
		return FireHydraAnimations.attack.lengthInSeconds() * 20F;
	}

	public static float spellReleaseAnimationLengthTicks(int variant) {
		return definitionForSpellRelease(variant).lengthInSeconds() * 20F;
	}

	private static AnimationDefinition definitionForSpellRelease(int variant) {
		return switch (variant) {
			case 2  -> FireHydraAnimations.firebreath;
			default -> FireHydraAnimations.fireball;
		};
	}
}
