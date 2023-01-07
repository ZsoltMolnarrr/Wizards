package net.wizards.config;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Map;

public class ItemConfig {
    public static class Attribute {
        public String id;
        public float value;
        public EntityAttributeModifier.Operation operation;

        public Attribute() {}
        public Attribute(String id, float value, EntityAttributeModifier.Operation operation) {
            this.id = id;
            this.value = value;
            this.operation = operation;
        }
        public static Attribute bonus(EntityAttribute entityAttribute, float value) {
            return new Attribute(
                    Registry.ATTRIBUTE.getId(entityAttribute).toString(),
                    value,
                    EntityAttributeModifier.Operation.ADDITION
            );
        }
    }

    public static class Weapon {
        public float attack_damage = 0;
        public float attack_speed = 0;
        public ArrayList<Attribute> attributes = new ArrayList<>();

        public Weapon() { }
        public Weapon(float attack_damage, float attack_speed) {
            this.attack_damage = attack_damage;
            this.attack_speed = attack_speed;
        }
        public Weapon add(Attribute attribute) {
            attributes.add(attribute);
            return this;
        }
    }

    public static class ArmorSet {

    }

    public Map<String, Weapon> weapons;
}