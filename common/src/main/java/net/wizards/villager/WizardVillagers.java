package net.wizards.villager;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
    /// (Fabric: `PointOfInterestHelper`; Forge: a plain `Registry.register` of a `PointOfInterestType`,
    /// whose block-state mapping Forge wires up via its POI registry callback), so it lives in each
    /// platform's entrypoint — this only exposes the shared state set.
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

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = new Identifier(WizardsMod.ID, name);
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(WizardsMod.ID, name), new VillagerProfession(
                id.toString(),
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent())
        );
    }

    /// Sells a Runes item, resolved lazily so registration order (and Runes' presence) does not matter:
    /// a `TradeOffers.Factory` may return null, which vanilla treats as "no offer".
    private static TradeOffers.Factory sellRune(Identifier runeId, int price, int count, int maxUses, int experience) {
        return (entity, random) -> {
            var item = Registries.ITEM.get(runeId);
            if (item == Items.AIR) {
                return null;
            }
            // 1.20.1 has no `SellItemFactory(Item, …, float)` overload — only the ItemStack one.
            return new TradeOffers.SellItemFactory(new ItemStack(item), price, count, maxUses, experience, 0.1f).create(entity, random);
        };
    }

    /// 1.20.1 only ships `BuyForOneEmeraldFactory` (always 1 emerald); the 1.21 `BuyItemFactory`
    /// (item, count, maxUses, experience, emeraldAmount) is rebuilt here on the raw `TradeOffer` ctor.
    private static TradeOffers.Factory buyForEmeralds(Item item, int count, int maxUses, int experience, int emeralds) {
        return (entity, random) -> new TradeOffer(
                new ItemStack(item, count), new ItemStack(Items.EMERALD, emeralds), maxUses, experience, 0.05F);
    }

    public static void register() {
        PROFESSION = registerProfession(
                WIZARD_MERCHANT,
                RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), POI_ID));

        TRADES.clear();
        TRADES.put(1, List.of(
                sellRune(ARCANE_RUNE_ID, 2, 8, 128, 3),
                sellRune(FIRE_RUNE_ID, 2, 8, 128, 3),
                sellRune(FROST_RUNE_ID, 2, 8, 128, 3)
        ));
        TRADES.put(2, List.of(
                new TradeOffers.SellItemFactory(WizardWeapons.wizardStaff.item(), 4, 1, 12, 18),
                new TradeOffers.SellItemFactory(WizardWeapons.noviceWand.item(), 4, 1, 12, 18),
                new TradeOffers.SellItemFactory(WizardWeapons.arcaneWand.item(), 18, 1, 12, 18),
                new TradeOffers.SellItemFactory(WizardWeapons.fireWand.item(), 18, 1, 12, 18),
                new TradeOffers.SellItemFactory(WizardWeapons.frostWand.item(), 18, 1, 12, 18),

                buyForEmeralds(Items.WHITE_WOOL, 10, 12, 5, 6),
                buyForEmeralds(Items.LAPIS_LAZULI, 6, 3, 5, 12)
        ));
        TRADES.put(3, List.of(
                new TradeOffers.SellItemFactory(new ItemStack(WizardArmors.wizardRobeSet.head), 15, 1, 12, 16, 0.1F),
                new TradeOffers.SellItemFactory(new ItemStack(WizardArmors.wizardRobeSet.feet), 15, 1, 12, 16, 0.1F)
        ));
        TRADES.put(4, List.of(
                new TradeOffers.SellItemFactory(new ItemStack(WizardArmors.wizardRobeSet.chest), 20, 1, 12, 16, 0.1F),
                new TradeOffers.SellItemFactory(new ItemStack(WizardArmors.wizardRobeSet.legs), 20, 1, 12, 16, 0.1F)
        ));
        TRADES.put(5, List.of(
                (entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WizardWeapons.arcaneStaff.item(), 40, 3, 30, 0F).create(entity, random),
                (entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WizardWeapons.fireStaff.item(), 40, 3, 30, 0F).create(entity, random),
                (entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WizardWeapons.frostStaff.item(), 40, 3, 30, 0F).create(entity, random)
        ));
    }
}
