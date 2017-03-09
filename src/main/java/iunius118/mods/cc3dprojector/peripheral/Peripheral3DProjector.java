package iunius118.mods.cc3dprojector.peripheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import iunius118.mods.cc3dprojector.CC3DProjector;
import iunius118.mods.cc3dprojector.block.Block3DProjector;
import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import iunius118.mods.cc3dprojector.upgrade.Turtle3DProjector;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class Peripheral3DProjector implements IPeripheral {

	private final PeripheralType type;
	private final TileEntity3DProjector tileentity;
	private final ITurtleAccess turtleAccess;
	private final TurtleSide turtleSide;

	public Peripheral3DProjector(ITurtleAccess turtle, TurtleSide side) {
		type = PeripheralType.UPGRADE;
		tileentity = null;
		turtleAccess = turtle;
		turtleSide = side;
	}

	public Peripheral3DProjector(TileEntity3DProjector tile) {
		type = PeripheralType.TILEENTITY;
		tileentity = tile;
		turtleAccess = null;
		turtleSide = null;
	}

	@Override
	public String getType() {
		return "3d_projector";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] {"turnOn", "turnOff"};
	}

	public void turnOn() {
		if (type == PeripheralType.UPGRADE) {
			NBTTagCompound tag = turtleAccess.getUpgradeNBTData(turtleSide);

			if (tag != null && tag.getBoolean(Turtle3DProjector.TAG_IS_ON) == false) {
				tag.setBoolean(Turtle3DProjector.TAG_IS_ON, true);
				turtleAccess.updateUpgradeNBTData(turtleSide);
			}
		} else {
			if (tileentity.getBlockMetadata() == 0) {
				tileentity.getWorld().setBlockState(tileentity.getPos(), CC3DProjector.block3DProjector.getDefaultState().withProperty(Block3DProjector.IS_ON, Boolean.TRUE));
			}
		}
	}

	public void turnOff() {
		if (type == PeripheralType.UPGRADE) {
			NBTTagCompound tag = turtleAccess.getUpgradeNBTData(turtleSide);

			if (tag != null && tag.getBoolean(Turtle3DProjector.TAG_IS_ON) == true) {
				tag.setBoolean(Turtle3DProjector.TAG_IS_ON, false);
				turtleAccess.updateUpgradeNBTData(turtleSide);
			}
		} else {
			if (tileentity.getBlockMetadata() == 1) {
				tileentity.getWorld().setBlockState(tileentity.getPos(), CC3DProjector.block3DProjector.getDefaultState().withProperty(Block3DProjector.IS_ON, Boolean.FALSE));
			}
		}
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch (method) {
		case 0: 	// turnOn
			turnOn();
			break;
		case 1: 	// turnOff
			turnOff();
			break;
		}

		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {

	}

	@Override
	public void detach(IComputerAccess computer) {

	}

	@Override
	public boolean equals(IPeripheral other) {
		if (other instanceof Peripheral3DProjector) {
			Peripheral3DProjector old = (Peripheral3DProjector)other;

			if (type == PeripheralType.UPGRADE) {
				BlockPos oldPos = old.turtleAccess.getPosition();
				TurtleSide oldSide = old.turtleSide;
				return this.turtleAccess.getPosition().equals(oldPos) && (this.turtleSide == oldSide);
			} else {
				return old == this;
			}
		}

		return false;
	}

}
