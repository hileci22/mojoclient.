package dev.mojoclient;

import net.minecraft.client.MinecraftClient;

public class ModConfig {
    // Sadece sunucu sahibi combat modullerine acikca izin veriyorsa true yap.
    public static boolean allowOnMultiplayer = false;

    public static boolean combatAllowed(MinecraftClient c) {
        return c.isInSingleplayer() || c.getServer() != null || allowOnMultiplayer;
    }
}
