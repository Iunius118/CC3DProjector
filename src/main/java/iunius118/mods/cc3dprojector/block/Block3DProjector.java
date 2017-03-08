package iunius118.mods.cc3dprojector.block;

import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Block3DProjector extends BlockContainer {

	public Block3DProjector() {
		super(Material.ground);
		this.setStepSound(soundTypeStone);
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntity3DProjector();
	}

}
