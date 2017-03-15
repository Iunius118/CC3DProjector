package iunius118.mods.cc3dprojector.block;

import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Block3DProjector extends BlockContainer {

	public static final PropertyBool IS_ON = PropertyBool.create("ison");

	public Block3DProjector() {
		super(Material.ground);
		this.setStepSound(soundTypeStone);
		this.setCreativeTab(CreativeTabs.tabDecorations);

		this.setDefaultState(this.blockState.getBaseState().withProperty(IS_ON, Boolean.FALSE));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(IS_ON, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(IS_ON).booleanValue() ? 1 : 0;
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, IS_ON);
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		return (world.getBlockState(pos).getValue(IS_ON)) ? 15 : 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntity3DProjector();
	}

}
