package net.wizards.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.AttributeModifier;
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

    public static RegistryEntry<ArmorMaterial> material(String name,
                                         int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
                                         int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient) {
        var material = new ArmorMaterial(
                Map.of(
                ArmorItem.Type.HELMET, protectionHead,
                ArmorItem.Type.CHESTPLATE, protectionChest,
                ArmorItem.Type.LEGGINGS, protectionLegs,
                ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                List.of(new ArmorMaterial.Layer(Identifier.of(WizardsMod.ID, name))),
                0,0
                );
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(WizardsMod.ID, name), material);
    }

    public static RegistryEntry<ArmorMaterial> material_wizard = material(
            "wizard_robe",
            1, 3, 2, 1,
            9,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), WOOL_INGREDIENTS);

    public static RegistryEntry<ArmorMaterial> material_arcane = material(
            "arcane_robe",
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), WOOL_INGREDIENTS);

    public static RegistryEntry<ArmorMaterial> material_fire = material(
            "fire_robe",
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), WOOL_INGREDIENTS);

    public static RegistryEntry<ArmorMaterial> material_frost = material(
            "frost_robe",
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), WOOL_INGREDIENTS);

    public static RegistryEntry<ArmorMaterial> material_netherite_arcane = material(
            "netherite_arcane_robe",
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> material_netherite_fire = material(
            "netherite_fire_robe",
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> material_netherite_frost = material(
            "netherite_frost_robe",
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability, Armor.Set.ItemFactory factory, ArmorSetConfig defaults, int tier) {
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
            Identifier.of(WizardsMod.ID, "wizard_robe"),
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
            .armorSet();

    public static final Armor.Set arcaneRobeSet = create(
            material_arcane,
            Identifier.of(WizardsMod.ID, "arcane_robe"),
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
            .armorSet();

    public static final Armor.Set fireRobeSet = create(
            material_fire,
            Identifier.of(WizardsMod.ID, "fire_robe"),
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
            .armorSet();

    public static final Armor.Set frostRobeSet = create(
            material_frost,
            Identifier.of(WizardsMod.ID, "frost_robe"),
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
            .armorSet();


    public static final Armor.Set netherite_arcane = create(
            material_netherite_arcane,
            Identifier.of(WizardsMod.ID, "netherite_arcane_robe"),
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
            .armorSet();

    public static final Armor.Set netherite_fire = create(
            material_netherite_fire,
            Identifier.of(WizardsMod.ID, "netherite_fire_robe"),
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
            .armorSet();

    public static final Armor.Set netherite_frost = create(
            material_netherite_frost,
            Identifier.of(WizardsMod.ID, "netherite_frost_robe"),
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
            .armorSet();

    public static void register(Map<String, ArmorSetConfig> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}

