package iunius118.mods.cc3dprojector.block;

import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Block3DProjector extends BlockContainer {
    public static final PropertyBool IS_ON = PropertyBool.create("ison");

    public Block3DProjector() {
        super(Material.GROUND);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(CreativeTabs.DECORATIONS);

        this.setDefaultState(this.blockState.getBaseState().withProperty(IS_ON, Boolean.FALSE));
    }

    @Override
    @Deprecated
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(IS_ON, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(IS_ON) ? 1 : 0;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IS_ON);
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public int getLightValue(IBlockState state) {
        return (state.getValue(IS_ON)) ? 15 : 0;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nullable World worldIn, int meta) {
        return new TileEntity3DProjector();
    }
}
