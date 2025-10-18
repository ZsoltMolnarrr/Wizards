package net.wizards.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.wizards.WizardsMod;

import java.util.concurrent.CompletableFuture;

/**
 * Generates all crafting recipes for the Wizards mod using Fabric's built-in API.
 * Conditional recipes (staff_ruby_fire, staff_smaragdant_frost, staff_crystal_arcane)
 * are kept as hand-written JSONs.
 */
public class WizardRecipes extends FabricRecipeProvider {

    public WizardRecipes(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateWandRecipes(exporter);
        generateStaffRecipes(exporter);
        generateArmorRecipes(exporter);
        generateNetheriteUpgrades(exporter);
    }

    // ========================================
    // WAND RECIPES
    // ========================================

    private void generateWandRecipes(RecipeExporter exporter) {
        // Novice Wand - coal + stick
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item("wand_novice"))
                .pattern(" C")
                .pattern("S ")
                .input('C', Items.COAL)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.COAL), conditionsFromItem(Items.COAL))
                .offerTo(exporter);

        // Arcane Wand - amethyst shard + gold ingot
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item("wand_arcane"))
                .pattern(" A")
                .pattern("G ")
                .input('A', Items.AMETHYST_SHARD)
                .input('G', Items.GOLD_INGOT)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter);

        // Fire Wand - gunpowder + gold ingot
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item("wand_fire"))
                .pattern(" A")
                .pattern("G ")
                .input('A', Items.GUNPOWDER)
                .input('G', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GUNPOWDER), conditionsFromItem(Items.GUNPOWDER))
                .offerTo(exporter);

        // Frost Wand - snowball + iron ingot
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item("wand_frost"))
                .pattern(" S")
                .pattern("I ")
                .input('S', Items.SNOWBALL)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(Items.SNOWBALL), conditionsFromItem(Items.SNOWBALL))
                .offerTo(exporter);
    }

    // ========================================
    // STAFF RECIPES
    // ========================================

    private void generateStaffRecipes(RecipeExporter exporter) {
        // Wizard Staff - quartz + stick
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item("staff_wizard"))
                .pattern("  Q")
                .pattern(" S ")
                .pattern("S  ")
                .input('Q', Items.QUARTZ)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.QUARTZ), conditionsFromItem(Items.QUARTZ))
                .offerTo(exporter);

        // Arcane Staff - amethyst shard + ender pearl + gold ingot + stick
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item("staff_arcane"))
                .pattern(" AP")
                .pattern(" SA")
                .pattern("G  ")
                .input('P', Items.ENDER_PEARL)
                .input('A', Items.AMETHYST_SHARD)
                .input('G', Items.GOLD_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter);

        // Fire Staff - blaze powder + nether brick + gold ingot + stick
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item("staff_fire"))
                .pattern(" NP")
                .pattern(" SN")
                .pattern("G  ")
                .input('P', Items.BLAZE_POWDER)
                .input('N', Items.NETHER_BRICK)
                .input('G', Items.GOLD_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.BLAZE_POWDER), conditionsFromItem(Items.BLAZE_POWDER))
                .offerTo(exporter);

        // Frost Staff - prismarine crystals + snowball + iron ingot + stick
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item("staff_frost"))
                .pattern(" BP")
                .pattern(" SB")
                .pattern("I  ")
                .input('P', Items.PRISMARINE_CRYSTALS)
                .input('B', Items.SNOWBALL)
                .input('I', Items.IRON_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.PRISMARINE_CRYSTALS), conditionsFromItem(Items.PRISMARINE_CRYSTALS))
                .offerTo(exporter);

        // Note: Conditional staves (ruby_fire, smaragdant_frost, crystal_arcane)
        // are kept as hand-written JSONs with mod load conditions
    }

    // ========================================
    // ARMOR RECIPES
    // ========================================

    private void generateArmorRecipes(RecipeExporter exporter) {
        // Wizard Robes - wool + lapis lazuli
        generateArmorSet(exporter, "wizard_robe", Items.LAPIS_LAZULI);

        // Arcane Robes - wool + ender pearl
        generateArmorSet(exporter, "arcane_robe", Items.ENDER_PEARL);

        // Fire Robes - wool + blaze powder
        generateArmorSet(exporter, "fire_robe", Items.BLAZE_POWDER);

        // Frost Robes - wool + prismarine shard
        generateArmorSet(exporter, "frost_robe", Items.PRISMARINE_SHARD);
    }

    /**
     * Generate all 4 armor pieces for a set using the standard robe patterns
     */
    private void generateArmorSet(RecipeExporter exporter, String armorName, Item specialIngredient) {
        // Helmet/Head - pattern: "  W" / " W " / "WLW"
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item(armorName + "_head"))
                .pattern("  W")
                .pattern(" W ")
                .pattern("WLW")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), conditionsFromItem(specialIngredient))
                .offerTo(exporter);

        // Chestplate - pattern: "L L" / "WLW" / "WWW"
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item(armorName + "_chest"))
                .pattern("L L")
                .pattern("WLW")
                .pattern("WWW")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), conditionsFromItem(specialIngredient))
                .offerTo(exporter);

        // Leggings - pattern: "LLL" / "W W" / "W W"
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item(armorName + "_legs"))
                .pattern("LLL")
                .pattern("W W")
                .pattern("W W")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), conditionsFromItem(specialIngredient))
                .offerTo(exporter);

        // Boots - pattern: "L L" / "W W"
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, item(armorName + "_feet"))
                .pattern("L L")
                .pattern("W W")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), conditionsFromItem(specialIngredient))
                .offerTo(exporter);
    }

    // ========================================
    // NETHERITE UPGRADE RECIPES
    // ========================================

    private void generateNetheriteUpgrades(RecipeExporter exporter) {
        // Wand upgrades
        offerNetheriteUpgradeRecipe(exporter, item("wand_arcane"), RecipeCategory.COMBAT, item("wand_netherite_arcane"));
        offerNetheriteUpgradeRecipe(exporter, item("wand_fire"), RecipeCategory.COMBAT, item("wand_netherite_fire"));
        offerNetheriteUpgradeRecipe(exporter, item("wand_frost"), RecipeCategory.COMBAT, item("wand_netherite_frost"));

        // Staff upgrades
        offerNetheriteUpgradeRecipe(exporter, item("staff_arcane"), RecipeCategory.COMBAT, item("staff_netherite_arcane"));
        offerNetheriteUpgradeRecipe(exporter, item("staff_fire"), RecipeCategory.COMBAT, item("staff_netherite_fire"));
        offerNetheriteUpgradeRecipe(exporter, item("staff_frost"), RecipeCategory.COMBAT, item("staff_netherite_frost"));

        // Armor upgrades - Arcane set
        offerNetheriteUpgradeRecipe(exporter, item("arcane_robe_head"), RecipeCategory.COMBAT, item("netherite_arcane_robe_head"));
        offerNetheriteUpgradeRecipe(exporter, item("arcane_robe_chest"), RecipeCategory.COMBAT, item("netherite_arcane_robe_chest"));
        offerNetheriteUpgradeRecipe(exporter, item("arcane_robe_legs"), RecipeCategory.COMBAT, item("netherite_arcane_robe_legs"));
        offerNetheriteUpgradeRecipe(exporter, item("arcane_robe_feet"), RecipeCategory.COMBAT, item("netherite_arcane_robe_feet"));

        // Armor upgrades - Fire set
        offerNetheriteUpgradeRecipe(exporter, item("fire_robe_head"), RecipeCategory.COMBAT, item("netherite_fire_robe_head"));
        offerNetheriteUpgradeRecipe(exporter, item("fire_robe_chest"), RecipeCategory.COMBAT, item("netherite_fire_robe_chest"));
        offerNetheriteUpgradeRecipe(exporter, item("fire_robe_legs"), RecipeCategory.COMBAT, item("netherite_fire_robe_legs"));
        offerNetheriteUpgradeRecipe(exporter, item("fire_robe_feet"), RecipeCategory.COMBAT, item("netherite_fire_robe_feet"));

        // Armor upgrades - Frost set
        offerNetheriteUpgradeRecipe(exporter, item("frost_robe_head"), RecipeCategory.COMBAT, item("netherite_frost_robe_head"));
        offerNetheriteUpgradeRecipe(exporter, item("frost_robe_chest"), RecipeCategory.COMBAT, item("netherite_frost_robe_chest"));
        offerNetheriteUpgradeRecipe(exporter, item("frost_robe_legs"), RecipeCategory.COMBAT, item("netherite_frost_robe_legs"));
        offerNetheriteUpgradeRecipe(exporter, item("frost_robe_feet"), RecipeCategory.COMBAT, item("netherite_frost_robe_feet"));
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Get a wizard mod item by name
     */
    private Item item(String name) {
        return Registries.ITEM.get(Identifier.of(WizardsMod.ID, name));
    }

    @Override
    public String getName() {
        return "Wizard Crafting Recipes";
    }
}
