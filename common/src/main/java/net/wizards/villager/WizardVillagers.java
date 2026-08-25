package net.wizards.villager;

import com.google.common.collect.ImmutableSet;
import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.runes.api.RuneItems;
import net.runes.crafting.RuneCraftingBlock;
import net.spell_engine.Platform;
import net.wizards.WizardsMod;
import net.wizards.item.WizardArmors;
import net.wizards.item.WizardWeapons;
import net.wizards.content.WizardsSounds;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class WizardVillagers {
    public static final String WIZARD_MERCHANT = "wizard_merchant";
    public static final Identifier POI_ID = Identifier.of(WizardsMod.ID, WIZARD_MERCHANT);
    /// 1.21.11 addresses professions by registry key (Fabric's `TradeOfferHelper` and NeoForge's
    /// `VillagerTradesEvent#getType` both take/return one).
    public static final RegistryKey<VillagerProfession> PROFESSION_KEY =
            RegistryKey.of(RegistryKeys.VILLAGER_PROFESSION, POI_ID);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// The workstation block states for the wizard-merchant POI. Registration itself is loader-specific
    /// (Fabric: `PointOfInterestHelper`; NeoForge: a plain `Registry.register` of a `PointOfInterestType`,
    /// whose block-state mapping NeoForge wires up via its POI registry callback), so it lives in each
    /// platform's entrypoint — this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(RuneCraftingBlock.INSTANCE.getStateManager().getStates());
    }

    /// The registered wizard-merchant profession, set by {@link #register()}. Read by the loader-specific
    /// trade-offer registration (Fabric `TradeOfferHelper` / NeoForge `VillagerTradesEvent`).
    public static VillagerProfession PROFESSION;

    /// Trade offers per merchant tier (1..5), populated by {@link #register()}. The actual registration
    /// with the game is loader-specific and lives in each platform's entrypoint.
    public static final LinkedHashMap<Integer, List<TradeOffers.Factory>> TRADES = new LinkedHashMap<>();

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = Identifier.of(WizardsMod.ID, name);
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(WizardsMod.ID, name), new VillagerProfession(
                // 1.21.11: the profession's first field is the display Text (vanilla builds
                // `entity.<namespace>.villager.<path>`), not the raw id string.
                Text.translatable("entity." + id.getNamespace() + ".villager." + id.getPath()),
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

//    private static class Offer {
//        int level;
//        ItemStack input;
//        ItemStack output;
//        int maxUses;
//        int experience;
//        float priceMultiplier;
//
//        public Offer(int level, ItemStack input, ItemStack output, int maxUses, int experience, float priceMultiplier) {
//            this.level = level;
//            this.input = input;
//            this.output = output;
//            this.maxUses = maxUses;
//            this.experience = experience;
//            this.priceMultiplier = priceMultiplier;
//        }
//
//        public static Offer buy(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
//            return new Offer(level, item, new ItemStack(Items.EMERALD, price), maxUses, experience, priceMultiplier);
//        }
//
//        public static Offer sell(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
//            return new Offer(level, new ItemStack(Items.EMERALD, price), item, maxUses, experience, priceMultiplier);
//        }
//    }

    public static void register() {
        if (!Platform.util().isModLoaded("lithostitched")) {
            // Only inject the village if the Lithostitched is not present
            StructurePoolAPI.injectAll(WizardsMod.villageConfig.value);
        }
        PROFESSION = registerProfession(
                WIZARD_MERCHANT,
                RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), POI_ID));
