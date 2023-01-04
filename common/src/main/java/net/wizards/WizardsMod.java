package net.wizards;

import net.minecraft.util.registry.Registry;
import net.wizards.item.RuneItems;

public class WizardsMod {
    public static final String ID = "wizards";

    public static void init() {
        for(var entry: RuneItems.all.entrySet()) {
            Registry.register(Registry.ITEM, entry.getKey(), entry.getValue());
        }
    }
}