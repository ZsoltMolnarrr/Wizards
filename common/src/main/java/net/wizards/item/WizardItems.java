package net.wizards.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

import java.util.HashMap;

public class WizardItems {
    public static final HashMap<String, Item> entries;
    static {
        entries = new HashMap<>();
        for(var weaponEntry: Weapons.entries) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for(var entry: Armors.entries) {
            var set = entry.armorSet();
            for (var piece: set.pieces()) {
                var armorItem = (ArmorItem) piece;
                entries.put(set.idOf(armorItem).toString(), armorItem);
            }
        }
    }
}
