package net.wizards.client.entity;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationDefinition;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

public class FireHydraModel extends EntityModel<SummonedEntityRenderState> {
	public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(WizardsMod.ID, "fire_hydra"), "main");

	private final ModelPart kneck_base;
	private final ModelPart kneck_part_1;
	private final ModelPart kneck_part_2;
	private final ModelPart kneck_part_3;
	private final ModelPart kneck_part_4;
	private final ModelPart head;
	private final ModelPart jaw;

	// Keyframe clips bound to this instance's parts (1.21.2+ `AnimationDefinition` → `Animation`).
	private final Animation idleAnimation;
	private final Animation attackAnimation;
	private final Animation fireballAnimation;
	private final Animation firebreathAnimation;
	private final Animation spawnAnimation;

	public FireHydraModel(ModelPart root) {
		super(root.getChild("root"));
		this.kneck_base = this.root.getChild("kneck_base");
		this.kneck_part_1 = this.kneck_base.getChild("kneck_part_1");
		this.kneck_part_2 = this.kneck_part_1.getChild("kneck_part_2");
		this.kneck_part_3 = this.kneck_part_2.getChild("kneck_part_3");
		this.kneck_part_4 = this.kneck_part_3.getChild("kneck_part_4");
		this.head = this.kneck_part_4.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.idleAnimation = FireHydraAnimations.idle.createAnimation(this.root);
		this.attackAnimation = FireHydraAnimations.attack.createAnimation(this.root);
		this.fireballAnimation = FireHydraAnimations.fireball.createAnimation(this.root);
		this.firebreathAnimation = FireHydraAnimations.firebreath.createAnimation(this.root);
		this.spawnAnimation = FireHydraAnimations.spawn.createAnimation(this.root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, 2.0F));

		ModelPartData kneck_base = root.addChild("kneck_base", ModelPartBuilder.create().uv(0, 46).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 12.0F, new Dilation(0.01F))
		.uv(20, 15).mirrored().cuboid(-1.0F, -5.0F, -6.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.origin(0.0F, -2.0F, -2.0F));

		ModelPartData kneck_part_1 = kneck_base.addChild("kneck_part_1", ModelPartBuilder.create().uv(36, 49).cuboid(-3.0F, -10.0F, -2.5F, 6.0F, 10.0F, 5.0F, new Dilation(0.0F))
		.uv(30, 13).cuboid(-1.0F, -7.0F, 2.5F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, -9.5F));

		ModelPartData kneck_part_2 = kneck_part_1.addChild("kneck_part_2", ModelPartBuilder.create().uv(0, 33).cuboid(-2.5F, -2.0229F, -0.0228F, 5.0F, 4.0F, 9.0F, new Dilation(0.0F))
		.uv(20, 9).mirrored().cuboid(-1.0F, 1.9771F, 2.4772F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(0.0F, -9.9771F, -0.4772F));

		ModelPartData kneck_part_3 = kneck_part_2.addChild("kneck_part_3", ModelPartBuilder.create().uv(28, 34).cuboid(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.01F))
		.uv(30, 13).cuboid(-1.0F, -6.0F, 2.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -0.0229F, 8.9772F));

		ModelPartData kneck_part_4 = kneck_part_3.addChild("kneck_part_4", ModelPartBuilder.create().uv(28, 34).cuboid(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F))
		.uv(30, 13).cuboid(-1.0F, -6.0F, 2.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -8.0F, 0.0F));

		ModelPartData head = kneck_part_4.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -2.0F, -4.0F, 6.0F, 5.0F, 6.0F, new Dilation(0.0F))
		.uv(24, 0).mirrored().cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
		.uv(0, 11).cuboid(-2.0F, -1.0F, -10.0F, 4.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -8.0F, -1.0F));

		ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(49, 0).cuboid(-1.0F, -5.0F, 4.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
		.uv(38, 0).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -2.0F, 1.0F, 0.0F, 0.0F, 0.3927F));

		ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(49, 0).cuboid(-1.0F, -5.0F, 5.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
		.uv(38, 0).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 3.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create().uv(0, 20).cuboid(-1.5F, -1.0F, -8.0F, 3.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 2.0F, -1.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(SummonedEntityRenderState state) {
		super.setAngles(state);
		this.spawnAnimation.apply(state.spawnAnimationState,   state.age,  1F);
		this.spawnAnimation.apply(state.despawnAnimationState, state.age, -1F);

		var anyAction = false;
		if (state.spellReleaseAnimationState.isRunning()) {
			this.spellReleaseAnimationFor(state.spellReleaseVariant)
					.apply(state.spellReleaseAnimationState, state.age, state.spellReleaseAnimationSpeed);
			anyAction = true;
		}
		if (state.attackAnimationState.isRunning()) {
			this.attackAnimation.apply(state.attackAnimationState, state.age, state.attackAnimationSpeed);
			anyAction = true;
		}
		if (!anyAction) {
			this.idleAnimation.apply(state.idleAnimationState, state.age, 1F);
		}
	}

	private Animation spellReleaseAnimationFor(int variant) {
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
