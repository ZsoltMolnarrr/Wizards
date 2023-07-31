package net.wizards.item;

import net.minecraft.util.Identifier;
import net.spell_engine.api.item.trinket.SpellBooks;
import net.wizards.WizardsMod;

import java.util.List;

public class WizardBooks {
    public static void register() {
        var books = List.of("arcane", "fire", "frost");
        for (var name: books) {
            SpellBooks.createAndRegister(new Identifier(WizardsMod.ID, name), Group.KEY);
        }
    }
}
