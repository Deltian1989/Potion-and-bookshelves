package net.deltian.potionandbookshelves.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RayTraceUtils {
    public static HitResult rayTrace(Level world, Player player, double range) {
        Vec3 startVec = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getLookAngle();
        Vec3 endVec = startVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        ClipContext context = new ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return world.clip(context);
    }
}
