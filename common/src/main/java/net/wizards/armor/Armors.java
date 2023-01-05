package net.wizards.armor;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.wizards.WizardsMod;

public class Armors {

    public static Material CLOTH = new Material();

    public static class WizardRobes {
        public static final String prefix = "wizard_robe_";
        public static final WizardArmor head = new WizardArmor(CLOTH, EquipmentSlot.HEAD, new FabricItemSettings().group(ItemGroup.COMBAT));
        public static final WizardArmor chest = new WizardArmor(CLOTH, EquipmentSlot.CHEST, new FabricItemSettings().group(ItemGroup.COMBAT));
        public static final WizardArmor legs = new WizardArmor(CLOTH, EquipmentSlot.LEGS, new FabricItemSettings().group(ItemGroup.COMBAT));
        public static final WizardArmor feet = new WizardArmor(CLOTH, EquipmentSlot.FEET, new FabricItemSettings().group(ItemGroup.COMBAT));
    }

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(WizardsMod.ID, WizardRobes.prefix + "head"), WizardRobes.head);
        Registry.register(Registry.ITEM, new Identifier(WizardsMod.ID, WizardRobes.prefix + "chest"), WizardRobes.chest);
        Registry.register(Registry.ITEM, new Identifier(WizardsMod.ID, WizardRobes.prefix + "legs"), WizardRobes.legs);
        Registry.register(Registry.ITEM, new Identifier(WizardsMod.ID, WizardRobes.prefix + "feet"), WizardRobes.feet);
    }
}
