package net.wizards;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.rpg_foundation.structure_pool.api.StructurePoolConfig;
import net.spell_engine.Platform;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.tiny_config.ConfigManager;
import net.wizards.config.Default;
import net.wizards.config.TweaksConfig;
import net.wizards.effect.WizardsEffects;
import net.wizards.entity.WizardEntities;

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
        if (Platform.util().isDevelopmentEnvironment()) {
            tweaksConfig.value.ignore_items_required_mods = true;
        }
    }

    public static void registerEntities() {
        // Each entity's base attributes are registered alongside its type build inside register(),
        // sourced from Wizards' own summoned-entity config (WizardEntities.summonConfig).
        WizardEntities.register();
    }

    public static void registerSounds() {
        WizardsSounds.register();
    }

    public static void registerItems() {
        Group.WIZARDS = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
                .icon(() -> new ItemStack(WizardArmors.wizardRobeSet.head))
                .title(Component.translatable("itemGroup.wizards.general"))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Group.KEY, Group.WIZARDS);
        WizardBooks.register();
        WizardWeapons.register(equipmentConfig.value.weapons);
        WizardArmors.register(equipmentConfig.value.armor_sets);
        equipmentConfig.save();
    }

    public static void registerEffects() {
        WizardsEffects.register(effectsConfig.value);
        effectsConfig.save();
    }

    public static void registerVillagers() {
        WizardVillagers.register();
    }
}