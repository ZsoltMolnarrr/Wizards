package net.wizards.villager;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.spell_engine.spellbinding.SpellBindingBlock;
import net.wizards.WizardsMod;
import net.wizards.item.Armors;
import net.wizards.item.WizardArmor;

public class WizardVillagers {
    public static final String WIZARD_MERCHANT = "wizard_merchant";

    public static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(new Identifier(WizardsMod.ID, name),
                1, 10, ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = new Identifier(WizardsMod.ID, name);
        return Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(WizardsMod.ID, name), new VillagerProfession(
                id.toString(),
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                WizardArmor.equipSound)
        );
    }

    public static void register() {
        var wizardPOI = registerPOI(WIZARD_MERCHANT, SpellBindingBlock.INSTANCE);
        var wizardMerchantProfession = registerProfession(
                WIZARD_MERCHANT,
                RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, new Identifier(WizardsMod.ID, WIZARD_MERCHANT)));
        TradeOfferHelper.registerVillagerOffers(wizardMerchantProfession, 1, factories -> {
                factories.add(((entity, random) -> new TradeOffer(
                        new ItemStack(Items.EMERALD, 3),
                        Armors.wizardRobeSet.head.getDefaultStack(),
                        6, 2, 0.02f)
                ));
        });
    }
}
