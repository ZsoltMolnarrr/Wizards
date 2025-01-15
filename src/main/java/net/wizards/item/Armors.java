package net.wizards.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;
import net.wizards.WizardsMod;

import java.util.ArrayList;
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
    
	private static final Supplier<Ingredient> NETHERITE_INGREDIENTS = () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); };

    public static Armor.CustomMaterial material_wizard = new Armor.CustomMaterial(
        "wizard_robe",
        10,
        9,
        WizardArmor.equipSound,
        WOOL_INGREDIENTS
    );

    public static Armor.CustomMaterial material_arcane = new Armor.CustomMaterial(
        "arcane_robe",
        20,
        10,
        WizardArmor.equipSound,
        WOOL_INGREDIENTS
    );

	public static Armor.CustomMaterial material_fire = new Armor.CustomMaterial(
		"fire_robe",
		20,
		10,
		WizardArmor.equipSound,
		WOOL_INGREDIENTS
	);

	public static Armor.CustomMaterial material_frost = new Armor.CustomMaterial(
		"frost_robe",
		20,
		10,
		WizardArmor.equipSound,
		WOOL_INGREDIENTS
	);

	public static Armor.CustomMaterial material_netherite_frost = new Armor.CustomMaterial(
		"netherite_frost_robe",
		30,
		15,
		WizardArmor.equipSound,
		NETHERITE_INGREDIENTS
	);

	public static Armor.CustomMaterial material_netherite_arcane = new Armor.CustomMaterial(
        "netherite_arcane_robe",
        30,
        15,
        WizardArmor.equipSound,
        NETHERITE_INGREDIENTS
    );

	public static Armor.CustomMaterial material_netherite_fire = new Armor.CustomMaterial(
		"netherite_fire_robe",
		30,
		15,
		WizardArmor.equipSound,
		NETHERITE_INGREDIENTS
	);

    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(Armor.CustomMaterial material, ItemConfig.ArmorSet defaults) {
        return new Armor.Entry(material, null, defaults);
    }

    public static final Armor.Set wizardRobeSet =
            create(
                    material_wizard,
                    ItemConfig.ArmorSet.with(
                        new ItemConfig.ArmorSet.Piece(1)
                                .addAll(ItemConfig.Attribute.bonuses(List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id), 1)),
                        new ItemConfig.ArmorSet.Piece(3)
                                .addAll(ItemConfig.Attribute.bonuses(List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id), 1)),
                        new ItemConfig.ArmorSet.Piece(2)
                                .addAll(ItemConfig.Attribute.bonuses(List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id), 1)),
                        new ItemConfig.ArmorSet.Piece(1)
                                .addAll(ItemConfig.Attribute.bonuses(List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id), 1))
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
	private static final float specializedNetheriteRobeSpellPower = 0.30F;
    private static final float specializedRobeCritDamage = 0.1F;
    private static final float specializedRobeCritChance = 0.02F;
    private static final float specializedRobeHaste = 0.03F;

    public static final Armor.Set arcaneRobeSet =
            create(
                    material_arcane,
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.HASTE.id, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.HASTE.id, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.HASTE.id, specializedRobeHaste)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.HASTE.id, specializedRobeHaste)
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
                    material_fire,
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, specializedRobeCritChance)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, specializedRobeCritChance)
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
                    material_frost,
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.FROST.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.FROST.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.FROST.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, specializedRobeCritDamage)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(SpellSchools.FROST.id, specializedRobeSpellPower),
                                            ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, specializedRobeCritDamage)
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

	// netherite version of the armors, same armor points but 30% of the attribute instead of 25%.	

	public static final Armor.Set netherite_arcaneRobeSet =
	create(
			material_netherite_arcane,
			ItemConfig.ArmorSet.with(
					new ItemConfig.ArmorSet.Piece(1)
							.addAll(List.of(
									ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, specializedNetheriteRobeSpellPower),
									ItemConfig.Attribute.multiply(SpellPowerMechanics.HASTE.id, specializedRobeHaste)
							)),
					new ItemConfig.ArmorSet.Piece(3)
							.addAll(List.of(
									ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, specializedNetheriteRobeSpellPower),
									ItemConfig.Attribute.multiply(SpellPowerMechanics.HASTE.id, specializedRobeHaste)
							)),
					new ItemConfig.ArmorSet.Piece(2)
							.addAll(List.of(
									ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, specializedNetheriteRobeSpellPower),
									ItemConfig.Attribute.multiply(SpellPowerMechanics.HASTE.id, specializedRobeHaste)
							)),
					new ItemConfig.ArmorSet.Piece(1)
							.addAll(List.of(
									ItemConfig.Attribute.multiply(SpellSchools.ARCANE.id, specializedNetheriteRobeSpellPower),
									ItemConfig.Attribute.multiply(SpellPowerMechanics.HASTE.id, specializedRobeHaste)
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

	public static final Armor.Set netherite_fireRobeSet =
		create(
				material_netherite_fire,
				ItemConfig.ArmorSet.with(
						new ItemConfig.ArmorSet.Piece(1)
								.addAll(List.of(
										ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, specializedNetheriteRobeSpellPower),
										ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, specializedRobeCritChance)
								)),
						new ItemConfig.ArmorSet.Piece(3)
								.addAll(List.of(
										ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, specializedNetheriteRobeSpellPower),
										ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, specializedRobeCritChance)
								)),
						new ItemConfig.ArmorSet.Piece(2)
								.addAll(List.of(
										ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, specializedNetheriteRobeSpellPower),
										ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, specializedRobeCritChance)
								)),
						new ItemConfig.ArmorSet.Piece(1)
								.addAll(List.of(
										ItemConfig.Attribute.multiply(SpellSchools.FIRE.id, specializedNetheriteRobeSpellPower),
										ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, specializedRobeCritChance)
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

	public static final Armor.Set netherite_frostRobeSet =
		create(
				material_netherite_frost,
				ItemConfig.ArmorSet.with(
						new ItemConfig.ArmorSet.Piece(1)
								.addAll(List.of(
										ItemConfig.Attribute.multiply(SpellSchools.FROST.id, specializedNetheriteRobeSpellPower),
										ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, specializedRobeCritDamage)
								)),
						new ItemConfig.ArmorSet.Piece(3)
								.addAll(List.of(
										ItemConfig.Attribute.multiply(SpellSchools.FROST.id, specializedNetheriteRobeSpellPower),
										ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, specializedRobeCritDamage)
								)),
						new ItemConfig.ArmorSet.Piece(2)
								.addAll(List.of(
										ItemConfig.Attribute.multiply(SpellSchools.FROST.id, specializedNetheriteRobeSpellPower),
										ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, specializedRobeCritDamage)
								)),
						new ItemConfig.ArmorSet.Piece(1)
								.addAll(List.of(
										ItemConfig.Attribute.multiply(SpellSchools.FROST.id, specializedNetheriteRobeSpellPower),
										ItemConfig.Attribute.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, specializedRobeCritDamage)
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

