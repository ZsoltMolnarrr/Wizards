package net.wizards.villager;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.state.BlockState;
import net.rpg_foundation.structure_pool.api.StructurePoolAPI;
import net.runes.crafting.RuneCraftingBlock;
import net.spell_engine.Platform;
import net.wizards.WizardsMod;
import net.wizards.content.WizardsSounds;

import java.util.Set;

public class WizardVillagers {
    public static final String WIZARD_MERCHANT = "wizard_merchant";
    public static final Identifier POI_ID = Identifier.fromNamespaceAndPath(WizardsMod.ID, WIZARD_MERCHANT);
    /// 1.21.11 addresses professions by registry key.
    public static final ResourceKey<VillagerProfession> PROFESSION_KEY =
            ResourceKey.create(Registries.VILLAGER_PROFESSION, POI_ID);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// The workstation block states for the wizard-merchant POI. Registration itself is loader-specific
    /// (Fabric: `PoiHelper`; NeoForge: a plain `Registry.register` of a `PointOfInterestType`,
    /// whose block-state mapping NeoForge wires up via its POI registry callback), so it lives in each
    /// platform's entrypoint — this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(RuneCraftingBlock.INSTANCE.getStateDefinition().getPossibleStates());
    }

    /// The registered wizard-merchant profession, set by {@link #register()}.
    public static VillagerProfession PROFESSION;

    /// 26.1 made villager trades data driven: the offers themselves live in
    /// `data/wizards/villager_trade/wizard_merchant/<level>/*.json`, are grouped by
    /// `data/wizards/tags/villager_trade/wizard_merchant/level_<n>.json` and picked up by
    /// `data/wizards/trade_set/wizard_merchant/level_<n>.json`. The profession only refers to the
    /// trade-set keys per merchant level — `VillagerTrades.ItemListing`, Fabric's `TradeOfferHelper`
    /// and NeoForge's `VillagerTradesEvent` are all gone.
    public static ResourceKey<TradeSet> tradeSet(int level) {
        return ResourceKey.create(Registries.TRADE_SET,
                Identifier.fromNamespaceAndPath(WizardsMod.ID, WIZARD_MERCHANT + "/level_" + level));
    }

    public static Int2ObjectMap<ResourceKey<TradeSet>> tradeSetsByLevel() {
        return Int2ObjectMap.ofEntries(
                Int2ObjectMap.entry(1, tradeSet(1)),
                Int2ObjectMap.entry(2, tradeSet(2)),
                Int2ObjectMap.entry(3, tradeSet(3)),
                Int2ObjectMap.entry(4, tradeSet(4)),
                Int2ObjectMap.entry(5, tradeSet(5))
        );
    }

    public static VillagerProfession registerProfession(String name, ResourceKey<PoiType> workStation) {
        var id = Identifier.fromNamespaceAndPath(WizardsMod.ID, name);
        return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, id, new VillagerProfession(
                // 1.21.11: the profession's first field is the display Text (vanilla builds
                // `entity.<namespace>.villager.<path>`), not the raw id string.
                Component.translatable("entity." + id.getNamespace() + ".villager." + id.getPath()),
                (entry) -> entry.is(workStation),
                (entry) -> entry.is(workStation),
                ImmutableSet.of(),
                ImmutableSet.of(),
                WizardsSounds.WIZARD_ROBES_EQUIP.soundEvent(),
                tradeSetsByLevel())
        );
    }

    public static void register() {
        if (!Platform.util().isModLoaded("lithostitched")) {
            // Only inject the village if the Lithostitched is not present
            StructurePoolAPI.injectAll(WizardsMod.villageConfig.value);
        }
        PROFESSION = registerProfession(
                WIZARD_MERCHANT,
                ResourceKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), POI_ID));
    }
}
