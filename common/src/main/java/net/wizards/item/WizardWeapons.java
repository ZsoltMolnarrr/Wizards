package net.wizards.item;

import net.spell_engine.Platform;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spell_engine.rpg_series.config.WeaponConfig;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.rpg_series.item.Weapons;
import net.spell_power.api.SpellSchools;
import net.wizards.WizardsMod;
import net.wizards.content.WizardSpells;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WizardWeapons {
    private static final String NAMESPACE = WizardsMod.ID;
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();
    private static Weapon.Entry add(Weapon.Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = new Identifier(idString);
        if (requirement) {
            return () -> {
                return Ingredient.ofItems(fallback);
            };
        } else {
            return () -> {
                var item = Registries.ITEM.get(id);
                var ingredient = item != null ? item : fallback;
                return Ingredient.ofItems(ingredient);
            };
        }
    }

    private static final String AETHER = "aether";
    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";

    // MARK: Wands

    public static final Weapon.Entry noviceWand = add(Weapons.damageWand(
            NAMESPACE, "wand_novice",
                    Equipment.Tier.TIER_0, () -> Ingredient.ofItems(Items.STICK),
                    List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fire_scorch.id()))
            .translatedName("Novice Wand")
    );
    public static final Weapon.Entry arcaneWand = add(Weapons.damageWand(
            NAMESPACE, "wand_arcane",
                    Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
                    List.of(SpellSchools.ARCANE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_bolt.id()))
            .translatedName("Arcane Wand")
    );
    public static final Weapon.Entry fireWand = add(Weapons.damageWand(
            NAMESPACE, "wand_fire",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
            List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fireball.id()))
            .translatedName("Fire Wand")
    );
    public static final Weapon.Entry frostWand = add(Weapons.damageWand(
            NAMESPACE, "wand_frost",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.IRON_INGOT),
            List.of(SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frost_shard.id()))
            .translatedName("Frost Wand")
    );

    public static final Weapon.Entry netheriteArcaneWand = add(Weapons.damageWand(
            NAMESPACE, "wand_netherite_arcane",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.ARCANE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_bolt.id()))
            .translatedName("Netherite Arcane Wand")
    );
    public static final Weapon.Entry netheriteFireWand = add(Weapons.damageWand(
            NAMESPACE, "wand_netherite_fire",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fireball.id()))
            .translatedName("Netherite Fire Wand")
    );
    public static final Weapon.Entry netheriteFrostWand = add(Weapons.damageWand(
            NAMESPACE, "wand_netherite_frost",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frost_shard.id()))
            .translatedName("Netherite Frost Wand")
    );

    // MARK: Staves

    public static final Weapon.Entry wizardStaff = add(Weapons.damageStaff(
                    NAMESPACE, "staff_wizard",
                    Equipment.Tier.TIER_1, () -> Ingredient.ofItems(Items.STICK),
                    List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices("wizards:weapon/wizard_staff")
            .translatedName("Wizard Staff")
    );
    public static final Weapon.Entry arcaneStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_arcane",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
            List.of(SpellSchools.ARCANE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_blast.id()))
            .translatedName("Arcane Staff")
    );
    public static final Weapon.Entry fireStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_fire",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
            List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fire_blast.id()))
            .translatedName("Fire Staff")
    );
    public static final Weapon.Entry frostStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_frost",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.IRON_INGOT),
            List.of(SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frostbolt.id()))
            .translatedName("Frost Staff")
    );

    public static final Weapon.Entry netheriteArcaneStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_netherite_arcane",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.ARCANE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_blast.id()))
            .translatedName("Netherite Arcane Staff")
    );
    public static final Weapon.Entry netheriteFireStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_netherite_fire",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fire_blast.id()))
            .translatedName("Netherite Fire Staff")
    );
    public static final Weapon.Entry netheriteFrostStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_netherite_frost",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frostbolt.id()))
            .translatedName("Netherite Frost Staff")
    );

    // MARK: Register

    public static void register(Map<String, WeaponConfig> configs) {
        itemsToRegister(configs).forEach((id, item) -> Registry.register(Registries.ITEM, id, item));
    }

    /// Creates and configures every Wizards weapon and returns it keyed by the id it registers under.
    /// Creation only — nothing is written into the ITEM registry here, so a loader that registers items
    /// itself (Forge) iterates this instead of calling {@link #register}. **Must run inside the ITEM
    /// registration window**: `Item`'s constructor takes an intrusive registry holder.
    ///
    /// The compat entries below are appended here rather than in `<clinit>` because they are conditional on
    /// other mods being present, which is only knowable after mod loading has begun. Guarded so the extra
    /// `add(...)` calls happen exactly once.
    public static Map<Identifier, Item> itemsToRegister(Map<String, WeaponConfig> configs) {
        addCompatEntries();
        return Weapon.itemsToRegister(configs, entries, Group.KEY);
    }

    private static boolean compatEntriesAdded = false;
    private static void addCompatEntries() {
        if (compatEntriesAdded) {
            return;
        }
        compatEntriesAdded = true;
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || Platform.util().isModLoaded(BETTER_NETHER)) {
            var repair = ingredient("betternether:nether_ruby", Platform.util().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            add(Weapons.damageStaff(NAMESPACE, "staff_ruby_fire", Equipment.Tier.TIER_4, repair, List.of(SpellSchools.FIRE.id))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fire_blast.id()))
            );
        }
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || Platform.util().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", Platform.util().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            add(Weapons.damageStaff(NAMESPACE, "staff_crystal_arcane", Equipment.Tier.TIER_4, repair, List.of(SpellSchools.ARCANE.id))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_blast.id()))
            );
            add(Weapons.damageStaff(NAMESPACE, "staff_smaragdant_frost", Equipment.Tier.TIER_4, repair, List.of(SpellSchools.FROST.id))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frostbolt.id()))
            );
        }
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || Platform.util().isModLoaded(AETHER)) {
            var repair = ingredient("aether:ambrosium_shard", Platform.util().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            add(Weapons.damageStaff(NAMESPACE, "aether_wizard_staff", Equipment.Tier.TIER_4, repair,
                    List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id)))
                    .loot(Equipment.LootProperties.of("aether"))
                    .spellContainer(SpellContainers.forMagicWeapon())
                    .withSpellChoices("wizards:weapon/wizard_staff");
        }
    }
}
