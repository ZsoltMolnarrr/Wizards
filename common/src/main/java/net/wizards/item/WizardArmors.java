package net.wizards.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
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
    /// Repair ingredients are tags since 1.21.2 (`ArmorMaterial.repairIngredient`).
    /// `#minecraft:wool` is exactly the 16 wool items the 1.21.1 ingredient listed.
    private static final TagKey<Item> WOOL_INGREDIENTS = ItemTags.WOOL;
    private static final TagKey<Item> NETHERITE_INGREDIENTS = ItemTags.REPAIRS_NETHERITE_ARMOR;

    /// 1.21.4 replaced armor material layers with equipment assets
    /// (`assets/<ns>/equipment/<name>.json`). Wizards renders its armor through
    /// ArmorModelAPI's geo renderers, so no asset file is shipped — the loader falls back to an
    /// empty model and only the geo pass draws.
    private static ResourceKey<EquipmentAsset> assetId(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(WizardsMod.ID, name));
    }

    /// `ArmorMaterial` is a plain record since 1.21.2 — no registry, no `RegistryEntry`.
    /// `durability` here must match the value passed to `Armor.Entry.create`, because
    /// `Item.Settings.armor(material, type)` recomputes `maxDamage` from the material.
    public static ArmorMaterial material(String name,
                                         int durability,
                                         int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
                                         int enchantability, Holder<SoundEvent> equipSound, TagKey<Item> repairIngredient) {
        return new ArmorMaterial(
                durability,
                Map.of(
                        ArmorType.HELMET, protectionHead,
                        ArmorType.CHESTPLATE, protectionChest,
                        ArmorType.LEGGINGS, protectionLegs,
                        ArmorType.BOOTS, protectionFeet),
                enchantability,
                equipSound,
                0F,
                0F,
                repairIngredient,
                assetId(name));
    }

    public static ArmorMaterial material_wizard = material(
            "wizard_robe",
            10,
            1, 3, 2, 1,
            9,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), WOOL_INGREDIENTS);

    public static ArmorMaterial material_arcane = material(
            "arcane_robe",
            20,
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), WOOL_INGREDIENTS);

    public static ArmorMaterial material_fire = material(
            "fire_robe",
            20,
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), WOOL_INGREDIENTS);

    public static ArmorMaterial material_frost = material(
            "frost_robe",
            20,
            1, 3, 2, 1,
            10,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), WOOL_INGREDIENTS);

    public static ArmorMaterial material_netherite_arcane = material(
            "netherite_arcane_robe",
            30,
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), NETHERITE_INGREDIENTS);

    public static ArmorMaterial material_netherite_fire = material(
            "netherite_fire_robe",
            30,
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), NETHERITE_INGREDIENTS);

    public static ArmorMaterial material_netherite_frost = material(
            "netherite_frost_robe",
            30,
            1, 3, 2, 1,
            15,
            WizardsSounds.WIZARD_ROBES_EQUIP.entry(), NETHERITE_INGREDIENTS);

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
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "wizard_robe"),
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
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "arcane_robe"),
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
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "fire_robe"),
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
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "frost_robe"),
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
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "netherite_arcane_robe"),
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
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "netherite_fire_robe"),
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
            Identifier.fromNamespaceAndPath(WizardsMod.ID, "netherite_frost_robe"),
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

