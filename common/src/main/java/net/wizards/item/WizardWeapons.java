package net.wizards.item;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.rpg_series.item.Weapons;
import net.spell_power.api.SpellSchools;
import net.wizards.WizardsMod;

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

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults, Equipment.WeaponType category) {
        var entry = new Weapon.Entry(WizardsMod.ID, name, material, factory, defaults, category);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
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

    private static final float wandAttackDamage = 2;
    private static final float wandAttackSpeed = -2.4F;

    // Wand spell power bonuses
    private static final float T0_WAND_POWER = 3F;
    private static final float T1_WAND_POWER = 4F;
    private static final float T2_WAND_POWER = 5F;
    private static final float T3_WAND_POWER = 5.5F;
    private static final float T1_STAFF_POWER = 5F;
    private static final float T2_STAFF_POWER = 6F;
    private static final float T3_STAFF_POWER = 7F;
    private static final float T4_STAFF_POWER = 8F;

    private static Weapon.Entry wand(String name, Weapon.CustomMaterial material) {
        return entry(name, material, StaffItem::new, new WeaponConfig(wandAttackDamage, wandAttackSpeed), Equipment.WeaponType.DAMAGE_WAND);
    }

    public static final Weapon.Entry noviceWand = add(Weapons.damageWand(
                    NAMESPACE, "wand_novice",
                    Equipment.Tier.TIER_0, () -> Ingredient.ofItems(Items.STICK),
                    List.of(SpellSchools.FIRE.id))
            .spellContainer(SpellContainers.forMagicWeapon().withSpell("wizards:scorch"))
    );
    public static final Weapon.Entry arcaneWand = wand("wand_arcane",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T2_WAND_POWER))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry fireWand = wand("wand_fire",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, T2_WAND_POWER))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry frostWand = wand("wand_frost",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, T2_WAND_POWER))
            .loot(Equipment.LootProperties.of(2));

    public static final Weapon.Entry netheriteArcaneWand = wand("wand_netherite_arcane",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T3_WAND_POWER))
            .loot(Equipment.LootProperties.of(3));
    public static final Weapon.Entry netheriteFireWand = wand("wand_netherite_fire",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, T3_WAND_POWER))
            .loot(Equipment.LootProperties.of(3));
    public static final Weapon.Entry netheriteFrostWand = wand("wand_netherite_frost",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, T3_WAND_POWER))
            .loot(Equipment.LootProperties.of(3));


    // MARK: Staves

    private static final float staffAttackDamage = 4;
    private static final float staffAttackSpeed = -3F;

    private static Weapon.Entry staff(String name, Weapon.CustomMaterial material) {
        return entry(name, material, StaffItem::new, new WeaponConfig(staffAttackDamage, staffAttackSpeed), Equipment.WeaponType.DAMAGE_STAFF);
    }

    public static final Weapon.Entry wizardStaff = add(Weapons.damageWand(
                    NAMESPACE, "staff_wizard",
                    Equipment.Tier.TIER_1, () -> Ingredient.ofItems(Items.STICK),
                    List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id))
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices("wizards:weapon/wizard_staff")
    );
//    public static final Weapon.Entry wizardStaff = staff("staff_wizard",
//            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.STICK)))
//            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T1_STAFF_POWER))
//            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, T1_STAFF_POWER))
//            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, T1_STAFF_POWER))
//            .loot(Equipment.LootProperties.of(1));
    public static final Weapon.Entry arcaneStaff = staff("staff_arcane",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T2_STAFF_POWER))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry fireStaff = staff("staff_fire",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, T2_STAFF_POWER))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry frostStaff = staff("staff_frost",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.IRON_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, T2_STAFF_POWER))
            .loot(Equipment.LootProperties.of(2));

    public static final Weapon.Entry netheriteArcaneStaff = staff("staff_netherite_arcane",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T3_STAFF_POWER))
            .loot(Equipment.LootProperties.of(3));
    public static final Weapon.Entry netheriteFireStaff = staff("staff_netherite_fire",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, T3_STAFF_POWER))
            .loot(Equipment.LootProperties.of(3));
    public static final Weapon.Entry netheriteFrostStaff = staff("staff_netherite_frost",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, T3_STAFF_POWER))
            .loot(Equipment.LootProperties.of(3));

    // MARK: Register

    public static void register(Map<String, WeaponConfig> configs) {
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_NETHER)) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            staff("staff_ruby_fire",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, T4_STAFF_POWER))
                    .loot(Equipment.LootProperties.of(4));
        }
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            staff("staff_crystal_arcane",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_STAFF_POWER))
                    .loot(Equipment.LootProperties.of(4));
            staff("staff_smaragdant_frost",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, T4_STAFF_POWER))
                    .loot(Equipment.LootProperties.of(4));
        }
        if (WizardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(AETHER)) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            staff("aether_wizard_staff",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_STAFF_POWER))
                    .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, T4_STAFF_POWER))
                    .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, T4_STAFF_POWER))
                    .loot(Equipment.LootProperties.of("aether"));
        }


        Weapon.register(configs, entries, Group.KEY);
    }
}
