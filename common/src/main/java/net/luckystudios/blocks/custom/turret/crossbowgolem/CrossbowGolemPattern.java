package net.luckystudios.blocks.custom.turret.crossbowgolem;

import net.luckystudios.entity.custom.turrets.crossbowgolem.CrossbowGolemEntity;
import net.luckystudios.init.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Golem-style construction: iron block at the base, a dispenser on top of it, a log on each side of the
 * dispenser, and a carved pumpkin on top of the dispenser. The pattern is checked when a carved pumpkin is
 * placed (by a player or a dispenser). The blocks are consumed and a crossbow_golem is built on the iron block's spot.
 */
public final class CrossbowGolemPattern {
    private static BlockPattern pattern;

    private CrossbowGolemPattern() {
    }

    private static BlockPattern pattern() {
        if (pattern == null) {
            Predicate<BlockState> log = state -> state.is(BlockTags.LOGS);
            pattern = BlockPatternBuilder.start()
                    .aisle("~^~", "LDL", "~#~")
                    .where('^', BlockInWorld.hasState(state -> state.is(Blocks.CARVED_PUMPKIN)))
                    .where('D', BlockInWorld.hasState(state -> state.is(Blocks.DISPENSER)))
                    .where('L', BlockInWorld.hasState(log))
                    .where('#', BlockInWorld.hasState(state -> state.is(Blocks.IRON_BLOCK)))
                    .where('~', BlockInWorld.hasState(BlockStatePredicate.ANY))
                    .build();
        }
        return pattern;
    }

    /** Called after a carved pumpkin was placed at {@code pumpkinPos}. Returns true if a crossbow_golem was built. */
    public static boolean trySpawn(Level level, BlockPos pumpkinPos) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        BlockPattern.BlockPatternMatch match = pattern().find(level, pumpkinPos);
        if (match == null) {
            return false;
        }

        // The base of the crossbow_golem is where the iron block stood.
        BlockPos basePos = match.getBlock(1, 2, 0).getPos();
        BlockPos dispenserPos = match.getBlock(1, 1, 0).getPos();

        // Don't lose whatever the dispenser was holding.
        BlockEntity dispenser = level.getBlockEntity(dispenserPos);
        if (dispenser instanceof net.minecraft.world.Container container) {
            Containers.dropContents(level, dispenserPos, container);
        }

        // Only the five pattern blocks are consumed, never the wildcard corners.
        int[][] cells = {{1, 0}, {0, 1}, {1, 1}, {2, 1}, {1, 2}};
        for (int[] cell : cells) {
            BlockInWorld block = match.getBlock(cell[0], cell[1], 0);
            int stateId = Block.getId(block.getState());
            level.setBlock(block.getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.levelEvent(2001, block.getPos(), stateId);
        }

        // Putting the crossbow_golem block down spawns the crossbow_golem entity on top of it.
        level.setBlock(basePos, ModBlocks.CROSSBOW_GOLEM_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
        List<CrossbowGolemEntity> spawned = level.getEntitiesOfClass(CrossbowGolemEntity.class, new AABB(basePos.above()).inflate(0.1));
        Player placer = level.getNearestPlayer(basePos.getX() + 0.5, basePos.getY() + 0.5, basePos.getZ() + 0.5, 6.0, false);
        for (CrossbowGolemEntity crossbow_golem : spawned) {
            if (placer != null) {
                crossbow_golem.setPlacer(placer);
                crossbow_golem.setPlayerCreated(true);
                // Same as placing the block by hand: the crossbow_golem joins the builder's scoreboard team.
                if (placer.getTeam() instanceof net.minecraft.world.scores.PlayerTeam team) {
                    level.getScoreboard().addPlayerToTeam(crossbow_golem.getScoreboardName(), team);
                }
                crossbow_golem.setYRot(placer.getYRot() + 180.0F);
                crossbow_golem.yRotO = crossbow_golem.getYRot();
                crossbow_golem.yBodyRot = crossbow_golem.getYRot();
                crossbow_golem.yHeadRot = crossbow_golem.getYRot();
            }
            for (int i = 0; i < 20; i++) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF,
                        crossbow_golem.getX(), crossbow_golem.getY() + 0.5 + level.random.nextDouble(), crossbow_golem.getZ(),
                        1, 0.4, 0.4, 0.4, 0.02);
            }
        }
        level.playSound(null, basePos, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (placer instanceof ServerPlayer serverPlayer && !spawned.isEmpty()) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, spawned.get(0));
        }

        for (int[] cell : cells) {
            level.blockUpdated(match.getBlock(cell[0], cell[1], 0).getPos(), Blocks.AIR);
        }
        return true;
    }
}
