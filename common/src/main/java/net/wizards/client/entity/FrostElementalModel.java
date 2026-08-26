package net.wizards.client.entity;

import net.wizards.WizardsMod;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.spell_engine.entity.ModelAnimations;
import org.joml.Vector3f;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class FrostElementalModel extends EntityModel<SummonedEntityRenderState> {
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

	// Keyframe clips are bound to this instance's parts once, in the constructor (1.21.2+:
	// `AnimationDefinition` is the data, `Animation` the bound player).
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation attackAnimation;
	private final KeyframeAnimation attack2Animation;
	private final KeyframeAnimation shootChargeAnimation;
	private final KeyframeAnimation shootReleaseAnimation;
	private final KeyframeAnimation spellReleaseAnimation;
	private final KeyframeAnimation spawnAnimation;

	public FrostElementalModel(ModelPart root) {
		super(root.getChild("root"));
		this.body = this.root.getChild("body");
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
		this.idleAnimation = FrostElementalAnimations.idle.bake(this.root);
		this.attackAnimation = FrostElementalAnimations.attack.bake(this.root);
		this.attack2Animation = FrostElementalAnimations.attack_2.bake(this.root);
		this.shootChargeAnimation = FrostElementalAnimations.shoot_charge.bake(this.root);
		this.shootReleaseAnimation = FrostElementalAnimations.shoot_release.bake(this.root);
		this.spellReleaseAnimation = FrostElementalAnimations.spell_release.bake(this.root);
		this.spawnAnimation = FrostElementalAnimations.spawn.bake(this.root);
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition root = modelPartData.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 35).addBox(-5.0F, -10.0F, -4.0F, 10.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(96, 21).addBox(-5.0F, -10.0F, -4.0F, 10.0F, 10.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 2.0F, 2.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 51).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 9.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, -3.0F));

		PartDefinition tail_end = tail.addOrReplaceChild("tail_end", CubeListBuilder.create().texOffs(20, 51).addBox(-3.0F, -2.0F, 0.0F, 6.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.1585F, 1.7696F));

		PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, -2.0F));

		PartDefinition cube_r1 = chest.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 3).mirror().addBox(-8.2F, -8.2F, 6.4F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.1F)).mirror(false)
				.texOffs(28, 3).addBox(4.2F, -8.2F, 6.4F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.1F))
				.texOffs(74, 0).addBox(-8.0F, -8.0F, -5.0F, 16.0F, 10.0F, 11.0F, new CubeDeformation(0.3F))
				.texOffs(0, 14).addBox(-8.0F, -8.0F, -5.0F, 16.0F, 10.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r2 = chest.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 37).mirror().addBox(-8.4F, -9.4924F, 2.2213F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.11F)).mirror(false)
				.texOffs(32, 37).addBox(4.0F, -9.4924F, 2.2213F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.11F)), PartPose.offsetAndRotation(0.2F, -1.0F, 1.0F, 1.1781F, 0.0F, 0.0F));

		PartDefinition right_arm = chest.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(54, 10).mirror().addBox(-3.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(80, 48).mirror().addBox(-3.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.3F)).mirror(false)
				.texOffs(56, 0).mirror().addBox(-3.0F, -2.0F, 2.6F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(-10.0F, -8.0F, -0.5F));

		PartDefinition right_forearm = right_arm.addOrReplaceChild("right_forearm", CubeListBuilder.create().texOffs(54, 26).mirror().addBox(-5.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(54, 45).mirror().addBox(-5.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition right_finger_2 = right_forearm.addOrReplaceChild("right_finger_2", CubeListBuilder.create().texOffs(0, 14).mirror().addBox(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.0F, 11.0F, 1.5F));

		PartDefinition right_finger_1 = right_forearm.addOrReplaceChild("right_finger_1", CubeListBuilder.create().texOffs(0, 14).mirror().addBox(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.0F, 11.0F, -2.5F));

		PartDefinition right_finger_3 = right_forearm.addOrReplaceChild("right_finger_3", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, -0.5F));

		PartDefinition left_arm = chest.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(54, 10).addBox(-2.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(80, 48).addBox(-2.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.3F))
				.texOffs(56, 0).addBox(-2.0F, -2.0F, 2.6F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offset(10.0F, -8.0F, -0.5F));

		PartDefinition left_forearm = left_arm.addOrReplaceChild("left_forearm", CubeListBuilder.create().texOffs(54, 26).addBox(-1.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(54, 45).addBox(-1.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition left_finger_2 = left_forearm.addOrReplaceChild("left_finger_2", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, 0.0F, -1.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 11.0F, 1.25F));

		PartDefinition left_finger_1 = left_forearm.addOrReplaceChild("left_finger_1", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 11.0F, -2.5F));

		PartDefinition left_finger_3 = left_forearm.addOrReplaceChild("left_finger_3", CubeListBuilder.create().texOffs(0, 14).mirror().addBox(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 11.0F, -0.5F));

		PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -5.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(99, 44).addBox(-4.5F, -3.0F, -6.0F, 9.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -5.0F));
		return LayerDefinition.create(modelData, 128, 128);
	}

	// HAND-WRITTEN CODE

	// Basic render features

	public static final ModelLayerLocation TEXTURE = new ModelLayerLocation(Identifier.fromNamespaceAndPath(WizardsMod.ID, "frost_elemental"), "main");

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = Mth.clamp(headYaw, -60, 60);
		headPitch = Mth.clamp(headPitch, -60, 60);
		head.yRot = headYaw * 0.017453292F;
		head.xRot = headPitch * 0.017453292F;
	}

	// Animations

	private static final Vector3f TEMP = new Vector3f();

	// shoot_charge animation timing constants
	private static final long SHOOT_CHARGE_INTRO_MS = 2000L; // full animation length
	private static final long SHOOT_CHARGE_LOOP_MS  =  500L; // 25% = loop-back point
	private static final long SHOOT_CHARGE_BODY_MS  = 1500L; // loop body length (75%)

	@Override
	public void setupAnim(SummonedEntityRenderState state) {
		super.setupAnim(state);
		this.setHeadAngles(state.yRot, state.xRot);
		// Same time/amplitude mapping as animateMovement (limbSwing → time, limbSwingAmount → scale),
		// but routed through LoopingAnimationHelper so the walk clip's loop seam is interpolated with
		// wrapped catmull-rom neighbours instead of vanilla's clamped ones — removing the per-cycle hitch.
		long walkTime = (long) (state.walkAnimationPos * 50F);
		float walkAmount = Math.min(state.walkAnimationSpeed, 1F);
		ModelAnimations.seamlessLoop(this, FrostElementalAnimations.walk, walkTime, walkAmount, TEMP);
		this.spawnAnimation.apply(state.spawnAnimationState,   state.ageInTicks,  1F);
		this.spawnAnimation.apply(state.despawnAnimationState, state.ageInTicks, -1F);

		var anyAction = false;
		// Spell casting animation
		if (state.spellReleaseAnimationState.isStarted()) {
			this.spellReleaseAnimationFor(state.spellReleaseVariant)
					.apply(state.spellReleaseAnimationState, state.ageInTicks, state.spellReleaseAnimationSpeed);
			anyAction = true;
		} else if (state.spellCastAnimationState.isStarted()) {
			this.animateShootCharge(state.spellCastAnimationState, state.ageInTicks);
			anyAction = true;
		}
		// Attack animation
		if (state.attackAnimationState.isStarted()) {
			this.attackAnimationFor(state.attackVariant)
					.apply(state.attackAnimationState, state.ageInTicks, state.attackAnimationSpeed);
			anyAction = true;
		}
		// Idle animation (only if not doing any other action)
		if (!anyAction) {
			this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks, 1F);
		}
	}

	// Maps a behaviour-defined attack variant number to one of this model's attack animations.
	// Unknown variants fall back to the variant-1 default.
	private KeyframeAnimation attackAnimationFor(int variant) {
		return switch (variant) {
			case 2  -> attack2Animation;
			default -> attackAnimation;
		};
	}

	/// Clip length (ticks) of the attack animation for a variant — the renderer needs it to turn
	/// the summon's configured swing duration into a playback-speed multiplier.
	public static float attackAnimationLengthTicks(int variant) {
		return definitionForAttack(variant).lengthInSeconds() * 20F;
	}

	private static AnimationDefinition definitionForAttack(int variant) {
		return switch (variant) {
			case 2  -> FrostElementalAnimations.attack_2;
			default -> FrostElementalAnimations.attack;
		};
	}

	// Maps a behaviour-defined spell-release variant to one of this model's release animations.
	// Unknown variants fall back to the variant-1 default.
	private KeyframeAnimation spellReleaseAnimationFor(int variant) {
		return switch (variant) {
			case 2  -> spellReleaseAnimation;
			default -> shootReleaseAnimation;
		};
	}

	public static float spellReleaseAnimationLengthTicks(int variant) {
		return definitionForSpellRelease(variant).lengthInSeconds() * 20F;
	}

	private static AnimationDefinition definitionForSpellRelease(int variant) {
		return switch (variant) {
			case 2  -> FrostElementalAnimations.spell_release;
			default -> FrostElementalAnimations.shoot_release;
		};
	}

	// Plays shoot_charge with a full intro (0–2 s) then loops the 25%–100% portion indefinitely.
	private void animateShootCharge(AnimationState state, float ageInTicks) {
		state.ifStarted(s -> {
			long rawMs = s.getTimeInMillis(ageInTicks);
			long animMs = rawMs <= SHOOT_CHARGE_INTRO_MS
					? rawMs
					: SHOOT_CHARGE_LOOP_MS + (rawMs - SHOOT_CHARGE_LOOP_MS) % SHOOT_CHARGE_BODY_MS;
			this.shootChargeAnimation.apply(animMs, 1.0F);
		});
	}
}
