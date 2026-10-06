package dev.mojoclient.modules;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    public final String name;
    public final String description;
    public boolean enabled;

    protected Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(MinecraftClient.getInstance());
        else onDisable(MinecraftClient.getInstance());
    }

    public void onEnable(MinecraftClient c) {}
    public void onDisable(MinecraftClient c) {}
    public abstract void onTick(MinecraftClient c);
}
