package net.wizards.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.spell_power.SpellPowerMod;
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

    // Loader framework specific pre-init
    private void preInit() {
        SpellPowerMod.registerAttributes();
        Group.WIZARDS = FabricItemGroupBuilder.build(
                new Identifier(WizardsMod.ID, "general"),
                () -> new ItemStack(Armors.wizardRobeSet.head));
    }
}