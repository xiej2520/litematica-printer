package me.aleksilassila.litematica.printer.utils.minecraft;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

/** {@link Vec3}'s static factories, which 1.15.2 lacks. */
public final class Vec3Utils {
    private Vec3Utils() {
    }

    public static Vec3 atLowerCornerOf(Vec3i pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3 atCenterOf(Vec3i pos) {
        return new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }
}
