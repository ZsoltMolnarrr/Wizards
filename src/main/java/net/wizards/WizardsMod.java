package net.wizards;

import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.spell_engine.api.config.ConfigFile;
import net.tinyconfig.ConfigManager;
import net.wizards.config.Default;
import net.wizards.config.TweaksConfig;
import net.wizards.effect.Effects;
import net.wizards.item.Armors;
import net.wizards.item.Group;
import net.wizards.item.Weapons;
import net.wizards.item.WizardBooks;
import net.wizards.util.WizardsSounds;
import net.wizards.villager.WizardVillagers;

public class WizardsMod implements ModInitializer {
    public static final String ID = "wizards";

    public static ConfigManager<ConfigFile.Equipment> equipmentConfig = new ConfigManager<>
            ("equipment", Default.itemConfig)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();
    public static ConfigManager<StructurePoolConfig> villageConfig = new ConfigManager<>
            ("villages", Default.villageConfig)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();
    public static ConfigManager<TweaksConfig> tweaksConfig = new ConfigManager<>
            ("tweaks", new TweaksConfig())
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    @Override
    public void onInitialize() {
        equipmentConfig.refresh();
        tweaksConfig.refresh();
        villageConfig.refresh();
        WizardsSounds.register();
        Group.WIZARDS = FabricItemGroup.builder()
                .icon(() -> new ItemStack(Armors.wizardRobeSet.head))
                .displayName(Text.translatable("itemGroup.wizards.general"))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.WIZARDS);
        WizardBooks.register();
        Weapons.register(equipmentConfig.value.weapons);
        Armors.register(equipmentConfig.value.armor_sets);
        equipmentConfig.save();
        Effects.register();
        WizardVillagers.register();
    }
}