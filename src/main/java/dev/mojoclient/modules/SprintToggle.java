package dev.mojoclient.modules;

import net.minecraft.client.MinecraftClient;

public class SprintToggle extends Module {
    public SprintToggle() { super("Sprint Toggle", "Ileri giderken otomatik kosar"); }

    @Override
    public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        if (c.options.forwardKey.isPressed() && !c.player.isSneaking()
                && c.player.getHungerManager().getFoodLevel() > 6) {
            c.player.setSprinting(true);
        }
    }
}
