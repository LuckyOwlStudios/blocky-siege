package net.luckystudios.blocks.custom.turret.crossbowgolem;

import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.entity.LivingEntity;
import net.luckystudios.util.MenuHelper;
import com.mojang.serialization.MapCodec;
import net.luckystudios.entity.custom.turrets.crossbowgolem.CrossbowGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrossbowGolemBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<CrossbowGolemBlock> CODEC = simpleCodec(CrossbowGolemBlock::new);

    public CrossbowGolemBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    // This block has no facing property (HorizontalDirectionalBlock would look for one), so structures
    // that rotate or mirror it, like the village towers, must leave the state alone.
    @Override
    protected BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state;
    }

    @Override
    protected BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CrossbowGolemBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (movedByPiston) return;
        BlockPos abovePos = pos.above();
        if (level.getBlockState(abovePos).canBeReplaced()) {
            CrossbowGolemEntity crossbow_golem = new CrossbowGolemEntity(level, pos);
            crossbow_golem.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            level.addFreshEntity(crossbow_golem);
        }
    }

    /** Spawns the golem for a block that came from a generated structure, then forgets the request. */
    @Override
    protected void tick(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof CrossbowGolemBlockEntity be) || !be.spawnGolemOnLoad) return;
        be.spawnGolemOnLoad = false;
        be.setChanged();
        BlockPos abovePos = pos.above();
        if (level.getBlockState(abovePos).canBeReplaced()
                && level.getEntitiesOfClass(CrossbowGolemEntity.class, new AABB(abovePos)).isEmpty()) {
            CrossbowGolemEntity golem = new CrossbowGolemEntity(level, pos);
            golem.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            golem.setPersistenceRequired();
            level.addFreshEntity(golem);
        }
    }

    /** A crossbow_golem joins its placer's scoreboard team, so it spares teammates and targets everyone else. */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide || placer == null) return;
        PlayerTeam team = placer.getTeam();
        for (CrossbowGolemEntity crossbow_golem : level.getEntitiesOfClass(CrossbowGolemEntity.class, new AABB(pos.above()))) {
            crossbow_golem.setPlayerCreated(true);
            crossbow_golem.setPlacer(placer);
            if (team == null) continue;
            level.getScoreboard().addPlayerToTeam(crossbow_golem.getScoreboardName(), team);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos abovePos = context.getClickedPos().above();
        Level level = context.getLevel();
        if (level.getBlockState(abovePos).canBeReplaced()) {
            return super.getStateForPlacement(context);
        }
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof CrossbowGolemBlockEntity crossbow_golemBlockEntity) {
            MenuHelper.openMenu(player, new SimpleMenuProvider(crossbow_golemBlockEntity, crossbow_golemBlockEntity.getDisplayName()), pos);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // Only remove entity if the block is actually being broken (not just block state changing)
        if (!state.is(newState.getBlock())) {
            // Drop the crossbow and ammo instead of deleting them
            if (level.getBlockEntity(pos) instanceof CrossbowGolemBlockEntity crossbow_golemBlockEntity) {
                Containers.dropContents(level, pos, crossbow_golemBlockEntity.inventory);
            }
            // Find and remove the crossbow_golem entity above this block
            BlockPos abovePos = pos.above();
            List<CrossbowGolemEntity> crossbow_golems = level.getEntitiesOfClass(CrossbowGolemEntity.class,
                    new AABB(abovePos).inflate(0.1)); // Small area around the position above

            for (CrossbowGolemEntity crossbow_golem : crossbow_golems) {
                crossbow_golem.discard(); // Remove the entity
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
