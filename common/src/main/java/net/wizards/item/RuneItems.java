package net.wizards.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

import java.util.HashMap;
import java.util.Map;

public class RuneItems {

    private enum MagicSchool {
        ARCANE, FIRE, FROST, HEALING, LIGHTNING, SOUL;
    }

    public static Map<Identifier, Item> all;
    static {
        all = new HashMap<>();
        for(var school : MagicSchool.values()) {
            var id = new Identifier(WizardsMod.ID, school.toString().toLowerCase() + "_stone");
            var item = new Item(new FabricItemSettings().group(ItemGroup.COMBAT));
            all.put(id, item);
        }
    }
}
