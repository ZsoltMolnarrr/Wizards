package net.wizards.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.spell_power.api.enchantment.MagicalArmor;
import net.wizards.WizardsMod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class WizardArmor extends ArmorItem implements IAnimatable, MagicalArmor {
    public static final Identifier equipSoundId = new Identifier(WizardsMod.ID, "wizard_robes_equip");
    public static final SoundEvent equipSound = new SoundEvent(equipSoundId);

    public WizardArmor(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    private Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    public void setAttributes(Multimap<EntityAttribute, EntityAttributeModifier> attributes) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(this.slot));
        builder.putAll(attributes);
        this.attributes = builder.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (attributes == null) {
            return super.getAttributeModifiers(slot);
        }
        return slot == this.slot ? this.attributes : super.getAttributeModifiers(slot);
    }

    // MARK: IAnimatable
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.STOP;
    }
    @Override
    public void registerControllers(AnimationData data) {
        // data.addAnimationController(new AnimationController(this, "controller", 20, this::predicate));
    }
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
