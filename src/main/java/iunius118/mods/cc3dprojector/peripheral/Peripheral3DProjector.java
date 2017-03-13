package iunius118.mods.cc3dprojector.peripheral;

import java.util.Map;

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

	public static final String TAG_MODEL = "model";

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

				System.out.println("compiled size: " + buf.length);

				byte[] buf2 = model.deflate(buf);

				System.out.println("deflated size: " + buf2.length);

				byte[] buf3 = model.inflate(buf2);

				System.out.println("inflated size: " + buf3.length);

				Map map = model.decompile(buf3);

				addModelProgram(buf2);

				return new Object[] {map};
			}

		case 1: 	// clear()
			removeModelProgram();
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

	private void addModelProgram(byte[] buf) {
		NBTTagCompound tag = null;

		if (type == PeripheralType.TILEENTITY) {
			tag = tileentity.getTileData();
		} else if (type == PeripheralType.UPGRADE) {
			tag = turtleAccess.getUpgradeNBTData(turtleSide);
		}

		if (tag != null) {
			tag.setByteArray(TAG_MODEL, buf);


			if (type == PeripheralType.TILEENTITY) {
				tileentity.getWorld().setBlockState(tileentity.getPos(), CC3DProjector.block3DProjector.getDefaultState().withProperty(Block3DProjector.IS_ON, Boolean.TRUE));
			} else if (type == PeripheralType.UPGRADE) {
				tag.setBoolean(Turtle3DProjector.TAG_IS_ON, true);
				turtleAccess.updateUpgradeNBTData(turtleSide);
			}
		}
	}

	private void removeModelProgram() {
		NBTTagCompound tag = null;

		if (type == PeripheralType.TILEENTITY) {
			tag = tileentity.getTileData();
		} else if (type == PeripheralType.UPGRADE) {
			tag = turtleAccess.getUpgradeNBTData(turtleSide);
		}

		if (tag != null) {
			tag.removeTag(TAG_MODEL);

			if (type == PeripheralType.TILEENTITY) {
				tileentity.getWorld().setBlockState(tileentity.getPos(), CC3DProjector.block3DProjector.getDefaultState().withProperty(Block3DProjector.IS_ON, Boolean.FALSE));
			} else if (type == PeripheralType.UPGRADE) {
				tag.setBoolean(Turtle3DProjector.TAG_IS_ON, false);
				turtleAccess.updateUpgradeNBTData(turtleSide);
			}
		}
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
			} else if (type == PeripheralType.UPGRADE) {
				if (computerID == other.computerID && type == other.type) {
					return true;
				}
			}

			return false;
		}

	}

}
