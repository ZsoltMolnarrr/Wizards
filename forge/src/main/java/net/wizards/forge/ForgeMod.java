package net.wizards.forge;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;
import net.spell_engine.api.effect.Effects;
import net.spell_engine.rpg_series.item.Armor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import net.wizards.WizardsMod;
import net.wizards.content.WizardsSounds;
import net.wizards.effect.WizardsEffects;
import net.wizards.entity.WizardEntities;
import net.wizards.forge.client.ForgeClientMod;
import net.wizards.item.Group;
import net.wizards.item.WizardArmors;
import net.wizards.item.WizardBooks;
import net.wizards.item.WizardWeapons;
import net.wizards.villager.WizardVillagers;

/// Forge 47 entrypoint (1.20.1 port of the NeoForge entrypoint).
///
/// Forge locks every vanilla registry outside its own `RegisterEvent` window — and on 47.0-47.3 even
/// inside it, for a plain `Registry.register` — so every registration goes through the helper the event
/// hands out, in the window of the registry it writes to. Summoned-entity default attributes are
/// buffered through SpellEngine's `Platform.util().registerSummonedEntityAttributes` seam (flushed by
/// SpellEngine's own `EntityAttributeCreationEvent` listener), so Wizards needs no attribute listener.
@Mod(WizardsMod.ID)
public final class ForgeMod {
    // FMLJavaModLoadingContext.get() is flagged for removal by late 47.x builds, but the
    // constructor-injected replacement doesn't exist on early 47.x; get() works on all of [47,).
    @SuppressWarnings("removal")
    public ForgeMod() {
        // Run our common setup.
        WizardsMod.init();

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Explicit event classes: Forge 47's plain addListener(Consumer) infers the event type from the
        // lambda via TypeTools, which is fragile; the 4-arg overload takes it directly.
        modBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, ForgeMod::register);
        // Villager trades — game-bus event (fired per profession); replaces Fabric API's TradeOfferHelper.
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, VillagerTradesEvent.class, ForgeMod::onVillagerTrades);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClientMod.register(modBus);
        }
    }

    /// Registration is duplicated here rather than delegated to `common`'s registerX() methods, because a
    /// plain `Registry.register` is not usable on this loader: Forge only clears the vanilla registry's own
    /// lock from 47.4.0 onwards, so on 47.0-47.3 and NeoForge 1.20.1 it throws "Can not register to a locked
    /// registry" even inside the correct `RegisterEvent` window (measured on 47.3.0 with the 3.1.2.007 jar:
    /// 42 such errors and the server fails to start). The helper this event hands out is the API every build
    /// of [47,) sanctions, so Forge iterates the same content `common` exposes and registers it itself.
    /// `common` keeps its own vanilla-shaped registration for Fabric.
    ///
    /// `event.register` is a no-op unless its key matches the event's registry, so all six blocks are
    /// declared unconditionally; Forge posts one event per registry and each block runs in exactly its own.
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, helper ->
                WizardsSounds.soundsToRegister().forEach(helper::register));

        event.register(RegistryKeys.STATUS_EFFECT, helper -> {
            WizardsEffects.configure();
            Effects.effectsToRegister(WizardsEffects.entries, WizardsMod.effectsConfig.value.effects)
                    .forEach(helper::register);
            // The helper returns void where Registry.registerReference returns the entry, so the
            // Effects.Entry#entry fields are read back from the registry afterwards.
            Effects.linkEntries(WizardsEffects.entries);
            WizardsMod.effectsConfig.save();
        });

        event.register(RegistryKeys.ENTITY_TYPE, helper -> {
            WizardEntities.entityTypesToRegister().forEach(helper::register);
            // Buffered through SpellEngine's summoned-entity seam, not a registry write of its own.
            WizardEntities.registerSummonedAttributes();
        });

        event.register(RegistryKeys.ITEM, helper -> {
            // Same order as `WizardsMod.registerItems()`. The items are built inside this window: `Item`'s
            // constructor takes an intrusive registry holder. Their spell-power attribute lookups resolve
            // because Forge posts the `attribute` event (4) before the `item` one (7).
            WizardBooks.register();
            WizardWeapons.itemsToRegister(WizardsMod.equipmentConfig.value.weapons).forEach(helper::register);
            Armor.itemsToRegister(WizardsMod.equipmentConfig.value.armor_sets, WizardArmors.entries, Group.KEY)
                    .forEach(helper::register);
            WizardsMod.equipmentConfig.save();
        });

        // The item group gets its own block: `creative_mode_tab` is second to last of the ~66 registry
        // events while `item` is the 7th, so registering it from the ITEM window above would be a silent
        // key mismatch and the tab would simply never exist. `creative_mode_tab` is a vanilla-only registry,
        // so the helper falls through to a plain `Registry.register` — fine, only Forge-wrapped registries
        // are locked.
        event.register(RegistryKeys.ITEM_GROUP, helper ->
                helper.register(Group.KEY, WizardsMod.itemGroupToRegister()));

        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, helper -> {
            // Forge 47's PointOfInterestTypeCallbacks wires the block-state -> POI mapping from the type's
            // own block states as the entry is added, so nothing else is needed here.
            helper.register(WizardVillagers.POI_ID,
                    new PointOfInterestType(WizardVillagers.poiBlockStates(),
                            WizardVillagers.POI_TICKET_COUNT, WizardVillagers.POI_SEARCH_DISTANCE));
        });

        event.register(RegistryKeys.VILLAGER_PROFESSION, helper -> {
            helper.register(WizardVillagers.PROFESSION_ID, WizardVillagers.professionToRegister());
            // The helper returns void, so the field `VillagerTradesEvent` filters on is filled in afterwards.
            WizardVillagers.linkProfessionEntry();
            WizardVillagers.setupTrades();
        });
    }

    private static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != WizardVillagers.PROFESSION) {
            return;
        }
        WizardVillagers.TRADES.forEach((tier, factories) -> {
            var tierList = event.getTrades().get(tier.intValue());
            if (tierList != null) {
                tierList.addAll(factories);
            }
        });
    }
}
