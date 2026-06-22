package net.wizards.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.SpellDataComponents;
import net.spell_engine.internals.criteria.SpellCastCriteria;
import net.spell_engine.spellbinding.SpellBindingCriteria;
import net.spell_engine.spellbinding.SpellBookCreationCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Generates the Wizard advancement tree using the standard {@link Advancement.Builder} API.
 * <p>
 * The advancements live in the shared {@code rpg_series} namespace (they are part of the RPG Series
 * progression UI) but their content is provided by the Wizards mod. {@link #entries()} is the single
 * source of truth: this provider exports each built {@link AdvancementEntry}, and
 * {@code WizardsDataGenerator.LangGen} reads the same list for the
 * {@code advancements.rpg_series.<path>.title/description} translation keys, which are derived from the
 * advancement id (see {@link #translationKey}). Every advancement id therefore matches its translation
 * suffix — e.g. {@code trade_with_wizard} rather than a separate file name and key.
 */
public class WizardAdvancements extends FabricAdvancementProvider {
    public static final String NAMESPACE = "rpg_series";

    public WizardAdvancements(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        for (var entry : entries()) {
            consumer.accept(entry.entry());
        }
    }

    /** A generated advancement paired with the plain-text strings behind its (id-derived) translation keys. */
    public record Entry(AdvancementEntry entry, String title, String description) {
        public String titleKey() { return translationKey(entry.id(), "title"); }
        public String descriptionKey() { return translationKey(entry.id(), "description"); }
    }

    static String translationKey(Identifier id, String suffix) {
        return "advancements." + id.getNamespace() + "." + id.getPath() + "." + suffix;
    }

    // MARK: Definitions

    private static List<Entry> entries;

    /** Lazily builds the advancement tree on first use (during data generation, when registries are ready). */
    public static List<Entry> entries() {
        if (entries != null) {
            return entries;
        }
        var list = new ArrayList<Entry>();

        // Class choice — create a spell book
        list.add(task("path_choose_arcane", "rpg_series:classes", spellBookIcon("arcane"),
                "book", spellBookCreation("wizards:spell_book/arcane"),
                "Path of Arcane", "Create a Tome of Arcane"));
        list.add(task("path_choose_fire", "rpg_series:classes", spellBookIcon("fire"),
                "book", spellBookCreation("wizards:spell_book/fire"),
                "Path of Fire", "Create a Tome of Fire"));
        list.add(task("path_choose_frost", "rpg_series:classes", spellBookIcon("frost"),
                "book", spellBookCreation("wizards:spell_book/frost"),
                "Path of Frost", "Create a Tome of Frost"));

        // Obtain the first spell of a school — bind a single spell
        list.add(task("spell_novice_arcane", "rpg_series:path_choose_arcane", item("wizards:staff_arcane"),
                "bind", spellBinding("wizards:spell_book/arcane", false),
                "Magic Attunement", "Obtain your first Arcane Wizard Spell"));
        list.add(task("spell_novice_fire", "rpg_series:path_choose_fire", item("wizards:staff_fire"),
                "bind", spellBinding("wizards:spell_book/fire", false),
                "Playing with fire", "Obtain your first Fire Wizard Spell"));
        list.add(task("spell_novice_frost", "rpg_series:path_choose_frost", item("wizards:staff_frost"),
                "bind", spellBinding("wizards:spell_book/frost", false),
                "Keep it cool", "Obtain your first Frost Wizard Spell"));

        // Obtain all spells of a school — complete binding
        list.add(challenge("spell_master_arcane", "rpg_series:spell_novice_arcane", item("wizards:staff_netherite_arcane"),
                "bind", spellBinding("wizards:spell_book/arcane", true),
                "Mind Mastery", "Obtain all Arcane Wizard Spells"));
        list.add(challenge("spell_master_fire", "rpg_series:spell_novice_fire", item("wizards:staff_netherite_fire"),
                "bind", spellBinding("wizards:spell_book/fire", true),
                "Pyromaniac", "Obtain all Fire Wizard Spells"));
        list.add(challenge("spell_master_frost", "rpg_series:spell_novice_frost", item("wizards:staff_netherite_frost"),
                "bind", spellBinding("wizards:spell_book/frost", true),
                "Icy Veins", "Obtain all Frost Wizard Spells"));

        // Cast a spell with a wand (silent — no toast or chat announcement)
        list.add(silent("spell_cast_arcane_wand", "rpg_series:path_choose_arcane", item("wizards:wand_arcane"),
                "cast", spellCast("wizards:arcane_bolt"),
                "Arcane Practice", "Cast a spell with an Arcane Wand"));
        list.add(silent("spell_cast_fire_wand", "rpg_series:path_choose_fire", item("wizards:wand_fire"),
                "cast", spellCast("wizards:fireball"),
                "Fiery Practice", "Cast a spell with a Fire Wand"));
        list.add(silent("spell_cast_frost_wand", "rpg_series:path_choose_frost", item("wizards:wand_frost"),
                "cast", spellCast("wizards:frost_shard"),
                "Frosty Practice", "Cast a spell with a Frost Wand"));

        // Cast a spell from a spell book (matches any spell in the book's tag)
        list.add(silent("spell_cast_arcane_book", "rpg_series:spell_novice_arcane", spellBookIcon("arcane"),
                "cast", spellCast("#wizards:spell_book/arcane"),
                "Arcane Empowerment", "Cast a spell from the Arcane Tome"));
        list.add(silent("spell_cast_fire_book", "rpg_series:spell_novice_fire", spellBookIcon("fire"),
                "cast", spellCast("#wizards:spell_book/fire"),
                "Fire Empowerment", "Cast a spell from the Fire Tome"));
        list.add(silent("spell_cast_frost_book", "rpg_series:spell_novice_frost", spellBookIcon("frost"),
                "cast", spellCast("#wizards:spell_book/frost"),
                "Frost Empowerment", "Cast a spell from the Frost Tome"));

        // Misc
        list.add(task("obtain_wizard_robes", "rpg_series:misc_items", item("wizards:wizard_robe_chest"),
                "wizard_robes", wizardRobes(),
                "A Wizard Is Never Late", "Acquire a full set of Wizard Robes"));
        list.add(secret("trade_with_wizard", "rpg_series:misc_items", item("runes:fire_stone"),
                "trade_with_wizard", villagerTrade("wizards:wizard_merchant"),
                "Magic Sale", "Trade with a Wizard Merchant villager"));

        entries = list;
        return entries;
    }

    // MARK: Builder shorthands

    /** Visible task: toast + chat announcement on. */
    private static Entry task(String idPath, String parent, ItemStack icon,
                              String criterionName, AdvancementCriterion<?> criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, true, true, false, criterionName, criterion, title, description);
    }

    /** Challenge-framed task. */
    private static Entry challenge(String idPath, String parent, ItemStack icon,
                                   String criterionName, AdvancementCriterion<?> criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.CHALLENGE, true, true, false, criterionName, criterion, title, description);
    }

    /** Task without toast or chat announcement (used for the practice/empowerment milestones). */
    private static Entry silent(String idPath, String parent, ItemStack icon,
                                String criterionName, AdvancementCriterion<?> criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, false, false, false, criterionName, criterion, title, description);
    }

    /** Hidden task: not shown in the tree until earned. */
    private static Entry secret(String idPath, String parent, ItemStack icon,
                                String criterionName, AdvancementCriterion<?> criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, true, true, true, criterionName, criterion, title, description);
    }

    @SuppressWarnings("deprecation") // Advancement.Builder.parent(Identifier) is the only way to reference parents built outside this provider.
    private static Entry advancement(String idPath, String parent, ItemStack icon, AdvancementFrame frame,
                                     boolean showToast, boolean announceToChat, boolean hidden,
                                     String criterionName, AdvancementCriterion<?> criterion, String title, String description) {
        var id = Identifier.of(NAMESPACE, idPath);
        // The original data-pack advancements did not send telemetry events (vanilla default is off),
        // so keep them untelemetered rather than using the telemetered Advancement.Builder.create().
        var entry = Advancement.Builder.createUntelemetered()
                .parent(Identifier.of(parent))
                .display(
                        icon,
                        Text.translatable(translationKey(id, "title")),
                        Text.translatable(translationKey(id, "description")),
                        null,
                        frame,
                        showToast,
                        announceToChat,
                        hidden)
                .criterion(criterionName, criterion)
                .build(id);
        return new Entry(entry, title, description);
    }

    // MARK: Icon helpers

    private static ItemStack item(String itemId) {
        return new ItemStack(Registries.ITEM.get(Identifier.of(itemId)));
    }

    private static ItemStack spellBookIcon(String school) {
        var stack = new ItemStack(Registries.ITEM.get(Identifier.of("spell_engine", "spell_book")));
        stack.set(SpellDataComponents.ITEM_MODEL, Identifier.of("wizards", "item/spell_book/" + school));
        return stack;
    }

    // MARK: Criterion helpers

    private static AdvancementCriterion<?> spellBookCreation(String spellPool) {
        return SpellBookCreationCriteria.INSTANCE.create(
                new SpellBookCreationCriteria.Condition(Optional.empty(), Optional.of(spellPool)));
    }

    private static AdvancementCriterion<?> spellBinding(String spellPool, boolean complete) {
        return SpellBindingCriteria.INSTANCE.create(
                new SpellBindingCriteria.Condition(Optional.empty(), Optional.of(spellPool), Optional.of(complete)));
    }

    private static AdvancementCriterion<?> spellCast(String spell) {
        return SpellCastCriteria.INSTANCE.create(
                new SpellCastCriteria.Condition(Optional.empty(), Optional.of(spell), Optional.empty()));
    }

    private static AdvancementCriterion<?> wizardRobes() {
        return InventoryChangedCriterion.Conditions.items(
                Registries.ITEM.get(Identifier.of("wizards:wizard_robe_head")),
                Registries.ITEM.get(Identifier.of("wizards:wizard_robe_chest")),
                Registries.ITEM.get(Identifier.of("wizards:wizard_robe_legs")),
                Registries.ITEM.get(Identifier.of("wizards:wizard_robe_feet")));
    }

    private static AdvancementCriterion<?> villagerTrade(String profession) {
        var villagerData = new NbtCompound();
        villagerData.putString("profession", profession);
        var nbt = new NbtCompound();
        nbt.put("VillagerData", villagerData);
        var villager = EntityPredicate.contextPredicateFromEntityPredicate(
                EntityPredicate.Builder.create().nbt(new NbtPredicate(nbt)));
        return Criteria.VILLAGER_TRADE.create(
                new VillagerTradeCriterion.Conditions(Optional.empty(), Optional.of(villager), Optional.empty()));
    }
}
