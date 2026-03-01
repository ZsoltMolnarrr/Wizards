package net.wizards.item;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.WeaponConfig;
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
        var id = Identifier.of(idString);
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
    );
    public static final Weapon.Entry arcaneWand = add(Weapons.damageWand(
            NAMESPACE, "wand_arcane",
                    Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
                    List.of(SpellSchools.ARCANE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_bolt.id()))
    );
    public static final Weapon.Entry fireWand = add(Weapons.damageWand(
            NAMESPACE, "wand_fire",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
            List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fireball.id()))
    );
    public static final Weapon.Entry frostWand = add(Weapons.damageWand(
            NAMESPACE, "wand_frost",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.IRON_INGOT),
            List.of(SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frost_shard.id()))
    );

    public static final Weapon.Entry netheriteArcaneWand = add(Weapons.damageWand(
            NAMESPACE, "wand_netherite_arcane",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.ARCANE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_bolt.id()))
    );
    public static final Weapon.Entry netheriteFireWand = add(Weapons.damageWand(
            NAMESPACE, "wand_netherite_fire",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fireball.id()))
    );
    public static final Weapon.Entry netheriteFrostWand = add(Weapons.damageWand(
            NAMESPACE, "wand_netherite_frost",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frost_shard.id()))
    );

    // MARK: Staves

    public static final Weapon.Entry wizardStaff = add(Weapons.damageStaff(
                    NAMESPACE, "staff_wizard",
                    Equipment.Tier.TIER_1, () -> Ingredient.ofItems(Items.STICK),
                    List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices("wizards:weapon/wizard_staff")
    );
    public static final Weapon.Entry arcaneStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_arcane",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
            List.of(SpellSchools.ARCANE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_blast.id()))
    );
    public static final Weapon.Entry fireStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_fire",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.GOLD_INGOT),
            List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fire_blast.id()))
    );
    public static final Weapon.Entry frostStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_frost",
            Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.IRON_INGOT),
            List.of(SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frostbolt.id()))
    );

    public static final Weapon.Entry netheriteArcaneStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_netherite_arcane",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.ARCANE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_blast.id()))
    );
    public static final Weapon.Entry netheriteFireStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_netherite_fire",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fire_blast.id()))
    );
    public static final Weapon.Entry netheriteFrostStaff = add(Weapons.damageStaff(
            NAMESPACE, "staff_netherite_frost",
            Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
            List.of(SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frostbolt.id()))
    );

    // MARK: Register

    public static void register(Map<String, WeaponConfig> configs) {
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_NETHER)) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            add(Weapons.damageStaff(NAMESPACE, "staff_ruby_fire", Equipment.Tier.TIER_4, repair, List.of(SpellSchools.FIRE.id))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.fire_blast.id()))
            );
        }
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            add(Weapons.damageStaff(NAMESPACE, "staff_crystal_arcane", Equipment.Tier.TIER_4, repair, List.of(SpellSchools.ARCANE.id))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.arcane_blast.id()))
            );
            add(Weapons.damageStaff(NAMESPACE, "staff_smaragdant_frost", Equipment.Tier.TIER_4, repair, List.of(SpellSchools.FROST.id))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(WizardSpells.frostbolt.id()))
            );
        }
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(AETHER)) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            add(Weapons.damageStaff(NAMESPACE, "aether_wizard_staff", Equipment.Tier.TIER_4, repair,
                    List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id)))
                    .loot(Equipment.LootProperties.of("aether"))
                    .spellContainer(SpellContainers.forMagicWeapon())
                    .withSpellChoices("wizards:weapon/wizard_staff");
        }

        Weapon.register(configs, entries, Group.KEY);
    }
}
