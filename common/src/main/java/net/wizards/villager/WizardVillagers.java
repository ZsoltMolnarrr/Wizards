package net.wizards.villager;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.wizards.WizardsMod;
import net.wizards.item.WizardArmors;
import net.wizards.item.WizardWeapons;
import net.wizards.content.WizardsSounds;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class WizardVillagers {
    public static final String WIZARD_MERCHANT = "wizard_merchant";
    public static final Identifier PROFESSION_ID = new Identifier(WizardsMod.ID, WIZARD_MERCHANT);
    public static final Identifier POI_ID = new Identifier(WizardsMod.ID, WIZARD_MERCHANT);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// Runes is not on Wizards' compile classpath on the 1.20.1 line (it has no Forge artifact and its
    /// 1.20.1 Fabric build is a legacy release). Everything Wizards needed from it — the rune-crafting
    /// workstation block and the three rune items — is addressed by registry id instead, so the mod
    /// builds and runs with or without Runes installed.
    public static final Identifier RUNE_CRAFTING_BLOCK_ID = new Identifier("runes", "crafting_altar");
    public static final Identifier ARCANE_RUNE_ID = new Identifier("runes", "arcane_stone");
    public static final Identifier FIRE_RUNE_ID = new Identifier("runes", "fire_stone");
    public static final Identifier FROST_RUNE_ID = new Identifier("runes", "frost_stone");

    /// The workstation block states for the wizard-merchant POI. Registration itself is loader-specific
    /// (Fabric: `PointOfInterestHelper`; Forge: a `PointOfInterestType` handed to the `RegisterEvent`
    /// helper, whose block-state mapping Forge wires up via its POI registry callback), so it lives in
    /// each platform's entrypoint — this only exposes the shared state set.
    ///
    /// Empty when Runes is absent: the POI type still registers (so the profession and its advancement
    /// stay valid), it simply has no block that can act as a workstation.
    public static Set<BlockState> poiBlockStates() {
        var block = runeCraftingBlock();
        if (block == null) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf(block.getStateManager().getStates());
    }

    private static Block runeCraftingBlock() {
        var block = Registries.BLOCK.get(RUNE_CRAFTING_BLOCK_ID);
        return block == Blocks.AIR ? null : block;
    }

    /// The registered wizard-merchant profession, set by {@link #register()}. Read by the loader-specific
    /// trade-offer registration (Fabric `TradeOfferHelper` / Forge `VillagerTradesEvent`).
    public static VillagerProfession PROFESSION;

    /// Trade offers per merchant tier (1..5), populated by {@link #register()}. The actual registration
    /// with the game is loader-specific and lives in each platform's entrypoint.
    public static final LinkedHashMap<Integer, List<TradeOffers.Factory>> TRADES = new LinkedHashMap<>();

    public static VillagerProfession createProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = new Identifier(WizardsMod.ID, name);
        return new VillagerProfession(
                id.toString(),
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent());
    }

    private static VillagerProfession professionToRegister;

    /// Builds the wizard-merchant profession once, keyed by {@link #PROFESSION_ID}. Creation only —
    /// nothing is registered here, so a loader that registers the profession itself (Forge, through the
    /// `RegisterEvent` helper) hands this to its own registration API instead of duplicating the
    /// construction.
    public static VillagerProfession professionToRegister() {
        if (professionToRegister == null) {
            professionToRegister = createProfession(
                    WIZARD_MERCHANT,
                    RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), POI_ID));
        }
        return professionToRegister;
    }

    /// Reads {@link #PROFESSION} back out of the registry, for a loader that registered the profession
    /// itself. `VillagerTradesEvent` filtering and the Fabric trade registration both compare against that
    /// field, and Forge's `RegisterEvent` helper returns void, so Forge calls this straight after its
    /// registration loop. Throws naming the id if the profession never landed — which is also what catches
    /// a silently mis-keyed `event.register` block.
    public static void linkProfessionEntry() {
        if (PROFESSION == null) {
            PROFESSION = Registries.VILLAGER_PROFESSION
                    .getOrEmpty(PROFESSION_ID)
                    .orElseThrow(() -> new IllegalStateException(
                            "Villager profession " + PROFESSION_ID + " is not in the registry — register it first"));
        }
    }

    // MARK: Trade offers
    //
    // `TradeOffers.SellItemFactory` and `TradeOffers.SellEnchantedToolFactory` are package-private classes
    // in the real 1.20.1 jar, and stay package-private after Forge's access transformer. They compile here
    // only because a mod on `common`'s classpath contributes an access widener that the production runtime
    // does not have, so calling them crashed the Forge server with
    // `IllegalAccessError: failed to access class net.minecraft.world.entity.npc.VillagerTrades$ItemsForEmeralds`.
    // Both are therefore rebuilt on the raw `TradeOffer` constructor, reproducing vanilla's arithmetic
    // exactly. Same shape as `JewelryVillagers` / `TavernVillagers` (see jewelry-port-notes.md §4).

    private static final float PRICE_MULTIPLIER = 0.05F;

    /// Mirrors `TradeOffers.SellItemFactory(ItemStack, price, count, maxUses, experience, multiplier)`.
    /// Vanilla rebuilds the sold stack as `new ItemStack(sell.getItem(), count)`, which drops NBT;
    /// `copyWithCount` keeps it, as `TavernVillagers` does for its potion offers.
    private static TradeOffers.Factory sell(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
        return (entity, random) -> new TradeOffer(
                new ItemStack(Items.EMERALD, price), stack.copyWithCount(count),
                maxUses, experience, multiplier);
    }

    /// Mirrors `TradeOffers.SellItemFactory(Item, price, count, maxUses, experience)` — the 0.05F default.
    private static TradeOffers.Factory sell(Item item, int price, int count, int maxUses, int experience) {
        return sell(new ItemStack(item), price, count, maxUses, experience, PRICE_MULTIPLIER);
    }

    /// Mirrors `TradeOffers.SellEnchantedToolFactory(Item, basePrice, maxUses, experience, multiplier)`:
    /// `5 + random.nextInt(15)` enchantment levels, no treasure enchantments, and the emerald price capped
    /// at 64.
    private static TradeOffers.Factory sellEnchanted(Item item, int basePrice, int maxUses, int experience, float multiplier) {
        return (entity, random) -> {
            int level = 5 + random.nextInt(15);
            var tool = EnchantmentHelper.enchant(random, new ItemStack(item), level, false);
            int price = Math.min(basePrice + level, 64);
            return new TradeOffer(new ItemStack(Items.EMERALD, price), tool, maxUses, experience, multiplier);
        };
    }

    /// Sells a Runes item, resolved lazily so registration order (and Runes' presence) does not matter:
    /// a `TradeOffers.Factory` may return null, which vanilla treats as "no offer".
    private static TradeOffers.Factory sellRune(Identifier runeId, int price, int count, int maxUses, int experience) {
        return (entity, random) -> {
            var item = Registries.ITEM.get(runeId);
            if (item == Items.AIR) {
                return null;
            }
            return sell(new ItemStack(item), price, count, maxUses, experience, 0.1F).create(entity, random);
        };
    }

    /// 1.20.1 only ships `BuyForOneEmeraldFactory` (always 1 emerald); the 1.21 `BuyItemFactory`
    /// (item, count, maxUses, experience, emeraldAmount) is rebuilt here on the raw `TradeOffer` ctor.
    private static TradeOffers.Factory buyForEmeralds(Item item, int count, int maxUses, int experience, int emeralds) {
        return (entity, random) -> new TradeOffer(
                new ItemStack(item, count), new ItemStack(Items.EMERALD, emeralds), maxUses, experience, PRICE_MULTIPLIER);
    }

    public static void register() {
        PROFESSION = Registry.register(Registries.VILLAGER_PROFESSION, PROFESSION_ID, professionToRegister());
        setupTrades();
    }

    /// Fills {@link #TRADES}. Creation only — the actual trade-offer wiring is loader-specific and lives in
    /// each platform's entrypoint (Fabric `TradeOfferHelper` / Forge `VillagerTradesEvent`).
    public static void setupTrades() {
        TRADES.clear();
        TRADES.put(1, List.of(
                sellRune(ARCANE_RUNE_ID, 2, 8, 128, 3),
                sellRune(FIRE_RUNE_ID, 2, 8, 128, 3),
                sellRune(FROST_RUNE_ID, 2, 8, 128, 3)
        ));
        TRADES.put(2, List.of(
                sell(WizardWeapons.wizardStaff.item(), 4, 1, 12, 18),
                sell(WizardWeapons.noviceWand.item(), 4, 1, 12, 18),
                sell(WizardWeapons.arcaneWand.item(), 18, 1, 12, 18),
                sell(WizardWeapons.fireWand.item(), 18, 1, 12, 18),
                sell(WizardWeapons.frostWand.item(), 18, 1, 12, 18),

                buyForEmeralds(Items.WHITE_WOOL, 10, 12, 5, 6),
                buyForEmeralds(Items.LAPIS_LAZULI, 6, 3, 5, 12)
        ));
        TRADES.put(3, List.of(
                sell(new ItemStack(WizardArmors.wizardRobeSet.head), 15, 1, 12, 16, 0.1F),
                sell(new ItemStack(WizardArmors.wizardRobeSet.feet), 15, 1, 12, 16, 0.1F)
        ));
        TRADES.put(4, List.of(
                sell(new ItemStack(WizardArmors.wizardRobeSet.chest), 20, 1, 12, 16, 0.1F),
                sell(new ItemStack(WizardArmors.wizardRobeSet.legs), 20, 1, 12, 16, 0.1F)
        ));
        TRADES.put(5, List.of(
                sellEnchanted(WizardWeapons.arcaneStaff.item(), 40, 3, 30, 0F),
                sellEnchanted(WizardWeapons.fireStaff.item(), 40, 3, 30, 0F),
                sellEnchanted(WizardWeapons.frostStaff.item(), 40, 3, 30, 0F)
        ));
    }
}
