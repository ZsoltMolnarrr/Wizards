package net.wizards.villager;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.rpg_foundation.structure_pool.api.StructurePoolAPI;
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
    public static final Identifier POI_ID = Identifier.fromNamespaceAndPath(WizardsMod.ID, WIZARD_MERCHANT);
    /// 1.21.11 addresses professions by registry key (Fabric's `TradeOfferHelper` and NeoForge's
    /// `VillagerTradesEvent#getType` both take/return one).
    public static final ResourceKey<VillagerProfession> PROFESSION_KEY =
            ResourceKey.create(Registries.VILLAGER_PROFESSION, POI_ID);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// The workstation block states for the wizard-merchant POI. Registration itself is loader-specific
    /// (Fabric: `PointOfInterestHelper`; NeoForge: a plain `Registry.register` of a `PointOfInterestType`,
    /// whose block-state mapping NeoForge wires up via its POI registry callback), so it lives in each
    /// platform's entrypoint — this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(RuneCraftingBlock.INSTANCE.getStateDefinition().getPossibleStates());
    }

    /// The registered wizard-merchant profession, set by {@link #register()}. Read by the loader-specific
    /// trade-offer registration (Fabric `TradeOfferHelper` / NeoForge `VillagerTradesEvent`).
    public static VillagerProfession PROFESSION;

    /// Trade offers per merchant tier (1..5), populated by {@link #register()}. The actual registration
    /// with the game is loader-specific and lives in each platform's entrypoint.
    public static final LinkedHashMap<Integer, List<VillagerTrades.ItemListing>> TRADES = new LinkedHashMap<>();

    public static VillagerProfession registerProfession(String name, ResourceKey<PoiType> workStation) {
        var id = Identifier.fromNamespaceAndPath(WizardsMod.ID, name);
        return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Identifier.fromNamespaceAndPath(WizardsMod.ID, name), new VillagerProfession(
                // 1.21.11: the profession's first field is the display Text (vanilla builds
                // `entity.<namespace>.villager.<path>`), not the raw id string.
                Component.translatable("entity." + id.getNamespace() + ".villager." + id.getPath()),
                (entry) -> {
                    return entry.is(workStation);
                },
                (entry) -> {
                    return entry.is(workStation);
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
                ResourceKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), POI_ID));
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
                new VillagerTrades.ItemsForEmeralds(RuneItems.get(RuneItems.RuneType.ARCANE), 2, 8, 128, 3, 0.1f),
                new VillagerTrades.ItemsForEmeralds(RuneItems.get(RuneItems.RuneType.FIRE), 2, 8, 128, 3, 0.1f),
                new VillagerTrades.ItemsForEmeralds(RuneItems.get(RuneItems.RuneType.FROST), 2, 8, 128, 3, 0.1f)
        ));
        TRADES.put(2, List.of(
                new VillagerTrades.ItemsForEmeralds(WizardWeapons.wizardStaff.item(), 4, 1, 12, 18),
                new VillagerTrades.ItemsForEmeralds(WizardWeapons.noviceWand.item(), 4, 1, 12, 18),
                new VillagerTrades.ItemsForEmeralds(WizardWeapons.arcaneWand.item(), 18, 1, 12, 18),
                new VillagerTrades.ItemsForEmeralds(WizardWeapons.fireWand.item(), 18, 1, 12, 18),
                new VillagerTrades.ItemsForEmeralds(WizardWeapons.frostWand.item(), 18, 1, 12, 18),

                new VillagerTrades.EmeraldForItems(Items.WHITE_WOOL, 10, 12, 5, 6),
                new VillagerTrades.EmeraldForItems(Items.LAPIS_LAZULI, 6, 3, 5, 12)
        ));
        TRADES.put(3, List.of(
                new VillagerTrades.ItemsForEmeralds(WizardArmors.wizardRobeSet.head, 15, 1, 12, 16, 0.1F),
                new VillagerTrades.ItemsForEmeralds(WizardArmors.wizardRobeSet.feet, 15, 1, 12, 16, 0.1F)
        ));
        TRADES.put(4, List.of(
                new VillagerTrades.ItemsForEmeralds(WizardArmors.wizardRobeSet.chest, 20, 1, 12, 16, 0.1F),
                new VillagerTrades.ItemsForEmeralds(WizardArmors.wizardRobeSet.legs, 20, 1, 12, 16, 0.1F)
        ));
        TRADES.put(5, List.of(
                (world, entity, random) -> new VillagerTrades.EnchantedItemForEmeralds(
                        WizardWeapons.arcaneStaff.item(), 40, 3, 30, 0F).getOffer(world, entity, random),
                (world, entity, random) -> new VillagerTrades.EnchantedItemForEmeralds(
                        WizardWeapons.fireStaff.item(), 40, 3, 30, 0F).getOffer(world, entity, random),
                (world, entity, random) -> new VillagerTrades.EnchantedItemForEmeralds(
                        WizardWeapons.frostStaff.item(), 40, 3, 30, 0F).getOffer(world, entity, random)
        ));
    }
}
