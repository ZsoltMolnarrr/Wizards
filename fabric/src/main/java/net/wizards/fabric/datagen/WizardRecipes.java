package net.wizards.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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

    public WizardRecipes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    /// 1.21.2+ splits the provider from the generator: the provider is registered with the pack,
    /// the generator holds the exporter + the item registry lookup that every builder now needs.
    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
        return new Generator(registries, exporter);
    }

    @Override
    public String getName() {
        return "Wizard Crafting Recipes";
    }

    private static class Generator extends RecipeProvider {
        Generator(HolderLookup.Provider registries, RecipeOutput exporter) {
            super(registries, exporter);
        }

    @Override
    public void buildRecipes() {
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
        this.shaped(RecipeCategory.COMBAT, WizardWeapons.noviceWand.item())
                .pattern(" C")
                .pattern("S ")
                .define('C', Items.COAL)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(Items.COAL), this.has(Items.COAL))
                .save(this.output);

        // Arcane Wand - amethyst shard + gold ingot
        this.shaped(RecipeCategory.COMBAT, WizardWeapons.arcaneWand.item())
                .pattern(" A")
                .pattern("G ")
                .define('A', Items.AMETHYST_SHARD)
                .define('G', Items.GOLD_INGOT)
                .unlockedBy(getHasName(Items.AMETHYST_SHARD), this.has(Items.AMETHYST_SHARD))
                .save(this.output);

        // Fire Wand - gunpowder + gold ingot
        this.shaped(RecipeCategory.COMBAT, WizardWeapons.fireWand.item())
                .pattern(" A")
                .pattern("G ")
                .define('A', Items.GUNPOWDER)
                .define('G', Items.GOLD_INGOT)
                .unlockedBy(getHasName(Items.GUNPOWDER), this.has(Items.GUNPOWDER))
                .save(this.output);

        // Frost Wand - snowball + iron ingot
        this.shaped(RecipeCategory.COMBAT, WizardWeapons.frostWand.item())
                .pattern(" S")
                .pattern("I ")
                .define('S', Items.SNOWBALL)
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.SNOWBALL), this.has(Items.SNOWBALL))
                .save(this.output);
    }

    // ========================================
    // STAFF RECIPES
    // ========================================

    private void generateStaffRecipes() {
        // Wizard Staff - quartz + stick
        this.shaped(RecipeCategory.COMBAT, WizardWeapons.wizardStaff.item())
                .pattern("  Q")
                .pattern(" S ")
                .pattern("S  ")
                .define('Q', Items.QUARTZ)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(Items.QUARTZ), this.has(Items.QUARTZ))
                .save(this.output);

        // Arcane Staff - amethyst shard + ender pearl + gold ingot + stick
        this.shaped(RecipeCategory.COMBAT, WizardWeapons.arcaneStaff.item())
                .pattern(" AP")
                .pattern(" SA")
                .pattern("G  ")
                .define('P', Items.ENDER_PEARL)
                .define('A', Items.AMETHYST_SHARD)
                .define('G', Items.GOLD_INGOT)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(Items.AMETHYST_SHARD), this.has(Items.AMETHYST_SHARD))
                .save(this.output);

        // Fire Staff - blaze powder + nether brick + gold ingot + stick
        this.shaped(RecipeCategory.COMBAT, WizardWeapons.fireStaff.item())
                .pattern(" NP")
                .pattern(" SN")
                .pattern("G  ")
                .define('P', Items.BLAZE_POWDER)
                .define('N', Items.NETHER_BRICK)
                .define('G', Items.GOLD_INGOT)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(Items.BLAZE_POWDER), this.has(Items.BLAZE_POWDER))
                .save(this.output);

        // Frost Staff - prismarine crystals + snowball + iron ingot + stick
        this.shaped(RecipeCategory.COMBAT, WizardWeapons.frostStaff.item())
                .pattern(" BP")
                .pattern(" SB")
                .pattern("I  ")
                .define('P', Items.PRISMARINE_CRYSTALS)
                .define('B', Items.SNOWBALL)
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(Items.PRISMARINE_CRYSTALS), this.has(Items.PRISMARINE_CRYSTALS))
                .save(this.output);

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
        this.shaped(RecipeCategory.COMBAT, set.head)
                .pattern("  W")
                .pattern(" W ")
                .pattern("WLW")
                .define('L', specialIngredient)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(specialIngredient), this.has(specialIngredient))
                .save(this.output);

        // Chestplate - pattern: "L L" / "WLW" / "WWW"
        this.shaped(RecipeCategory.COMBAT, set.chest)
                .pattern("L L")
                .pattern("WLW")
                .pattern("WWW")
                .define('L', specialIngredient)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(specialIngredient), this.has(specialIngredient))
                .save(this.output);

        // Leggings - pattern: "LLL" / "W W" / "W W"
        this.shaped(RecipeCategory.COMBAT, set.legs)
                .pattern("LLL")
                .pattern("W W")
                .pattern("W W")
                .define('L', specialIngredient)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(specialIngredient), this.has(specialIngredient))
                .save(this.output);

        // Boots - pattern: "L L" / "W W"
        this.shaped(RecipeCategory.COMBAT, set.feet)
                .pattern("L L")
                .pattern("W W")
                .define('L', specialIngredient)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(specialIngredient), this.has(specialIngredient))
                .save(this.output);
    }

    // ========================================
    // NETHERITE UPGRADE RECIPES
    // ========================================

    private void generateNetheriteUpgrades() {
        // Wand upgrades
        this.netheriteSmithing(WizardWeapons.arcaneWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteArcaneWand.item());
        this.netheriteSmithing(WizardWeapons.fireWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFireWand.item());
        this.netheriteSmithing(WizardWeapons.frostWand.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFrostWand.item());

        // Staff upgrades
        this.netheriteSmithing(WizardWeapons.arcaneStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteArcaneStaff.item());
        this.netheriteSmithing(WizardWeapons.fireStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFireStaff.item());
        this.netheriteSmithing(WizardWeapons.frostStaff.item(), RecipeCategory.COMBAT, WizardWeapons.netheriteFrostStaff.item());

        // Armor upgrades - Arcane set
        this.netheriteSmithing(WizardArmors.arcaneRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.head);
        this.netheriteSmithing(WizardArmors.arcaneRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.chest);
        this.netheriteSmithing(WizardArmors.arcaneRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.legs);
        this.netheriteSmithing(WizardArmors.arcaneRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_arcane.feet);

        // Armor upgrades - Fire set
        this.netheriteSmithing(WizardArmors.fireRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_fire.head);
        this.netheriteSmithing(WizardArmors.fireRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_fire.chest);
        this.netheriteSmithing(WizardArmors.fireRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_fire.legs);
        this.netheriteSmithing(WizardArmors.fireRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_fire.feet);

        // Armor upgrades - Frost set
        this.netheriteSmithing(WizardArmors.frostRobeSet.head, RecipeCategory.COMBAT, WizardArmors.netherite_frost.head);
        this.netheriteSmithing(WizardArmors.frostRobeSet.chest, RecipeCategory.COMBAT, WizardArmors.netherite_frost.chest);
        this.netheriteSmithing(WizardArmors.frostRobeSet.legs, RecipeCategory.COMBAT, WizardArmors.netherite_frost.legs);
        this.netheriteSmithing(WizardArmors.frostRobeSet.feet, RecipeCategory.COMBAT, WizardArmors.netherite_frost.feet);
    }

    }
}
