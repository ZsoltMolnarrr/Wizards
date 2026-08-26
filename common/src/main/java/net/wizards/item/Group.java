package net.wizards.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.wizards.WizardsMod;

public class Group {
    public static Identifier ID = Identifier.fromNamespaceAndPath(WizardsMod.ID, "generic");
    public static ResourceKey<CreativeModeTab> KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ID);
    public static CreativeModeTab WIZARDS;
}
