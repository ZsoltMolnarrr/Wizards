package net.wizards.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.wizards.WizardsMod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class WizardArmor extends ArmorItem implements IAnimatable, ConfigurableAttributes {
    public static final Identifier equipSoundId = new Identifier(WizardsMod.ID, "wizard_robes_equip");
    public static final SoundEvent equipSound = new SoundEvent(equipSoundId);
    public final Armors.Material customMaterial;

    public WizardArmor(Armors.Material material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
        this.customMaterial = material;
    }

    private Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    public void setAttributes(Multimap<EntityAttribute, EntityAttributeModifier> attributes) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        // builder.putAll(super.getAttributeModifiers(this.slot));
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
