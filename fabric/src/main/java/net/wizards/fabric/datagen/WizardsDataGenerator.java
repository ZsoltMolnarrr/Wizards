package net.wizards.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.spell_engine.api.datagen.SimpleSoundGeneratorV2;
import net.spell_engine.api.datagen.SpellGenerator;
import net.spell_engine.api.datagen.WeaponAttributeGenerator;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.api.tags.SpellTags;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;
import net.wizards.WizardsMod;
import net.wizards.content.WizardSpells;
import net.wizards.content.WizardsSounds;
import net.wizards.item.WizardArmors;
import net.wizards.item.WizardWeapons;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WizardsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(SoundGen::new);
        pack.addProvider(SpellGen::new);
        pack.addProvider(SpellTagGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(UnsmeltGenerator::new);
        pack.addProvider(WizardRecipes::new);
        pack.addProvider(WeaponGen::new);
    }

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            generateWeaponTags(WizardWeapons.entries);
            generateArmorTags(WizardArmors.entries, RPGSeriesItemTags.ArmorMetaType.MAGIC);
        }
    }

    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSpells(Builder builder) {
            for (var entry: WizardSpells.entries) {
                builder.add(entry.id(), entry.spell());
            }
        }
    }

    public static class SpellTagGenerator extends FabricTagProvider<Spell> {
        public SpellTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, SpellRegistry.KEY, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            var namespace = WizardsMod.ID;
            var treasureTagBuilder = getOrCreateTagBuilder(SpellTags.TREASURE);
            var processedBooks = new HashSet<WizardSpells.Book>();
            WizardSpells.entries.forEach(entry -> {
                if (entry.book() != null) {
                    var bookTagKey = SpellTags.spellBook(namespace, entry.book().toString().toLowerCase());
                    var bookTag = getOrCreateTagBuilder(bookTagKey);
                    bookTag.addOptional(entry.id());
                    var scrollTagKey = SpellTags.spellScroll(namespace, entry.book().toString().toLowerCase());
                    var scrollTag = getOrCreateTagBuilder(scrollTagKey);
                    scrollTag.addOptional(entry.id());
                    if (processedBooks.add(entry.book())) {
                        treasureTagBuilder.addOptionalTag(scrollTagKey);
                    }
                }
                for (var group : entry.weaponGroups()) {
                    var weaponGroupTagKey = SpellTags.weapon(namespace, group.toString().toLowerCase());
                    var weaponGroupTag = getOrCreateTagBuilder(weaponGroupTagKey);
                    weaponGroupTag.addOptional(entry.id());
                }
            });
        }
    }

    public static class SoundGen extends SimpleSoundGeneratorV2 {
        public SoundGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSounds(Builder builder) {
            builder.entries.add(new Entry(WizardsMod.ID,
                    WizardsSounds.entries.stream()
                            .map(entry -> SoundEntry.withVariants(entry.id().getPath(), entry.variants()))
                            .toList()
                    )
            );
        }
    }

    public static class UnsmeltGenerator extends FabricRecipeProvider {
        public UnsmeltGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        public static int UNSMELT_TIME = 300;

        @Override
        public void generate(RecipeExporter exporter) {
            disassembleArmor(exporter, WizardArmors.wizardRobeSet, Items.LAPIS_LAZULI);
            disassembleArmor(exporter, WizardArmors.arcaneRobeSet, Items.ENDER_PEARL);
            disassembleArmor(exporter, WizardArmors.fireRobeSet, Items.BLAZE_POWDER);
            disassembleArmor(exporter, WizardArmors.frostRobeSet, Items.PRISMARINE_SHARD);
            disassembleArmor(exporter, WizardArmors.netherite_arcane, Items.NETHERITE_SCRAP);
            disassembleArmor(exporter, WizardArmors.netherite_fire, Items.NETHERITE_SCRAP);
            disassembleArmor(exporter, WizardArmors.netherite_frost, Items.NETHERITE_SCRAP);

            disassemble(exporter,
                    List.of(WizardWeapons.arcaneWand.item(), WizardWeapons.fireWand.item()),
                    Items.GOLD_NUGGET);
            disassemble(exporter,
                    List.of(WizardWeapons.frostWand.item()),
                    Items.IRON_NUGGET);

            disassemble(exporter,
                    List.of(WizardWeapons.arcaneStaff.item()),
                    Items.AMETHYST_SHARD);
            disassemble(exporter,
                    List.of(WizardWeapons.fireStaff.item()),
                    Items.BLAZE_POWDER);
            disassemble(exporter,
                    List.of(WizardWeapons.frostStaff.item()),
                    Items.PRISMARINE_CRYSTALS);

            disassemble(exporter,
                    WizardWeapons.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("netherite"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.NETHERITE_SCRAP);
        }

        private static void disassembleArmor(RecipeExporter exporter, Armor.Set armorSet, Item output) {
            FabricRecipeProvider.offerSmelting(exporter,
                    armorSet.pieces(),
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME,
                    "disassemble"
            );
            FabricRecipeProvider.offerBlasting(exporter,
                    armorSet.pieces(),
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME / 2,
                    "disassemble"
            );
        }

        private static void disassemble(RecipeExporter exporter, List<ItemConvertible> items, Item output) {
            FabricRecipeProvider.offerSmelting(exporter,
                    items,
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME,
                    "disassemble"
            );
            FabricRecipeProvider.offerBlasting(exporter,
                    items,
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME / 2,
                    "disassemble"
            );
        }
    }

    public static class WeaponGen extends WeaponAttributeGenerator {
        public WeaponGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateWeaponAttributes(Builder builder) {
            WizardWeapons.entries.forEach(entry -> {
                if (entry.weaponAttributesPreset != null && !entry.weaponAttributesPreset.isEmpty()) {
                    builder.entries.add(new Entry(entry.id(), entry.weaponAttributesPreset));
                }
            });
        }
    }
}
