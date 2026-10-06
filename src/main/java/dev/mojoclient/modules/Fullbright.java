package dev.mojoclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends Module {
    public Fullbright() { super("Fullbright", "Her yer aydinlik gorunur"); }

    @Override
    public void onTick(MinecraftClient c) {
        if (c.player == null) return;
        c.player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
    }

    @Override
    public void onDisable(MinecraftClient c) {
        if (c.player != null) c.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
