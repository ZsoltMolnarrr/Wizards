package net.wizards.item;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.spell_engine.rpg_series.item.Armor;

public class WizardArmor extends Armor.CustomItem {
    public WizardArmor(ArmorMaterial material, EquipmentType type, Settings settings) {
        super(material, type, settings);
    }
}
