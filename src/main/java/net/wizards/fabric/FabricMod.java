package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.wizards.WizardsMod;
import net.wizards.item.Armors;
import net.wizards.item.Group;
import net.wizards.util.SoundHelper;

public class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        preInit();
        WizardsMod.init();
        SoundHelper.registerSounds();
    }

    private void preInit() {
        Group.WIZARDS = FabricItemGroup.builder()
                 .icon(() -> new ItemStack(Armors.wizardRobeSet.head))
                .displayName(Text.translatable("itemGroup.wizards.general"))
                .build();
    }
}