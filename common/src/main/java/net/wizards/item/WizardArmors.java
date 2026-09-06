package net.wizards.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.spell_engine.rpg_series.config.ArmorSetConfig;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;
import net.wizards.WizardsMod;
import net.wizards.content.WizardsSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WizardArmors {
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

    /// 1.20.1: `ArmorMaterial` is a plain interface — no registry, no `Layer` list. SpellEngine's
    /// `Armor.material(...)` builds a `CustomMaterial` whose `id` doubles as the (single) layer id,
    /// so `textures/models/armor/<name>_layer_{1,2}.png` keeps working unchanged.
    public static ArmorMaterial material(String name,
                                         int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
                                         int enchantability, SoundEvent equipSound, Supplier<Ingredient> repairIngredient) {
        return Armor.material(
                new Identifier(WizardsMod.ID, name),
                Map.of(
                ArmorItem.Type.HELMET, protectionHead,
                ArmorItem.Type.CHESTPLATE, protectionChest,
                ArmorItem.Type.LEGGINGS, protectionLegs,
                ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                0, 0);
    }

    public static ArmorMaterial material_wizard = material(
            "wizard_robe",
            1, 3, 2, 1,
            9,
            WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent(), WOOL_INGREDIENTS);

    public static ArmorMaterial material_arcane = material(
            "arcane_robe",
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent(), WOOL_INGREDIENTS);

    public static ArmorMaterial material_fire = material(
            "fire_robe",
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent(), WOOL_INGREDIENTS);

    public static ArmorMaterial material_frost = material(
            "frost_robe",
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent(), WOOL_INGREDIENTS);

    public static ArmorMaterial material_netherite_arcane = material(
            "netherite_arcane_robe",
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static ArmorMaterial material_netherite_fire = material(
            "netherite_fire_robe",
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static ArmorMaterial material_netherite_frost = material(
            "netherite_frost_robe",
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(ArmorMaterial material, Identifier id, int durability, Armor.Set.ItemFactory factory, ArmorSetConfig defaults, int tier) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults,
                Equipment.LootProperties.of(tier)
        );
        entries.add(entry);
        return entry;
    }

    private static final float spell_power_t1 = 0.2F;
    private static final float spell_power_t2 = 0.25F;
    private static final float spell_power_t3 = 0.3F;

    private static final float haste_t2 = 0.02F;
    private static final float haste_t3 = 0.03F;

    private static final float crit_damage_t2 = 0.05F;
    private static final float crit_chance_t3 = 0.03F;

    private static final float crit_chance_t2 = 0.02F;
    private static final float crit_damage_t3 = 0.06F;

    public static final Armor.Set wizardRobeSet = create(
            material_wizard,
            new Identifier(WizardsMod.ID, "wizard_robe"),
            10,
            WizardArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t1))
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t1))
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t1)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t1))
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t1))
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t1)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t1))
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t1))
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t1)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t1))
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t1))
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t1))
            ), 1)
            .translatedName("Wizard Hat", "Wizard Robe Top", "Wizard Robe Bottom", "Wizard Boots")
            .armorSet();

    public static final Armor.Set arcaneRobeSet = create(
            material_arcane,
            new Identifier(WizardsMod.ID, "arcane_robe"),
            20,
            WizardArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t2)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t2)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t2)
                            )),
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t2)
                            ))
            ), 2)
            .translatedName("Arcane Hat", "Arcane Robe Top", "Arcane Robe Bottom", "Arcane Boots")
            .armorSet();

    public static final Armor.Set fireRobeSet = create(
            material_fire,
            new Identifier(WizardsMod.ID, "fire_robe"),
            20,
            WizardArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t2)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t2)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t2)
                            )),
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t2)
                            ))
            ), 2)
            .translatedName("Fire Hat", "Fire Robe Top", "Fire Robe Bottom", "Fire Boots")
            .armorSet();

    public static final Armor.Set frostRobeSet = create(
            material_frost,
            new Identifier(WizardsMod.ID, "frost_robe"),
            20,
            WizardArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t2)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t2)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t2)
                            )),
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t2),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t2)
                            ))
            ), 2)
            .translatedName("Frost Hat", "Frost Robe Top", "Frost Robe Bottom", "Frost Boots")
            .armorSet();


    public static final Armor.Set netherite_arcane = create(
            material_netherite_arcane,
            new Identifier(WizardsMod.ID, "netherite_arcane_robe"),
            30,
            WizardArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)
                            )),
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.ARCANE.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)
                            ))
            ), 3)
            .translatedName("Netherite Arcane Hat", "Netherite Arcane Robe Top", "Netherite Arcane Robe Bottom", "Netherite Arcane Boots")
            .armorSet();

    public static final Armor.Set netherite_fire = create(
            material_netherite_fire,
            new Identifier(WizardsMod.ID, "netherite_fire_robe"),
            30,
            WizardArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)
                            )),
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)
                            ))
            ), 3)
            .translatedName("Netherite Fire Hat", "Netherite Fire Robe Top", "Netherite Fire Robe Bottom", "Netherite Fire Boots")
            .armorSet();

    public static final Armor.Set netherite_frost = create(
            material_netherite_frost,
            new Identifier(WizardsMod.ID, "netherite_frost_robe"),
            30,
            WizardArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)
                            )),
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.FROST.id, spell_power_t3),
                                    AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)
                            ))
            ), 3)
            .translatedName("Netherite Frost Hat", "Netherite Frost Robe Top", "Netherite Frost Robe Bottom", "Netherite Frost Boots")
            .armorSet();

    public static void register(Map<String, ArmorSetConfig> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}

