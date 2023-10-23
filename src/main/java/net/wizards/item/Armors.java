package net.wizards.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
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
                                .addAll(ItemConfig.Attribute.bonuses(EnumSet.of(MagicSchool.ARCANE, MagicSchool.FIRE, MagicSchool.FROST), 1)),
                        new ItemConfig.ArmorSet.Piece(3)
                                .addAll(ItemConfig.Attribute.bonuses(EnumSet.of(MagicSchool.ARCANE, MagicSchool.FIRE, MagicSchool.FROST), 1)),
                        new ItemConfig.ArmorSet.Piece(2)
                                .addAll(ItemConfig.Attribute.bonuses(EnumSet.of(MagicSchool.ARCANE, MagicSchool.FIRE, MagicSchool.FROST), 1)),
                        new ItemConfig.ArmorSet.Piece(1)
                                .addAll(ItemConfig.Attribute.bonuses(EnumSet.of(MagicSchool.ARCANE, MagicSchool.FIRE, MagicSchool.FROST), 1))
                    ))
            .bundle(material -> new Armor.Set(WizardsMod.ID,
                    new WizardArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                    new WizardArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                    new WizardArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                    new WizardArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
            ))
            .put(entries)
            .armorSet();


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
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.CRITICAL_DAMAGE, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.CRITICAL_DAMAGE, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.CRITICAL_DAMAGE, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.CRITICAL_DAMAGE, specializedRobeCritDamage)
                                    ))
                    ))
                    .bundle(material -> new Armor.Set(WizardsMod.ID,
                            new WizardArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
                    ))
                    .put(entries)
                    .armorSet();

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
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.CRITICAL_CHANCE, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.CRITICAL_CHANCE, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.CRITICAL_CHANCE, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.CRITICAL_CHANCE, specializedRobeCritChance)
                                    ))
                    ))
                    .bundle(material -> new Armor.Set(WizardsMod.ID,
                            new WizardArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
                    ))
                    .put(entries)
                    .armorSet();

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
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.HASTE, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.HASTE, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.HASTE, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellAttributes.HASTE, specializedRobeHaste)
                                    ))
                    ))
                    .bundle(material -> new Armor.Set(WizardsMod.ID,
                            new WizardArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                            new WizardArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
                    ))
                    .put(entries)
                    .armorSet();

    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}

