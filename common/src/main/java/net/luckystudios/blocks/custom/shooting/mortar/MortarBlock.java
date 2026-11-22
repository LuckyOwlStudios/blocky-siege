package net.luckystudios.blocks.custom.shooting.mortar;

import com.mojang.serialization.MapCodec;
import net.luckystudios.blocks.custom.shooting.cannon.CannonBlock;
import net.luckystudios.blocks.custom.shooting.cannon.CannonBlockEntity;
import net.luckystudios.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A cannon that lobs its shot in a high arc. It loads, fires and is crewed like the cannon (same GUI, ammo and
 * fuse); only the barrel's pitch range, the model and the falling-shell whistle differ.
 */
public class MortarBlock extends CannonBlock {
    public static final MapCodec<MortarBlock> CODEC = simpleCodec(MortarBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public MortarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MortarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntityTypes.MORTAR_BLOCK_ENTITY.get(), CannonBlockEntity::tick);
    }
}
