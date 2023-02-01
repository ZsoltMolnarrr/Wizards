package net.wizards.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.item.StaffItem;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.wizards.Platform;
import net.wizards.WizardsMod;
import net.wizards.config.ItemConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class Weapons {
    public static final ArrayList<Entry> entries = new ArrayList<>();

    public static final class Entry {
        private final String namespace;
        private final String name;
        private final Material material;
        private final Item item;
        private final ItemConfig.Weapon defaults;
        private @Nullable String requiredMod;

        public Entry(String namespace, String name, Material material, Item item, ItemConfig.Weapon defaults, @Nullable String requiredMod) {
            this.namespace = namespace;
            this.name = name;
            this.material = material;
            this.item = item;
            this.defaults = defaults;
            this.requiredMod = requiredMod;
        }

        public Identifier id() {
            return new Identifier(WizardsMod.ID, name);
        }

        public Entry attribute(ItemConfig.SpellAttribute attribute) {
            defaults.add(attribute);
            return this;
        }

        public Entry requires(String modName) {
            this.requiredMod = modName;
            return this;
        }

        public boolean isRequiredModInstalled() {
            if (requiredMod == null || requiredMod.isEmpty()) {
                return true;
            }
            return Platform.isModLoaded(requiredMod);
        }

        public String name() {
            return name;
        }

        public Material material() {
            return material;
        }

        public Item item() {
            return item;
        }

        public ItemConfig.Weapon defaults() {
            return defaults;
        }

        public @Nullable String requiredMod() {
            return requiredMod;
        }
    }

    private static Entry entry(String name, Material material, Item item, ItemConfig.Weapon defaults) {
        return entry(null, name, material, item, defaults);
    }

    private static Entry entry(String requiredMod, String name, Material material, Item item, ItemConfig.Weapon defaults) {
        var entry = new Entry(WizardsMod.ID, name, material, item, defaults, null);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }

    // MARK: Material

    public static class Material implements ToolMaterial {
        public static Material matching(ToolMaterials vanillaMaterial, Supplier<Ingredient> repairIngredient) {
            var material = new Material();
            material.miningLevel = vanillaMaterial.getMiningLevel();
            material.durability = vanillaMaterial.getDurability();
            material.miningSpeed = vanillaMaterial.getMiningSpeedMultiplier();
            material.enchantability = vanillaMaterial.getEnchantability();
            material.ingredient = new Lazy(repairIngredient);
            return material;
        }

        private int miningLevel = 0;
        private int durability = 0;
        private float miningSpeed = 0;
        private int enchantability = 0;
        private Lazy<Ingredient> ingredient = null;

        @Override
        public int getDurability() {
            return durability;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return miningSpeed;
        }

        @Override
        public float getAttackDamage() {
            return 0;
        }

        @Override
        public int getMiningLevel() {
            return miningLevel;
        }

        @Override
        public int getEnchantability() {
            return enchantability;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return (Ingredient)this.ingredient.get();
        }
    }


    private static Supplier<Ingredient> ingredient(String idString) {
        return ingredient(idString, Items.DIAMOND);
    }

    private static Supplier<Ingredient> ingredient(String idString, Item fallback) {
        var id = new Identifier(idString);
        return () -> { 
            var item = Registry.ITEM.get(id);
            var ingredient = item != null ? item : fallback;
            return Ingredient.ofItems(ingredient); 
        };
    }

    // MARK: Wands

    private static final float wandAttackDamage = 2;
    private static final float wandAttackSpeed = -2.4F;
    private static Entry wand(String name, Material material) {
        var settings = new Item.Settings().group(Group.WIZARDS);
        var item = new StaffItem(material, settings);
        return entry(name, material, item, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }

    public static final Entry noviceWand = wand("wand_novice",
            Material.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 1));
    public static final Entry arcaneWand = wand("wand_arcane",
            Material.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 2));
    public static final Entry fireWand = wand("wand_fire",
            Material.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 2));
    public static final Entry frostWand = wand("wand_frost",
            Material.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 2));

    public static final Entry netheriteArcaneWand = wand("wand_netherite_arcane",
            Material.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 3));
    public static final Entry netheriteFireWand = wand("wand_netherite_fire",
            Material.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 3));
    public static final Entry netheriteFrostWand = wand("wand_netherite_frost",
            Material.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 3));


    // MARK: Staves

    private static final float staffAttackDamage = 4;
    private static final float staffAttackSpeed = -3F;

    private static Entry staff(String name, Material material) {
        return staff(null, name, material);
    }

    private static Entry staff(String requiredMod, String name, Material material) {
        var settings = new Item.Settings().group(Group.WIZARDS);
        var item = new StaffItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(staffAttackDamage, staffAttackSpeed));
    }

    public static final Entry arcaneStaff = staff("staff_arcane",
            Material.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 4));
    public static final Entry fireStaff = staff("staff_fire",
            Material.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 4));
    public static final Entry frostStaff = staff("staff_frost",
            Material.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 4));

    public static final Entry netheriteArcaneStaff = staff("staff_netherite_arcane",
            Material.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 5));
    public static final Entry netheriteFireStaff = staff("staff_netherite_fire",
            Material.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 5));
    public static final Entry netheriteFrostStaff = staff("staff_netherite_frost",
            Material.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 5));

    public static final Entry crystalArcaneStaff = staff("betternether", "staff_crystal_arcane",
            Material.matching(ToolMaterials.NETHERITE, ingredient("betterend:aeternium_ingot")))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 6));
    public static final Entry rubyFireStaff = staff("betterend", "staff_ruby_fire",
            Material.matching(ToolMaterials.NETHERITE, ingredient("betternether:nether_ruby")))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 6));
    public static final Entry smaragdantFrostStaff = staff("betterend", "staff_smaragdant_frost",
            Material.matching(ToolMaterials.NETHERITE, ingredient("betterend:aeternium_ingot")))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 6));

    // MARK: Register

    public static void register(Map<String, ItemConfig.Weapon> configs) {
        for(var entry: entries) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };
            var item = entry.item();
            System.out.println(entry.name + " mod " + entry.requiredMod + " installed: " + entry.isRequiredModInstalled());
            if (!entry.isRequiredModInstalled()) { continue; }
            ((ConfigurableAttributes)item).setAttributes(attributesFrom(config));
            Registry.register(Registry.ITEM, entry.id(), item);
        }
    }

    private static Multimap<EntityAttribute, EntityAttributeModifier> attributesFrom(ItemConfig.Weapon config) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                        ItemAccessor.ATTACK_DAMAGE_MODIFIER_ID(),
                        "Weapon modifier",
                        config.attack_damage,
                        EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(
                        ItemAccessor.ATTACK_SPEED_MODIFIER_ID(),
                        "Weapon modifier",
                        config.attack_speed,
                        EntityAttributeModifier.Operation.ADDITION));
        for(var attribute: config.spell_attributes) {
            try {
                var entityAttribute = SpellAttributes.all.get(attribute.name).attribute;
                builder.put(entityAttribute,
                        new EntityAttributeModifier(
                                entityAttribute.weaponUUID,
                                "Weapon modifier",
                                attribute.value,
                                attribute.operation));
            } catch (Exception e) {
                System.err.println("Failed to add item attribute modifier: " + e.getMessage());
            }
        }
        return builder.build();
    }

    private static abstract class ItemAccessor extends Item {
        public ItemAccessor(Settings settings) { super(settings); }
        public static UUID ATTACK_DAMAGE_MODIFIER_ID() { return ATTACK_DAMAGE_MODIFIER_ID; }
        public static UUID ATTACK_SPEED_MODIFIER_ID() { return ATTACK_SPEED_MODIFIER_ID; }
    }
}
