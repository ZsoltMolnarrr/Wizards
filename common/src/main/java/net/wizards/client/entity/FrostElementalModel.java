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
import net.minecraft.entity.AnimationState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.wizards.WizardsMod;
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
	private final Animation idleAnimation;
	private final Animation attackAnimation;
	private final Animation attack2Animation;
	private final Animation shootChargeAnimation;
	private final Animation shootReleaseAnimation;
	private final Animation spellReleaseAnimation;
	private final Animation spawnAnimation;

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
		this.idleAnimation = FrostElementalAnimations.idle.createAnimation(this.root);
		this.attackAnimation = FrostElementalAnimations.attack.createAnimation(this.root);
		this.attack2Animation = FrostElementalAnimations.attack_2.createAnimation(this.root);
		this.shootChargeAnimation = FrostElementalAnimations.shoot_charge.createAnimation(this.root);
		this.shootReleaseAnimation = FrostElementalAnimations.shoot_release.createAnimation(this.root);
		this.spellReleaseAnimation = FrostElementalAnimations.spell_release.createAnimation(this.root);
		this.spawnAnimation = FrostElementalAnimations.spawn.createAnimation(this.root);
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 12.0F, 0.0F));

		ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(0, 35).cuboid(-5.0F, -10.0F, -4.0F, 10.0F, 10.0F, 6.0F, new Dilation(0.0F))
				.uv(96, 21).cuboid(-5.0F, -10.0F, -4.0F, 10.0F, 10.0F, 6.0F, new Dilation(0.3F)), ModelTransform.origin(0.0F, 2.0F, 2.0F));

		ModelPartData tail = body.addChild("tail", ModelPartBuilder.create().uv(0, 51).cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 9.0F, 4.0F, new Dilation(0.01F)), ModelTransform.origin(0.0F, 0.0F, -3.0F));

		ModelPartData tail_end = tail.addChild("tail_end", ModelPartBuilder.create().uv(20, 51).cuboid(-3.0F, -2.0F, 0.0F, 6.0F, 3.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 8.1585F, 1.7696F));

		ModelPartData chest = body.addChild("chest", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -9.0F, -2.0F));

		ModelPartData cube_r1 = chest.addChild("cube_r1", ModelPartBuilder.create().uv(28, 3).mirrored().cuboid(-8.2F, -8.2F, 6.4F, 4.0F, 4.0F, 7.0F, new Dilation(0.1F)).mirrored(false)
				.uv(28, 3).cuboid(4.2F, -8.2F, 6.4F, 4.0F, 4.0F, 7.0F, new Dilation(0.1F))
				.uv(74, 0).cuboid(-8.0F, -8.0F, -5.0F, 16.0F, 10.0F, 11.0F, new Dilation(0.3F))
				.uv(0, 14).cuboid(-8.0F, -8.0F, -5.0F, 16.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 1.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData cube_r2 = chest.addChild("cube_r2", ModelPartBuilder.create().uv(32, 37).mirrored().cuboid(-8.4F, -9.4924F, 2.2213F, 4.0F, 4.0F, 8.0F, new Dilation(0.11F)).mirrored(false)
				.uv(32, 37).cuboid(4.0F, -9.4924F, 2.2213F, 4.0F, 4.0F, 8.0F, new Dilation(0.11F)), ModelTransform.of(0.2F, -1.0F, 1.0F, 1.1781F, 0.0F, 0.0F));

		ModelPartData right_arm = chest.addChild("right_arm", ModelPartBuilder.create().uv(54, 10).mirrored().cuboid(-3.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
				.uv(80, 48).mirrored().cuboid(-3.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.3F)).mirrored(false)
				.uv(56, 0).mirrored().cuboid(-3.0F, -2.0F, 2.6F, 5.0F, 4.0F, 6.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.origin(-10.0F, -8.0F, -0.5F));

		ModelPartData right_forearm = right_arm.addChild("right_forearm", ModelPartBuilder.create().uv(54, 26).mirrored().cuboid(-5.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
				.uv(54, 45).mirrored().cuboid(-5.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.origin(0.0F, 10.0F, 0.0F));

		ModelPartData right_finger_2 = right_forearm.addChild("right_finger_2", ModelPartBuilder.create().uv(0, 14).mirrored().cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(-4.0F, 11.0F, 1.5F));

		ModelPartData right_finger_1 = right_forearm.addChild("right_finger_1", ModelPartBuilder.create().uv(0, 14).mirrored().cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(-4.0F, 11.0F, -2.5F));

		ModelPartData right_finger_3 = right_forearm.addChild("right_finger_3", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 11.0F, -0.5F));

		ModelPartData left_arm = chest.addChild("left_arm", ModelPartBuilder.create().uv(54, 10).cuboid(-2.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.0F))
				.uv(80, 48).cuboid(-2.0F, -2.0F, -3.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.3F))
				.uv(56, 0).cuboid(-2.0F, -2.0F, 2.6F, 5.0F, 4.0F, 6.0F, new Dilation(0.3F)), ModelTransform.origin(10.0F, -8.0F, -0.5F));

		ModelPartData left_forearm = left_arm.addChild("left_forearm", ModelPartBuilder.create().uv(54, 26).cuboid(-1.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new Dilation(0.0F))
				.uv(54, 45).cuboid(-1.0F, -1.0F, -4.0F, 6.0F, 12.0F, 7.0F, new Dilation(0.3F)), ModelTransform.origin(0.0F, 10.0F, 0.0F));

		ModelPartData left_finger_2 = left_forearm.addChild("left_finger_2", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, 0.0F, -1.25F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(4.0F, 11.0F, 1.25F));

		ModelPartData left_finger_1 = left_forearm.addChild("left_finger_1", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(4.0F, 11.0F, -2.5F));

		ModelPartData left_finger_3 = left_forearm.addChild("left_finger_3", ModelPartBuilder.create().uv(0, 14).mirrored().cuboid(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(0.0F, 11.0F, -0.5F));

		ModelPartData head = chest.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.5F, -3.0F, -5.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F))
				.uv(99, 44).cuboid(-4.5F, -3.0F, -6.0F, 9.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -6.0F, -5.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	// HAND-WRITTEN CODE

	// Basic render features

	public static final EntityModelLayer TEXTURE = new EntityModelLayer(Identifier.of(WizardsMod.ID, "frost_elemental"), "main");

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -60, 60);
		headPitch = MathHelper.clamp(headPitch, -60, 60);
		head.yaw = headYaw * 0.017453292F;
		head.pitch = headPitch * 0.017453292F;
	}

	// Animations

	private static final Vector3f TEMP = new Vector3f();

	// shoot_charge animation timing constants
	private static final long SHOOT_CHARGE_INTRO_MS = 2000L; // full animation length
	private static final long SHOOT_CHARGE_LOOP_MS  =  500L; // 25% = loop-back point
	private static final long SHOOT_CHARGE_BODY_MS  = 1500L; // loop body length (75%)

	@Override
	public void setAngles(SummonedEntityRenderState state) {
		super.setAngles(state);
		this.setHeadAngles(state.relativeHeadYaw, state.pitch);
		// Same time/amplitude mapping as animateMovement (limbSwing → time, limbSwingAmount → scale),
		// but routed through LoopingAnimationHelper so the walk clip's loop seam is interpolated with
		// wrapped catmull-rom neighbours instead of vanilla's clamped ones — removing the per-cycle hitch.
		long walkTime = (long) (state.limbSwingAnimationProgress * 50F);
		float walkAmount = Math.min(state.limbSwingAmplitude, 1F);
		ModelAnimations.seamlessLoop(this, FrostElementalAnimations.walk, walkTime, walkAmount, TEMP);
		this.spawnAnimation.apply(state.spawnAnimationState,   state.age,  1F);
		this.spawnAnimation.apply(state.despawnAnimationState, state.age, -1F);

		var anyAction = false;
		// Spell casting animation
		if (state.spellReleaseAnimationState.isRunning()) {
			this.spellReleaseAnimationFor(state.spellReleaseVariant)
					.apply(state.spellReleaseAnimationState, state.age, state.spellReleaseAnimationSpeed);
			anyAction = true;
		} else if (state.spellCastAnimationState.isRunning()) {
			this.animateShootCharge(state.spellCastAnimationState, state.age);
			anyAction = true;
		}
		// Attack animation
		if (state.attackAnimationState.isRunning()) {
			this.attackAnimationFor(state.attackVariant)
					.apply(state.attackAnimationState, state.age, state.attackAnimationSpeed);
			anyAction = true;
		}
		// Idle animation (only if not doing any other action)
		if (!anyAction) {
			this.idleAnimation.apply(state.idleAnimationState, state.age, 1F);
		}
	}

	// Maps a behaviour-defined attack variant number to one of this model's attack animations.
	// Unknown variants fall back to the variant-1 default.
	private Animation attackAnimationFor(int variant) {
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
	private Animation spellReleaseAnimationFor(int variant) {
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
		state.run(s -> {
			long rawMs = s.getTimeInMilliseconds(ageInTicks);
			long animMs = rawMs <= SHOOT_CHARGE_INTRO_MS
					? rawMs
					: SHOOT_CHARGE_LOOP_MS + (rawMs - SHOOT_CHARGE_LOOP_MS) % SHOOT_CHARGE_BODY_MS;
			this.shootChargeAnimation.apply(animMs, 1.0F);
		});
	}
}
