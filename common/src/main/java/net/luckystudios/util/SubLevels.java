package net.luckystudios.util;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

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

    /** Rotates a direction in {@code self}'s frame (e.g. its view or a shot's velocity) into world space. */
    public static Vec3 directionToWorld(Entity self, Vec3 direction) {
        return AVAILABLE ? Companion.directionToWorld(self, direction) : direction;
    }

    /**
     * A sub-level seen as a coordinate frame, for code that has to test world-space movement against the blocks
     * in a sub-level's plot (the blocks are really stored far away, so ordinary world collision never sees them).
     */
    public interface Frame {
        /** A world-space point in the sub-level's plot space, using where the sub-level was last tick (or is now). */
        Vec3 toLocal(Vec3 world, boolean previousPose);

        /** A world-space point in the sub-level's plot space, using where the sub-level is now. */
        default Vec3 toLocal(Vec3 world) {
            return toLocal(world, false);
        }

        /** A plot-space point in world space, using where the sub-level is now. */
        Vec3 toWorld(Vec3 local);

        Vec3 directionToLocal(Vec3 world);

        Vec3 directionToWorld(Vec3 local);
    }

    /** The sub-levels whose bounds touch this world-space box. Empty without Sable. */
    public static List<Frame> framesIntersecting(Level level, AABB worldBounds) {
        return AVAILABLE ? Companion.framesIntersecting(level, worldBounds) : List.of();
    }

    /** Only loaded when Sable Companion is present, so its classes are never touched otherwise. */
    private static final class Companion {
        static List<Frame> framesIntersecting(Level level, AABB worldBounds) {
            List<Frame> frames = new ArrayList<>();
            for (SubLevelAccess subLevel : SableCompanion.INSTANCE.getAllIntersecting(level, new BoundingBox3d(worldBounds))) {
                frames.add(new Frame() {
                    @Override
                    public Vec3 toLocal(Vec3 world, boolean previousPose) {
                        return (previousPose ? subLevel.lastPose() : subLevel.logicalPose()).transformPositionInverse(world);
                    }

                    @Override
                    public Vec3 toWorld(Vec3 local) {
                        return subLevel.logicalPose().transformPosition(local);
                    }

                    @Override
                    public Vec3 directionToLocal(Vec3 world) {
                        return subLevel.logicalPose().transformNormalInverse(world);
                    }

                    @Override
                    public Vec3 directionToWorld(Vec3 local) {
                        return subLevel.logicalPose().transformNormal(local);
                    }
                });
            }
            return frames;
        }

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

        static Vec3 directionToWorld(Entity self, Vec3 direction) {
            SubLevelAccess subLevel = containing(self);
            return subLevel == null ? direction : subLevel.logicalPose().transformNormal(direction);
        }
    }
}
