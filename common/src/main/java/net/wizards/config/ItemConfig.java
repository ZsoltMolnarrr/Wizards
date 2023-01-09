package net.wizards.config;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.spell_power.internals.AttributeFamily;

import java.util.ArrayList;
import java.util.Map;

public class ItemConfig {
    public static class SpellAttribute {
        public String name;
        public float value;
        public EntityAttributeModifier.Operation operation;

        public SpellAttribute() {}
        public SpellAttribute(String name, float value, EntityAttributeModifier.Operation operation) {
            this.name = name;
            this.value = value;
            this.operation = operation;
        }
        public static SpellAttribute bonus(AttributeFamily attribute, float value) {
            return new SpellAttribute(
                    attribute.name,
                    value,
                    EntityAttributeModifier.Operation.ADDITION
            );
        }
    }

    public static class Weapon {
        public float attack_damage = 0;
        public float attack_speed = 0;
        public ArrayList<SpellAttribute> spell_attributes = new ArrayList<>();

        public Weapon() { }
        public Weapon(float attack_damage, float attack_speed) {
            this.attack_damage = attack_damage;
            this.attack_speed = attack_speed;
        }
        public Weapon add(SpellAttribute attribute) {
            spell_attributes.add(attribute);
            return this;
        }
    }

    public static class ArmorSet {

    }

    public Map<String, Weapon> weapons;
}