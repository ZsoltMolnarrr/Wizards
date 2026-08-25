package net.wizards.item;

import net.minecraft.item.Item;
import net.spell_engine.rpg_series.item.Armor;

import java.util.HashMap;

public class WizardItems {
    public static final HashMap<String, Item> entries;
    static {
        entries = new HashMap<>();
        for(var weaponEntry: WizardWeapons.entries) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for(var entry: WizardArmors.entries) {
            var set = entry.armorSet();
            for (var piece: set.pieces()) {
                var armorItem = (Armor.CustomItem) piece;
                entries.put(set.idOf(armorItem).toString(), armorItem);
            }
        }
    }
}
