package net.wizards.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.advancements.criterion.TradeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStackTemplate;
import net.spell_engine.misc.criteria.SpellCastCriteria;
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
 * source of truth: this provider exports each built {@link AdvancementHolder}, and
 * {@code WizardsDataGenerator.LangGen} reads the same list for the
 * {@code advancements.rpg_series.<path>.title/description} translation keys, which are derived from the
 * advancement id (see {@link #translationKey}). Every advancement id therefore matches its translation
 * suffix — e.g. {@code trade_with_wizard} rather than a separate file name and key.
 */
public class WizardAdvancements extends FabricAdvancementProvider {
    public static final String NAMESPACE = "rpg_series";

    public WizardAdvancements(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        for (var entry : entries()) {
            consumer.accept(entry.entry());
        }
    }

    /** A generated advancement paired with the plain-text strings behind its (id-derived) translation keys. */
    public record Entry(AdvancementHolder entry, String title, String description) {
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
    private static Entry task(String idPath, String parent, ItemStackTemplate icon,
                              String criterionName, Criterion<?> criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementType.TASK, true, true, false, criterionName, criterion, title, description);
    }

    /** Challenge-framed task. */
    private static Entry challenge(String idPath, String parent, ItemStackTemplate icon,
                                   String criterionName, Criterion<?> criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementType.CHALLENGE, true, true, false, criterionName, criterion, title, description);
    }

    /** Task without toast or chat announcement (used for the practice/empowerment milestones). */
    private static Entry silent(String idPath, String parent, ItemStackTemplate icon,
                                String criterionName, Criterion<?> criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementType.TASK, false, false, false, criterionName, criterion, title, description);
    }

    /** Hidden task: not shown in the tree until earned. */
    private static Entry secret(String idPath, String parent, ItemStackTemplate icon,
                                String criterionName, Criterion<?> criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementType.TASK, true, true, true, criterionName, criterion, title, description);
    }

    @SuppressWarnings("deprecation") // Advancement.Builder.parent(Identifier) is the only way to reference parents built outside this provider.
    private static Entry advancement(String idPath, String parent, ItemStackTemplate icon, AdvancementType frame,
                                     boolean showToast, boolean announceToChat, boolean hidden,
                                     String criterionName, Criterion<?> criterion, String title, String description) {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, idPath);
        // The original data-pack advancements did not send telemetry events (vanilla default is off),
        // so keep them untelemetered rather than using the telemetered Advancement.Builder.create().
        var entry = Advancement.Builder.recipeAdvancement()
                .parent(Identifier.parse(parent))
                .display(
                        icon,
                        Component.translatable(translationKey(id, "title")),
                        Component.translatable(translationKey(id, "description")),
                        null,
                        frame,
                        showToast,
                        announceToChat,
                        hidden)
                .addCriterion(criterionName, criterion)
                .build(id);
        return new Entry(entry, title, description);
    }

    // MARK: Icon helpers

    /// 26.1: advancement icons are `ItemStackTemplate`s — no `ItemStack` may exist before registries load.
    private static ItemStackTemplate item(String itemId) {
        return new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId)));
    }

    /// 1.21.11: the vanilla `minecraft:item_model` component names an item-model *definition*
    /// (`assets/wizards/items/spell_book/<school>.json`) and Spell Engine sets it to the pool id
    /// verbatim, so the advancement icon must use the same id.
    private static ItemStackTemplate spellBookIcon(String school) {
        return new ItemStackTemplate(
                BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("spell_engine", "spell_book")),
                DataComponentPatch.builder()
                        .set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("wizards", "spell_book/" + school))
                        .build());
    }

    // MARK: Criterion helpers

    private static Criterion<?> spellBookCreation(String spellPool) {
        return SpellBookCreationCriteria.INSTANCE.createCriterion(
                new SpellBookCreationCriteria.Condition(Optional.empty(), Optional.of(spellPool)));
    }

    private static Criterion<?> spellBinding(String spellPool, boolean complete) {
        return SpellBindingCriteria.INSTANCE.createCriterion(
                new SpellBindingCriteria.Condition(Optional.empty(), Optional.of(spellPool), Optional.of(complete)));
    }

    private static Criterion<?> spellCast(String spell) {
        return SpellCastCriteria.INSTANCE.createCriterion(
                new SpellCastCriteria.Condition(Optional.empty(), Optional.of(spell), Optional.empty()));
    }

    private static Criterion<?> wizardRobes() {
        return InventoryChangeTrigger.TriggerInstance.hasItems(
                BuiltInRegistries.ITEM.getValue(Identifier.parse("wizards:wizard_robe_head")),
                BuiltInRegistries.ITEM.getValue(Identifier.parse("wizards:wizard_robe_chest")),
                BuiltInRegistries.ITEM.getValue(Identifier.parse("wizards:wizard_robe_legs")),
                BuiltInRegistries.ITEM.getValue(Identifier.parse("wizards:wizard_robe_feet")));
    }

    private static Criterion<?> villagerTrade(String profession) {
        var villagerData = new CompoundTag();
        villagerData.putString("profession", profession);
        var nbt = new CompoundTag();
        nbt.put("VillagerData", villagerData);
        var villager = EntityPredicate.wrap(
                EntityPredicate.Builder.entity().nbt(new NbtPredicate(nbt)));
        return CriteriaTriggers.TRADE.createCriterion(
                new TradeTrigger.TriggerInstance(Optional.empty(), Optional.of(villager), Optional.empty()));
    }
}
