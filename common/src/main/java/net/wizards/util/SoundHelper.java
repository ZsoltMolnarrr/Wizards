package net.wizards.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SoundHelper {
    public static void playSoundEvent(Level world, Entity entity, SoundEvent soundEvent) {
        playSoundEvent(world, entity, soundEvent, 1, 1);
    }

    public static void playSoundEvent(Level world, Entity entity, SoundEvent soundEvent, float volume, float pitch) {
        world.playSound(
                (Player)null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                soundEvent,
                SoundSource.PLAYERS,
                volume,
                pitch);
    }
}
