package net.wizards;

import net.tinyconfig.ConfigManager;
import net.wizards.item.Armors;
import net.wizards.config.ItemConfig;
import net.wizards.item.Weapons;

public class WizardsMod {
    public static final String ID = "wizards";

    public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<ItemConfig>
            ("items", new ItemConfig())
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();


    public static void init() {
        Armors.register();
        Weapons.register();
    }
}