package net.wizards;

import net.tinyconfig.ConfigManager;
import net.wizards.config.Default;
import net.wizards.config.ItemConfig;
import net.wizards.config.LootConfig;
import net.wizards.effect.Effects;
import net.wizards.item.Armors;
import net.wizards.item.Weapons;

public class WizardsMod {
    public static final String ID = "wizards";

    public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<ItemConfig>
            ("items", Default.itemConfig)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    public static ConfigManager<LootConfig> lootConfig = new ConfigManager<LootConfig>
            ("loot", Default.lootConfig)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    public static void init() {
        lootConfig.refresh();
        itemConfig.refresh();
        Weapons.register(itemConfig.value.weapons);
        Armors.register(itemConfig.value.armor_sets);
        itemConfig.save();
        Effects.register();
    }
}