package net.wizards.forge;

import net.minecraftforge.fml.ModList;
import net.wizards.Platform;

import static net.wizards.Platform.Type.FORGE;

public class PlatformImpl {
    public static Platform.Type getPlatformType() {
        return FORGE;
    }

    public static boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
