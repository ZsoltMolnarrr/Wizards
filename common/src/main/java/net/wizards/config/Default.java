package net.wizards.config;

import net.wizards.item.Armors;
import net.wizards.item.Weapons;

public class Default {
    public final static ItemConfig itemConfig;
    static {
        itemConfig = new ItemConfig();
        for (var weapon: Weapons.entries) {
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }
        for (var armorSet: Armors.entries) {
            itemConfig.armor_sets.put(armorSet.name(), armorSet.defaults());
        }
    }
}