//        List<Offer> wizardMerchantOffers = List.of(
//                Offer.sell(1, new ItemStack(RuneItems.get(RuneItems.RuneType.ARCANE), 8), 2, 128, 1, 0.01f),
//                Offer.sell(1, new ItemStack(RuneItems.get(RuneItems.RuneType.FIRE), 8), 2, 128, 1, 0.01f),
//                Offer.sell(1, new ItemStack(RuneItems.get(RuneItems.RuneType.FROST), 8), 2, 128, 1, 0.01f),
//                Offer.sell(2, Weapons.wizardStaff.item().getDefaultStack(), 4, 12, 5, 0.1f),
//                Offer.sell(2, Weapons.noviceWand.item().getDefaultStack(), 4, 12, 5, 0.1f),
//                Offer.sell(2, Weapons.arcaneWand.item().getDefaultStack(), 18, 12, 8, 0.1f),
//                Offer.sell(2, Weapons.fireWand.item().getDefaultStack(), 18, 12, 8, 0.1f),
//                Offer.sell(2, Weapons.frostWand.item().getDefaultStack(), 18, 12, 8, 0.1f),
//                Offer.buy(2, new ItemStack(Items.WHITE_WOOL, 5), 8, 12, 10, 0.05f),
//                Offer.buy(2, new ItemStack(Items.LAPIS_LAZULI, 6), 12, 3, 10, 0.05f),
//                Offer.sell(3, Armors.wizardRobeSet.head.getDefaultStack(), 15, 12, 13, 0.05f),
//                Offer.sell(3, Armors.wizardRobeSet.feet.getDefaultStack(), 15, 12, 13, 0.05f),
//                Offer.sell(4, Armors.wizardRobeSet.chest.getDefaultStack(), 20, 12, 15, 0.05f),
//                Offer.sell(4, Armors.wizardRobeSet.legs.getDefaultStack(), 20, 12, 15, 0.05f)
//            );

        TRADES.clear();
        TRADES.put(1, List.of(
                new TradeOffers.SellItemFactory(RuneItems.get(RuneItems.RuneType.ARCANE), 2, 8, 128, 3, 0.1f),
                new TradeOffers.SellItemFactory(RuneItems.get(RuneItems.RuneType.FIRE), 2, 8, 128, 3, 0.1f),
                new TradeOffers.SellItemFactory(RuneItems.get(RuneItems.RuneType.FROST), 2, 8, 128, 3, 0.1f)
        ));
        TRADES.put(2, List.of(
                new TradeOffers.SellItemFactory(WizardWeapons.wizardStaff.item(), 4, 1, 12, 18),
                new TradeOffers.SellItemFactory(WizardWeapons.noviceWand.item(), 4, 1, 12, 18),
                new TradeOffers.SellItemFactory(WizardWeapons.arcaneWand.item(), 18, 1, 12, 18),
                new TradeOffers.SellItemFactory(WizardWeapons.fireWand.item(), 18, 1, 12, 18),
                new TradeOffers.SellItemFactory(WizardWeapons.frostWand.item(), 18, 1, 12, 18),

                new TradeOffers.BuyItemFactory(Items.WHITE_WOOL, 10, 12, 5, 6),
                new TradeOffers.BuyItemFactory(Items.LAPIS_LAZULI, 6, 3, 5, 12)
        ));
        TRADES.put(3, List.of(
                new TradeOffers.SellItemFactory(WizardArmors.wizardRobeSet.head, 15, 1, 12, 16, 0.1F),
                new TradeOffers.SellItemFactory(WizardArmors.wizardRobeSet.feet, 15, 1, 12, 16, 0.1F)
        ));
        TRADES.put(4, List.of(
                new TradeOffers.SellItemFactory(WizardArmors.wizardRobeSet.chest, 20, 1, 12, 16, 0.1F),
                new TradeOffers.SellItemFactory(WizardArmors.wizardRobeSet.legs, 20, 1, 12, 16, 0.1F)
        ));
        TRADES.put(5, List.of(
                (world, entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WizardWeapons.arcaneStaff.item(), 40, 3, 30, 0F).create(world, entity, random),
                (world, entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WizardWeapons.fireStaff.item(), 40, 3, 30, 0F).create(world, entity, random),
                (world, entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WizardWeapons.frostStaff.item(), 40, 3, 30, 0F).create(world, entity, random)
        ));
    }
}
