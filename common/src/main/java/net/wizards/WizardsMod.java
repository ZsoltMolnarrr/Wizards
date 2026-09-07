package net.wizards;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.minecraft.text.Text;
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
        tweaksConfig.refresh();
        villageConfig.refresh();
        if (!Platform.util().isModLoaded("lithostitched")) {
            // Only inject the towers if Lithostitched is not present - otherwise the data-driven
            // paths in `resources/data/wizards` already do it.
            //
            // `injectAll` only *queues* the entries; StructurePoolAPI's own entrypoint applies them
            // when the server starts (Fabric SERVER_STARTING / Forge ServerAboutToStartEvent, both
            // before the spawn region generates). The queue is deliberately never cleared, so this
            // must be called exactly once, here at mod init - never per world load.
            StructurePoolAPI.injectAll(villageConfig.value);
        }
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
        Group.WIZARDS = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
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

    public static void registerVillagers() {
        WizardVillagers.register();
    }
}