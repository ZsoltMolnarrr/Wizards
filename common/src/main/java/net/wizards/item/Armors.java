package net.wizards.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.wizards.WizardsMod;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Armors {
    private static final Supplier<Ingredient> WOOL_INGREDIENTS = () -> { return Ingredient.ofItems(
            Items.WHITE_WOOL,
            Items.ORANGE_WOOL,
            Items.MAGENTA_WOOL,
            Items.LIGHT_BLUE_WOOL,
            Items.YELLOW_WOOL,
            Items.LIME_WOOL,
            Items.PINK_WOOL,
            Items.GRAY_WOOL,
            Items.LIGHT_GRAY_WOOL,
            Items.CYAN_WOOL,
            Items.PURPLE_WOOL,
            Items.BLUE_WOOL,
            Items.BROWN_WOOL,
            Items.GREEN_WOOL,
            Items.RED_WOOL,
            Items.BLACK_WOOL);
    };

    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(Armor.CustomMaterial material, ItemConfig.ArmorSet defaults) {
        return new Armor.Entry(material, null, defaults);
    }

    public static final Armor.Set wizardRobeSet =
            create(
                    new Armor.CustomMaterial(
                        "wizard_robe",
                        10,
                        9,
                        WizardArmor.equipSound,
                        WOOL_INGREDIENTS
                    ),
                    ItemConfig.ArmorSet.with(
                        new ItemConfig.ArmorSet.Piece(1)
                                .addAll(ItemConfig.SpellAttribute.bonuses(EnumSet.of(MagicSchool.ARCANE, MagicSchool.FIRE, MagicSchool.FROST), 1)),
                        new ItemConfig.ArmorSet.Piece(3)
                                .addAll(ItemConfig.SpellAttribute.bonuses(EnumSet.of(MagicSchool.ARCANE, MagicSchool.FIRE, MagicSchool.FROST), 1)),
                        new ItemConfig.ArmorSet.Piece(2)
                                .addAll(ItemConfig.SpellAttribute.bonuses(EnumSet.of(MagicSchool.ARCANE, MagicSchool.FIRE, MagicSchool.FROST), 1)),
                        new ItemConfig.ArmorSet.Piece(1)
                                .addAll(ItemConfig.SpellAttribute.bonuses(EnumSet.of(MagicSchool.ARCANE, MagicSchool.FIRE, MagicSchool.FROST), 1))
                    ))
            .armorSet(material -> new Armor.Set(WizardsMod.ID,
                    new WizardArmor(material, EquipmentSlot.HEAD, new Item.Settings().group(Group.WIZARDS)),
                    new WizardArmor(material, EquipmentSlot.CHEST, new Item.Settings().group(Group.WIZARDS)),
                    new WizardArmor(material, EquipmentSlot.LEGS, new Item.Settings().group(Group.WIZARDS)),
                    new WizardArmor(material, EquipmentSlot.FEET, new Item.Settings().group(Group.WIZARDS))
            ), entries);


    private static final float specializedRobeSpellPower = 0.25F;
    private static final float specializedRobeCritDamage = 0.1F;
    private static final float specializedRobeCritChance = 0.02F;
    private static final float specializedRobeHaste = 0.03F;

    public static final Armor.Set arcaneRobeSet =
            create(
                    new Armor.CustomMaterial(
                            "arcane_robe",
                            20,
                            10,
                            WizardArmor.equipSound,
                            WOOL_INGREDIENTS
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_DAMAGE, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_DAMAGE, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_DAMAGE, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_DAMAGE, specializedRobeCritDamage)
                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(WizardsMod.ID,
                            new WizardArmor(material, EquipmentSlot.HEAD, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.CHEST, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.LEGS, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.FEET, new Item.Settings().group(Group.WIZARDS))
                    ), entries);

    public static final Armor.Set fireRobeSet =
            create(
                    new Armor.CustomMaterial(
                            "fire_robe",
                            20,
                            10,
                            WizardArmor.equipSound,
                            WOOL_INGREDIENTS
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_CHANCE, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_CHANCE, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_CHANCE, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_CHANCE, specializedRobeCritChance)
                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(WizardsMod.ID,
                            new WizardArmor(material, EquipmentSlot.HEAD, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.CHEST, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.LEGS, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.FEET, new Item.Settings().group(Group.WIZARDS))
                    ), entries);

    public static final Armor.Set frostRobeSet =
            create(
                    new Armor.CustomMaterial(
                            "frost_robe",
                            20,
                            10,
                            WizardArmor.equipSound,
                            WOOL_INGREDIENTS
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.HASTE, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.HASTE, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.HASTE, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.HASTE, specializedRobeHaste)
                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(WizardsMod.ID,
                            new WizardArmor(material, EquipmentSlot.HEAD, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.CHEST, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.LEGS, new Item.Settings().group(Group.WIZARDS)),
                            new WizardArmor(material, EquipmentSlot.FEET, new Item.Settings().group(Group.WIZARDS))
                    ), entries);


    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries);
    }
}

