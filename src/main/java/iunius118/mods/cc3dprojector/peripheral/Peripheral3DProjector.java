package iunius118.mods.cc3dprojector.peripheral;

import java.util.Map;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import net.minecraft.util.BlockPos;

public class Peripheral3DProjector implements IPeripheral {

	private final PeripheralType type;
	private final TileEntity3DProjector tileentity;
	private final ITurtleAccess turtleAccess;
	private final TurtleSide turtleSide;

	public Peripheral3DProjector(TileEntity3DProjector tile) {
		type = PeripheralType.TILEENTITY;
		tileentity = tile;
		turtleAccess = null;
		turtleSide = null;
	}

	public Peripheral3DProjector(ITurtleAccess turtle, TurtleSide side) {
		type = PeripheralType.UPGRADE;
		tileentity = null;
		turtleAccess = turtle;
		turtleSide = side;
	}

	@Override
	public String getType() {
		return "3d_projector";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] {"send", "clear"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch (method) {
		case 0: 	// send( table )
			if (arguments.length == 0 || !(arguments[0] instanceof Map)) {
				throw new LuaException("Expected table");
			} else {
				ModelProgramProcessor model = new ModelProgramProcessor();
				byte[] buf = model.compile((Map)arguments[0]);
				Map map = model.decompile(buf);
				return new Object[] {map};
			}
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

	public class Identification {

		private final PeripheralType type;
		private final BlockPos pos;
		private final int computerID;
		private final TurtleSide turtleSide;

		public Identification(TileEntity3DProjector tile) {
			type = PeripheralType.TILEENTITY;
			pos = tile.getPos();
			computerID = -1;
			turtleSide = null;
		}

		public Identification(IComputerAccess computer, TurtleSide side) {
			type = PeripheralType.UPGRADE;
			pos = null;
			computerID = computer.getID();
			turtleSide = side;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (!(obj instanceof Identification)) {
				return false;
			}

			Identification other = (Identification) obj;

			if (type != other.type) {
				return false;
			}

			if (type == PeripheralType.TILEENTITY) {
				if (pos != null && pos.equals(other.pos)) {
					return true;
				}
			} else {
				if (computerID == other.computerID && type == other.type) {
					return true;
				}
			}

			return false;
		}

	}

}
