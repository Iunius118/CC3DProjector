package iunius118.mods.cc3dprojector.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class PeripheralProvider implements IPeripheralProvider {

	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileEntity3DProjector) {
			return ((TileEntity3DProjector)tile).getPeripheral();
		}

		return null;
	}

}
