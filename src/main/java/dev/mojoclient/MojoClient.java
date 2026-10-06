package dev.mojoclient;

import dev.mojoclient.modules.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class MojoClient implements ClientModInitializer {
    public static final List<Module> MODULES = List.of(
            new KillAura(), new SprintToggle(), new Fullbright());

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            for (Module m : MODULES) if (m.enabled) m.onTick(c);
        });

        // Duraklatma menusune "MojoClient" butonu ekle (dokunmatik icin kolay erisim)
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof GameMenuScreen) {
                Screens.getButtons(screen).add(ButtonWidget.builder(
                        Text.literal("MojoClient"),
                        b -> client.setScreen(new ModMenuScreen(screen)))
                        .dimensions(5, 5, 90, 20).build());
            }
        });
    }
}
