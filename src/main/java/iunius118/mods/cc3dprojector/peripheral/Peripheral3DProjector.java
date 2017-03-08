package iunius118.mods.cc3dprojector.peripheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;

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
		return null;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
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
		return false;
	}

}
