package net.luckystudios.util;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Helpers for entities that ride on Sable sub-levels (moving block structures, e.g. Create Aeronautics ships).
 * <p>
 * An entity on a sub-level actually lives in that sub-level's "plot", a far-away patch of the level, and is
 * drawn at the sub-level's real position. So its own coordinates (and its yaw/pitch) are in the plot's frame,
 * while everything else in the world is in world space. These helpers convert between the two.
 * <p>
 * Built on Sable Companion (MIT). NeoForge bundles it; on Fabric it only exists when Sable is installed, so
 * every call is skipped when the class is missing, and without Sable every method returns its input.
 */
public final class SubLevels {
    private static final boolean AVAILABLE = isCompanionPresent();

    private SubLevels() {
    }

    private static boolean isCompanionPresent() {
        try {
            Class.forName("dev.ryanhcode.sable.companion.SableCompanion", false, SubLevels.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }

    /** Whether this entity sits in a sub-level's plot (i.e. rides a moving structure). */
    public static boolean isOnSubLevel(Entity entity) {
        return AVAILABLE && Companion.containing(entity) != null;
    }

    /** Where a point really is in the world: projects it out of a sub-level plot, or returns it as is. */
    public static Vec3 toWorld(Level level, Vec3 point) {
        return AVAILABLE ? Companion.toWorld(level, point) : point;
    }

    /**
     * Converts a point (world space, or in any sub-level's plot) into the frame {@code self} lives in, so it can be
     * compared with {@code self}'s own coordinates and rotations.
     */
    public static Vec3 toFrameOf(Entity self, Vec3 point) {
        return AVAILABLE ? Companion.toFrameOf(self, point) : point;
    }

    /** Rotates a world-space direction (e.g. a velocity) into the frame {@code self} lives in. */
    public static Vec3 directionToFrameOf(Entity self, Vec3 direction) {
        return AVAILABLE ? Companion.directionToFrameOf(self, direction) : direction;
    }

    /** Only loaded when Sable Companion is present, so its classes are never touched otherwise. */
    private static final class Companion {
        static SubLevelAccess containing(Entity entity) {
            return SableCompanion.INSTANCE.getContaining(entity);
        }

        static Vec3 toWorld(Level level, Vec3 point) {
            return SableCompanion.INSTANCE.projectOutOfSubLevel(level, (Position) point);
        }

        static Vec3 toFrameOf(Entity self, Vec3 point) {
            Vec3 world = toWorld(self.level(), point);
            SubLevelAccess subLevel = containing(self);
            return subLevel == null ? world : subLevel.logicalPose().transformPositionInverse(world);
        }

        static Vec3 directionToFrameOf(Entity self, Vec3 direction) {
            SubLevelAccess subLevel = containing(self);
            return subLevel == null ? direction : subLevel.logicalPose().transformNormalInverse(direction);
        }
    }
}
