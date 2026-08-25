package net.wizards.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.spell_engine.rpg_series.item.Armor;
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

    /// 1.21.2+ splits the provider from the generator: the provider is registered with the pack,
    /// the generator holds the exporter + the item registry lookup that every builder now needs.
    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        return new Generator(registries, exporter);
    }

    @Override
    public String getName() {
        return "Wizard Crafting Recipes";
    }

    private static class Generator extends RecipeGenerator {
        Generator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
            super(registries, exporter);
        }

    @Override
    public void generate() {
        generateWandRecipes();
        generateStaffRecipes();
        generateArmorRecipes();
        generateNetheriteUpgrades();
    }

    // ========================================
    // WAND RECIPES
    // ========================================

    private void generateWandRecipes() {
        // Novice Wand - coal + stick
        this.createShaped(RecipeCategory.COMBAT, WizardWeapons.noviceWand.item())
                .pattern(" C")
                .pattern("S ")
                .input('C', Items.COAL)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.COAL), this.conditionsFromItem(Items.COAL))
                .offerTo(this.exporter);

        // Arcane Wand - amethyst shard + gold ingot
        this.createShaped(RecipeCategory.COMBAT, WizardWeapons.arcaneWand.item())
                .pattern(" A")
                .pattern("G ")
                .input('A', Items.AMETHYST_SHARD)
                .input('G', Items.GOLD_INGOT)
                .criterion(hasItem(Items.AMETHYST_SHARD), this.conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(this.exporter);

        // Fire Wand - gunpowder + gold ingot
        this.createShaped(RecipeCategory.COMBAT, WizardWeapons.fireWand.item())
                .pattern(" A")
                .pattern("G ")
                .input('A', Items.GUNPOWDER)
                .input('G', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GUNPOWDER), this.conditionsFromItem(Items.GUNPOWDER))
                .offerTo(this.exporter);

        // Frost Wand - snowball + iron ingot
        this.createShaped(RecipeCategory.COMBAT, WizardWeapons.frostWand.item())
                .pattern(" S")
                .pattern("I ")
                .input('S', Items.SNOWBALL)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(Items.SNOWBALL), this.conditionsFromItem(Items.SNOWBALL))
                .offerTo(this.exporter);
    }

    // ========================================
    // STAFF RECIPES
    // ========================================

    private void generateStaffRecipes() {
        // Wizard Staff - quartz + stick
        this.createShaped(RecipeCategory.COMBAT, WizardWeapons.wizardStaff.item())
                .pattern("  Q")
                .pattern(" S ")
                .pattern("S  ")
                .input('Q', Items.QUARTZ)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.QUARTZ), this.conditionsFromItem(Items.QUARTZ))
                .offerTo(this.exporter);

        // Arcane Staff - amethyst shard + ender pearl + gold ingot + stick
        this.createShaped(RecipeCategory.COMBAT, WizardWeapons.arcaneStaff.item())
                .pattern(" AP")
                .pattern(" SA")
                .pattern("G  ")
                .input('P', Items.ENDER_PEARL)
                .input('A', Items.AMETHYST_SHARD)
                .input('G', Items.GOLD_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.AMETHYST_SHARD), this.conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(this.exporter);

        // Fire Staff - blaze powder + nether brick + gold ingot + stick
        this.createShaped(RecipeCategory.COMBAT, WizardWeapons.fireStaff.item())
                .pattern(" NP")
                .pattern(" SN")
                .pattern("G  ")
                .input('P', Items.BLAZE_POWDER)
                .input('N', Items.NETHER_BRICK)
                .input('G', Items.GOLD_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.BLAZE_POWDER), this.conditionsFromItem(Items.BLAZE_POWDER))
                .offerTo(this.exporter);

        // Frost Staff - prismarine crystals + snowball + iron ingot + stick
        this.createShaped(RecipeCategory.COMBAT, WizardWeapons.frostStaff.item())
                .pattern(" BP")
                .pattern(" SB")
                .pattern("I  ")
                .input('P', Items.PRISMARINE_CRYSTALS)
                .input('B', Items.SNOWBALL)
                .input('I', Items.IRON_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.PRISMARINE_CRYSTALS), this.conditionsFromItem(Items.PRISMARINE_CRYSTALS))
                .offerTo(this.exporter);

        // Note: Conditional staves (ruby_fire, smaragdant_frost, crystal_arcane)
        // are kept as hand-written JSONs with mod load conditions
    }

    // ========================================
    // ARMOR RECIPES
    // ========================================

    private void generateArmorRecipes() {
        // Wizard Robes - wool + lapis lazuli
        generateArmorSet(WizardArmors.wizardRobeSet, Items.LAPIS_LAZULI);

        // Arcane Robes - wool + ender pearl
        generateArmorSet(WizardArmors.arcaneRobeSet, Items.ENDER_PEARL);

        // Fire Robes - wool + blaze powder
        generateArmorSet(WizardArmors.fireRobeSet, Items.BLAZE_POWDER);

        // Frost Robes - wool + prismarine shard
        generateArmorSet(WizardArmors.frostRobeSet, Items.PRISMARINE_SHARD);
    }

    /**
     * Generate all 4 armor pieces for a set using the standard robe patterns
     */
    private void generateArmorSet(Armor.Set set, Item specialIngredient) {
        // Helmet/Head - pattern: "  W" / " W " / "WLW"
        this.createShaped(RecipeCategory.COMBAT, set.head)
                .pattern("  W")
                .pattern(" W ")
                .pattern("WLW")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), this.conditionsFromItem(specialIngredient))
                .offerTo(this.exporter);

        // Chestplate - pattern: "L L" / "WLW" / "WWW"
        this.createShaped(RecipeCategory.COMBAT, set.chest)
                .pattern("L L")
                .pattern("WLW")
                .pattern("WWW")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), this.conditionsFromItem(specialIngredient))
                .offerTo(this.exporter);

        // Leggings - pattern: "LLL" / "W W" / "W W"
        this.createShaped(RecipeCategory.COMBAT, set.legs)
                .pattern("LLL")
                .pattern("W W")
                .pattern("W W")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), this.conditionsFromItem(specialIngredient))
                .offerTo(this.exporter);

        // Boots - pattern: "L L" / "W W"
        this.createShaped(RecipeCategory.COMBAT, set.feet)
                .pattern("L L")
                .pattern("W W")
                .input('L', specialIngredient)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(specialIngredient), this.conditionsFromItem(specialIngredient))
                .offerTo(this.exporter);
    }

    // ========================================
    // NETHERITE UPGRADE RECIPES
    // ========================================

    private void generateNetheriteUpgrades() {
        // Wand upgrades
        this.offerNetheriteUpgradeRecipe(WizardWeapons.arcaneWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteArcaneWand.item());
        this.offerNetheriteUpgradeRecipe(WizardWeapons.fireWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFireWand.item());
        this.offerNetheriteUpgradeRecipe(WizardWeapons.frostWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFrostWand.item());

        // Staff upgrades
        this.offerNetheriteUpgradeRecipe(WizardWeapons.arcaneStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteArcaneStaff.item());
        this.offerNetheriteUpgradeRecipe(WizardWeapons.fireStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFireStaff.item());
        this.offerNetheriteUpgradeRecipe(WizardWeapons.frostStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFrostStaff.item());

        // Armor upgrades - Arcane set
        this.offerNetheriteUpgradeRecipe(WizardArmors.arcaneRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.head);
        this.offerNetheriteUpgradeRecipe(WizardArmors.arcaneRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.chest);
        this.offerNetheriteUpgradeRecipe(WizardArmors.arcaneRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.legs);
        this.offerNetheriteUpgradeRecipe(WizardArmors.arcaneRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.feet);

        // Armor upgrades - Fire set
        this.offerNetheriteUpgradeRecipe(WizardArmors.fireRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_fire.head);
        this.offerNetheriteUpgradeRecipe(WizardArmors.fireRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_fire.chest);
        this.offerNetheriteUpgradeRecipe(WizardArmors.fireRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_fire.legs);
        this.offerNetheriteUpgradeRecipe(WizardArmors.fireRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_fire.feet);

        // Armor upgrades - Frost set
        this.offerNetheriteUpgradeRecipe(WizardArmors.frostRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_frost.head);
        this.offerNetheriteUpgradeRecipe(WizardArmors.frostRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_frost.chest);
        this.offerNetheriteUpgradeRecipe(WizardArmors.frostRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_frost.legs);
        this.offerNetheriteUpgradeRecipe(WizardArmors.frostRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_frost.feet);
    }

    }
}
