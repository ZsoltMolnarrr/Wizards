package net.wizards.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.spell_engine.api.datagen.SimpleSoundGeneratorV2;
import net.spell_engine.api.datagen.SpellGenerator;
import net.wizards.WizardsMod;
import net.wizards.content.WizardSpells;
import net.wizards.content.WizardsSounds;

import java.util.concurrent.CompletableFuture;

public class WizardsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(SoundGen::new);
        pack.addProvider(SpellGen::new);
    }

    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSpells(Builder builder) {
            for (var entry: WizardSpells.entries) {
                builder.add(entry.id(), entry.spell());
            }
        }
    }

    public static class SoundGen extends SimpleSoundGeneratorV2 {
        public SoundGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSounds(Builder builder) {
            builder.entries.add(new Entry(WizardsMod.ID,
                    WizardsSounds.entries.stream()
                            .map(entry -> SoundEntry.withVariants(entry.id().getPath(), entry.variants()))
                            .toList()
                    )
            );
        }
    }
}
