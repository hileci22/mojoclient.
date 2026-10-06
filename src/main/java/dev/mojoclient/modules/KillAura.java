package dev.mojoclient.modules;

import dev.mojoclient.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {
    // Ayarlar
    public double range = 3.0;
    public int cps = 8;
    public boolean targetPlayers = false;
    public boolean targetMobs = true;
    public boolean autoLook = true;      // hedefe otomatik bak
    public boolean strafe = true;        // hedefin etrafinda don
    public boolean strafeJump = true;    // doneren zipla
    public double strafeSpeed = 0.22;    // blok/tick
    public double strafeStep = 0.35;     // radyan/tick, donus hizi

    private long last;
    private int dir = 1;

    public KillAura() { super("Kill Aura", "Yakindaki hedefe otomatik vurur, etrafinda doner"); }

    @Override
    public void onTick(MinecraftClient c) {
        PlayerEntity p = c.player;
        if (p == null || c.world == null || c.interactionManager == null) return;
        if (!ModConfig.combatAllowed(c)) return;

        LivingEntity target = findTarget(c, p);
        if (target == null) return;

        if (autoLook) lookAt(p, target);
        if (strafe) strafeAround(p, target);

        long now = System.currentTimeMillis();
        if (now - last < 1000L / Math.max(1, cps)) return;
        if (p.getAttackCooldownProgress(0f) < 0.9f) return;
        if (p.squaredDistanceTo(target) > range * range) return;

        c.interactionManager.attackEntity(p, target);
        p.swingHand(Hand.MAIN_HAND);
        last = now;
    }

    private LivingEntity findTarget(MinecraftClient c, PlayerEntity p) {
        LivingEntity best = null;
        double bestD = (range + 1.0) * (range + 1.0);
        for (LivingEntity e : c.world.getEntitiesByClass(LivingEntity.class,
                p.getBoundingBox().expand(range + 1.0), x -> valid(p, x))) {
            double d = p.squaredDistanceTo(e);
            if (d <= bestD) { bestD = d; best = e; }
        }
        return best;
    }

    private boolean valid(PlayerEntity self, LivingEntity e) {
        if (e == self || !e.isAlive()) return false;
        if (e instanceof PlayerEntity pe) return targetPlayers && !pe.isSpectator();
        return targetMobs && e instanceof HostileEntity;
    }

    private void lookAt(PlayerEntity p, LivingEntity t) {
        Vec3d eye = p.getEyePos();
        Vec3d to = t.getPos().add(0, t.getHeight() * 0.6, 0);
        double dx = to.x - eye.x, dy = to.y - eye.y, dz = to.z - eye.z;
        double h = Math.sqrt(dx * dx + dz * dz);
        p.setYaw((float) Math.toDegrees(Math.atan2(-dx, dz)));
        p.setPitch((float) -Math.toDegrees(Math.atan2(dy, h)));
    }

    private void strafeAround(PlayerEntity p, LivingEntity t) {
        double dx = p.getX() - t.getX();
        double dz = p.getZ() - t.getZ();
        double angle = Math.atan2(dz, dx);

        if (p.horizontalCollision) dir = -dir; // duvara carpinca yon degistir

        double radius = Math.max(1.5, range - 0.5);
        double nextAngle = angle + dir * strafeStep;
        double tx = t.getX() + Math.cos(nextAngle) * radius;
        double tz = t.getZ() + Math.sin(nextAngle) * radius;

        double mx = tx - p.getX(), mz = tz - p.getZ();
        double len = Math.sqrt(mx * mx + mz * mz);
        if (len < 1.0E-4) return;
        double speed = Math.min(strafeSpeed, len);
        mx = mx / len * speed;
        mz = mz / len * speed;

        p.setVelocity(mx, p.getVelocity().y, mz);
        if (strafeJump && p.isOnGround()) p.jump();
    }
}
