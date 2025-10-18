package net.wizards.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.spell_engine.api.item.armor.Armor;
import net.wizards.item.WizardArmors;
import net.wizards.item.WizardWeapons;

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
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WizardWeapons.noviceWand.item())
                .pattern(" C")
                .pattern("S ")
                .input('C', Items.COAL)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.COAL), conditionsFromItem(Items.COAL))
                .offerTo(exporter);

        // Arcane Wand - amethyst shard + gold ingot
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WizardWeapons.arcaneWand.item())
                .pattern(" A")
                .pattern("G ")
                .input('A', Items.AMETHYST_SHARD)
                .input('G', Items.GOLD_INGOT)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter);

        // Fire Wand - gunpowder + gold ingot
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WizardWeapons.fireWand.item())
                .pattern(" A")
                .pattern("G ")
                .input('A', Items.GUNPOWDER)
                .input('G', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GUNPOWDER), conditionsFromItem(Items.GUNPOWDER))
                .offerTo(exporter);

        // Frost Wand - snowball + iron ingot
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WizardWeapons.frostWand.item())
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
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WizardWeapons.wizardStaff.item())
                .pattern("  Q")
                .pattern(" S ")
                .pattern("S  ")
                .input('Q', Items.QUARTZ)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.QUARTZ), conditionsFromItem(Items.QUARTZ))
                .offerTo(exporter);

        // Arcane Staff - amethyst shard + ender pearl + gold ingot + stick
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WizardWeapons.arcaneStaff.item())
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
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WizardWeapons.fireStaff.item())
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
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WizardWeapons.frostStaff.item())
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
        generateArmorSet(exporter, WizardArmors.wizardRobeSet, Items.LAPIS_LAZULI);

        // Arcane Robes - wool + ender pearl
        generateArmorSet(exporter, WizardArmors.arcaneRobeSet, Items.ENDER_PEARL);

        // Fire Robes - wool + blaze powder
        generateArmorSet(exporter, WizardArmors.fireRobeSet, Items.BLAZE_POWDER);

        // Frost Robes - wool + prismarine shard
        generateArmorSet(exporter, WizardArmors.frostRobeSet, Items.PRISMARINE_SHARD);
    }

    /**
     * Generate all 4 armor pieces for a set using the standard robe patterns
     */
    private void generateArmorSet(RecipeExporter exporter, Armor.Set set, Item specialIngredient) {
        // Helmet/Head - pattern: "  W" / " W " / "WLW"
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, set.head)
                .pattern("  W")
                .pattern(" W ")
                .pattern("WLW")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), conditionsFromItem(specialIngredient))
                .offerTo(exporter);

        // Chestplate - pattern: "L L" / "WLW" / "WWW"
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, set.chest)
                .pattern("L L")
                .pattern("WLW")
                .pattern("WWW")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), conditionsFromItem(specialIngredient))
                .offerTo(exporter);

        // Leggings - pattern: "LLL" / "W W" / "W W"
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, set.legs)
                .pattern("LLL")
                .pattern("W W")
                .pattern("W W")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), conditionsFromItem(specialIngredient))
                .offerTo(exporter);

        // Boots - pattern: "L L" / "W W"
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, set.feet)
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
        offerNetheriteUpgradeRecipe(exporter, WizardWeapons.arcaneWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteArcaneWand.item());
        offerNetheriteUpgradeRecipe(exporter, WizardWeapons.fireWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFireWand.item());
        offerNetheriteUpgradeRecipe(exporter, WizardWeapons.frostWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFrostWand.item());

        // Staff upgrades
        offerNetheriteUpgradeRecipe(exporter, WizardWeapons.arcaneStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteArcaneStaff.item());
        offerNetheriteUpgradeRecipe(exporter, WizardWeapons.fireStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFireStaff.item());
        offerNetheriteUpgradeRecipe(exporter, WizardWeapons.frostStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFrostStaff.item());

        // Armor upgrades - Arcane set
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.arcaneRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.head);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.arcaneRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.chest);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.arcaneRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.legs);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.arcaneRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.feet);

        // Armor upgrades - Fire set
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.fireRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_fire.head);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.fireRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_fire.chest);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.fireRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_fire.legs);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.fireRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_fire.feet);

        // Armor upgrades - Frost set
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.frostRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_frost.head);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.frostRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_frost.chest);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.frostRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_frost.legs);
        offerNetheriteUpgradeRecipe(exporter, WizardArmors.frostRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_frost.feet);
    }

    // ========================================
    // HELPER METHODS
    // ========================================


    @Override
    public String getName() {
        return "Wizard Crafting Recipes";
    }
}
