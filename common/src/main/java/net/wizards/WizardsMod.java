package net.wizards;

import net.wizards.armor.Armors;

public class WizardsMod {
    public static final String ID = "wizards";

    public static void init() {
        Armors.register();
    }
}