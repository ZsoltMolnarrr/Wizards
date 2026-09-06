package net.wizards.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.SpellItemData;
import net.spell_engine.misc.criteria.SpellCastCriteria;
import net.spell_engine.spellbinding.SpellBindingCriteria;
import net.spell_engine.spellbinding.SpellBookCreationCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    /// 1.20.1 / Fabric API 0.92: `FabricAdvancementProvider` is registry-independent — a 1-arg constructor
    /// and `generateAdvancement(Consumer<Advancement>)` (there is no `AdvancementEntry` wrapper yet).
    public WizardAdvancements(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        for (var entry : entries()) {
            consumer.accept(entry.entry());
        }
    }

    /** A generated advancement paired with the plain-text strings behind its (id-derived) translation keys. */
    public record Entry(Advancement entry, String title, String description) {
        public String titleKey() { return translationKey(entry.getId(), "title"); }
        public String descriptionKey() { return translationKey(entry.getId(), "description"); }
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
        // 1.20.1: Runes is optional (no Forge artifact), so the icon must be an item that always exists —
        // an unresolvable icon id makes the whole advancement fail to load.
        list.add(secret("trade_with_wizard", "rpg_series:misc_items", item("minecraft:emerald"),
                "trade_with_wizard", villagerTrade("wizards:wizard_merchant"),
                "Magic Sale", "Trade with a Wizard Merchant villager"));

        entries = list;
        return entries;
    }

    // MARK: Builder shorthands

    /** Visible task: toast + chat announcement on. */
    private static Entry task(String idPath, String parent, ItemStack icon,
                              String criterionName, CriterionConditions criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, true, true, false, criterionName, criterion, title, description);
    }

    /** Challenge-framed task. */
    private static Entry challenge(String idPath, String parent, ItemStack icon,
                                   String criterionName, CriterionConditions criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.CHALLENGE, true, true, false, criterionName, criterion, title, description);
    }

    /** Task without toast or chat announcement (used for the practice/empowerment milestones). */
    private static Entry silent(String idPath, String parent, ItemStack icon,
                                String criterionName, CriterionConditions criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, false, false, false, criterionName, criterion, title, description);
    }

    /** Hidden task: not shown in the tree until earned. */
    private static Entry secret(String idPath, String parent, ItemStack icon,
                                String criterionName, CriterionConditions criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, true, true, true, criterionName, criterion, title, description);
    }

    /// 1.20.1 `Advancement.Builder#build` refuses to build unless the parent *object* resolves
    /// (`findParent(id -> null)`), so `parent(Identifier)` alone throws "Tried to build incomplete
    /// advancement!". Cross-mod parents (the shared `rpg_series:*` tree) are therefore represented by a
    /// bare stub carrying only the id — `Advancement#createTask` serialises `parent` from that id, so the
    /// emitted JSON is identical to a hand-written `"parent": "rpg_series:..."`.
    private static Advancement parentStub(Identifier parentId) {
        return new Advancement(parentId, null, null, AdvancementRewards.NONE, Map.of(), new String[0][], false);
    }

    private static Entry advancement(String idPath, String parent, ItemStack icon, AdvancementFrame frame,
                                     boolean showToast, boolean announceToChat, boolean hidden,
                                     String criterionName, CriterionConditions criterion, String title, String description) {
        var id = new Identifier(NAMESPACE, idPath);
        // The original data-pack advancements did not send telemetry events (vanilla default is off),
        // so keep them untelemetered rather than using the telemetered Advancement.Builder.create().
        var entry = Advancement.Builder.createUntelemetered()
                .parent(parentStub(new Identifier(parent)))
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
        return new ItemStack(Registries.ITEM.get(new Identifier(itemId)));
    }

    private static ItemStack spellBookIcon(String school) {
        var stack = new ItemStack(Registries.ITEM.get(new Identifier("spell_engine", "spell_book")));
        // 1.20.1: no data components — the custom item model lives in the `spell_engine` NBT sub-compound.
        SpellItemData.setItemModel(stack, new Identifier("wizards", "item/spell_book/" + school));
        return stack;
    }

    // MARK: Criterion helpers

    /// 1.20.1 criteria are plain `AbstractCriterionConditions` instances handed to
    /// `Advancement.Builder#criterion(String, CriterionConditions)` — there is no `Criterion` wrapper.
    private static CriterionConditions spellBookCreation(String spellPool) {
        return new SpellBookCreationCriteria.Condition(Optional.of(spellPool));
    }

    private static CriterionConditions spellBinding(String spellPool, boolean complete) {
        return new SpellBindingCriteria.Condition(Optional.of(spellPool), Optional.of(complete));
    }

    private static CriterionConditions spellCast(String spell) {
        return new SpellCastCriteria.Condition(LootContextPredicate.EMPTY, spell, null);
    }

    private static CriterionConditions wizardRobes() {
        return InventoryChangedCriterion.Conditions.items(
                Registries.ITEM.get(new Identifier("wizards:wizard_robe_head")),
                Registries.ITEM.get(new Identifier("wizards:wizard_robe_chest")),
                Registries.ITEM.get(new Identifier("wizards:wizard_robe_legs")),
                Registries.ITEM.get(new Identifier("wizards:wizard_robe_feet")));
    }

    private static CriterionConditions villagerTrade(String profession) {
        var villagerData = new NbtCompound();
        villagerData.putString("profession", profession);
        var nbt = new NbtCompound();
        nbt.put("VillagerData", villagerData);
        var villager = EntityPredicate.asLootContextPredicate(
                EntityPredicate.Builder.create().nbt(new NbtPredicate(nbt)).build());
        return new VillagerTradeCriterion.Conditions(LootContextPredicate.EMPTY, villager, ItemPredicate.ANY);
    }
}
