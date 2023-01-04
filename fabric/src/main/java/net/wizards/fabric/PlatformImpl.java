package net.wizards.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.wizards.Platform;

import static net.wizards.Platform.Type.FABRIC;

public class PlatformImpl {
    public static Platform.Type getPlatformType() {
        return FABRIC;
    }

    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}
