package net.wizards;

import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.spell_engine.api.config.ConfigFile;
import net.tiny_config.ConfigManager;
import net.wizards.config.Default;
import net.wizards.config.TweaksConfig;
import net.wizards.effect.WizardsEffects;
import net.wizards.item.WizardArmors;
import net.wizards.item.Group;
import net.wizards.item.WizardWeapons;
import net.wizards.item.WizardBooks;
import net.wizards.content.WizardsSounds;
import net.wizards.villager.WizardVillagers;

public class WizardsMod {
    public static final String ID = "wizards";

    public static ConfigManager<ConfigFile.Equipment> equipmentConfig = new ConfigManager<>
            ("equipment_v2", Default.itemConfig)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();
    public static ConfigManager<ConfigFile.Effects> effectsConfig = new ConfigManager<>
            ("effects", new ConfigFile.Effects())
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

    public static void init() {
        equipmentConfig.refresh();
        effectsConfig.refresh();
        villageConfig.refresh();
        tweaksConfig.refresh();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            tweaksConfig.value.ignore_items_required_mods = true;
        }
    }

    public static void registerSounds() {
        WizardsSounds.register();
    }

    public static void registerItems() {
        Group.WIZARDS = FabricItemGroup.builder()
                .icon(() -> new ItemStack(WizardArmors.wizardRobeSet.head))
                .displayName(Text.translatable("itemGroup.wizards.general"))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.WIZARDS);
        WizardBooks.register();
        WizardWeapons.register(equipmentConfig.value.weapons);
        WizardArmors.register(equipmentConfig.value.armor_sets);
        equipmentConfig.save();
    }

    public static void registerEffects() {
        WizardsEffects.register(effectsConfig.value);
        effectsConfig.save();
    }

    public static void registerPOI() {
        WizardVillagers.registerPOI();
    }

    public static void registerVillagers() {
        WizardVillagers.register();
    }
}