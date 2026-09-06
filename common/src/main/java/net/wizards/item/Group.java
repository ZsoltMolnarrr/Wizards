package net.wizards.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

public class Group {
    public static Identifier ID = new Identifier(WizardsMod.ID, "generic");
    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    public static ItemGroup WIZARDS;
}
