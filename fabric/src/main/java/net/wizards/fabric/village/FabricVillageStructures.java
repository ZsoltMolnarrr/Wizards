package net.wizards.fabric.village;

import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.spell_engine.Platform;
import net.tiny_config.ConfigManager;
import net.wizards.WizardsMod;
import net.wizards.village.VillageStructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// Fabric-only implementation of {@link VillageStructures}: StructurePoolAPI has no Forge artifact on
/// 1.20.1, so both the `config/wizards/villages.json` config and the injection call live here.
public final class FabricVillageStructures {
    private FabricVillageStructures() { }

    public static final ConfigManager<StructurePoolConfig> villageConfig = new ConfigManager<StructurePoolConfig>
            ("villages", defaults())
            .builder()
            .setDirectory(WizardsMod.ID)
            .sanitize(true)
            .build();

    /// Installs the injector and loads (or writes) the config file. Called from the Fabric entrypoint
    /// before {@code WizardsMod.registerVillagers()}.
    public static void install() {
        villageConfig.refresh();
        VillageStructures.injector = () -> {
            if (!Platform.util().isModLoaded("lithostitched")) {
                // Only inject the village if Lithostitched is not present
                StructurePoolAPI.injectAll(villageConfig.value);
            }
        };
    }

    private static StructurePoolConfig defaults() {
        var config = new StructurePoolConfig();
        var limit = 1;
        config.entries.addAll(List.of(
                new StructurePoolConfig.Entry("minecraft:village/desert/houses", new ArrayList<>(Arrays.asList(
                        new StructurePoolConfig.Entry.Structure("wizards:village/desert/wizard_tower", 1, limit),
                        new StructurePoolConfig.Entry.Structure("wizards:village/desert/wizard_tower_2", 3, limit))
                )),
                new StructurePoolConfig.Entry("minecraft:village/savanna/houses", "wizards:village/savanna/wizard_tower", 3, limit),

                new StructurePoolConfig.Entry("minecraft:village/plains/houses", "wizards:village/plains/wizard_tower", 3, limit),

                new StructurePoolConfig.Entry("minecraft:village/taiga/houses", "wizards:village/taiga/wizard_tower", 3, limit),

                new StructurePoolConfig.Entry("minecraft:village/snowy/houses", new ArrayList<>(Arrays.asList(
                        new StructurePoolConfig.Entry.Structure("wizards:village/snowy/wizard_tower", 1, limit),
                        new StructurePoolConfig.Entry.Structure("wizards:village/snowy/wizard_tower_2", 3, limit))
                ))
        ));
        return config;
    }
}
