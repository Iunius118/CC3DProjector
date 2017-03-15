package iunius118.mods.cc3dprojector.peripheral;

import java.util.HashMap;
import java.util.List;
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
		return new String[] {"write", "read", "clear"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch (method) {
		case 0: 	// write( table )
			if (arguments.length == 0 || !(arguments[0] instanceof Map)) {
				throw new LuaException("Expected table");
			} else {
				ModelProgramProcessor processor = new ModelProgramProcessor();
				byte[] buf = processor.compile((Map)arguments[0]);
				byte[] buf2 = processor.deflate(buf);
				setModelProgram(buf2, computer.getID());
			}

			break;

		case 1: 	// read() -> table
			byte[] buf = getModelProgram();

			if (buf != null) {
				ModelProgramProcessor processor = new ModelProgramProcessor();
				byte[] buf2 = processor.inflate(buf);
				List<Map<Integer, Object>> list = processor.decompile(buf2);
				Map<Integer, Map<Integer, Object>> map = new HashMap();

				for (int i = 0; i < list.size(); i++) {
					map.put(Integer.valueOf(i + 1), list.get(i));
				}

				return new Object[] {map};
			}

			break;

		case 2: 	// clear()
			clearModelProgram();
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

	private void setModelProgram(byte[] buf, int computerID) {
		NBTTagCompound tag = null;

		if (type == PeripheralType.TILEENTITY) {
			tag = tileentity.getTileData();
		} else if (type == PeripheralType.UPGRADE) {
			tag = turtleAccess.getUpgradeNBTData(turtleSide);
		}

		if (tag != null) {
			if (type == PeripheralType.TILEENTITY) {
				tileentity.getWorld().setBlockState(tileentity.getPos(), CC3DProjector.block3DProjector.getDefaultState().withProperty(Block3DProjector.IS_ON, Boolean.FALSE));
				tileentity.getWorld().setBlockState(tileentity.getPos(), CC3DProjector.block3DProjector.getDefaultState().withProperty(Block3DProjector.IS_ON, Boolean.TRUE));
				tag.setByteArray(TAG_MODEL, buf);
			} else if (type == PeripheralType.UPGRADE) {
				tag.setByteArray(TAG_MODEL, buf);
				tag.setBoolean(Turtle3DProjector.TAG_IS_ON, true);
				tag.setInteger(Turtle3DProjector.TAG_COMPUTER_ID, computerID);
				turtleAccess.updateUpgradeNBTData(turtleSide);
			}
		}
	}

	private byte[] getModelProgram() {
		byte[] buf = null;
		NBTTagCompound tag = null;

		if (type == PeripheralType.TILEENTITY) {
			tag = tileentity.getTileData();
		} else if (type == PeripheralType.UPGRADE) {
			tag = turtleAccess.getUpgradeNBTData(turtleSide);
		}

		if (tag != null) {
			buf = tag.getByteArray(TAG_MODEL);
			if (buf.length > 0) {
				return buf;
			}
		}

		return null;
	}

	private void clearModelProgram() {
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

	public static class Identification {

		public final PeripheralType type;
		public final BlockPos pos;
		public final int id;
		public final TurtleSide turtleSide;

		public Identification(TileEntity3DProjector tile) {
			type = PeripheralType.TILEENTITY;
			pos = tile.getPos();
			id = -1;
			turtleSide = null;
		}

		public Identification(int computerID, TurtleSide side) {
			type = PeripheralType.UPGRADE;
			pos = null;
			id = computerID;
			turtleSide = side;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			result = prime * result + ((pos == null) ? 0 : pos.hashCode());
			result = prime * result + ((turtleSide == null) ? 0 : turtleSide.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
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
				if (id > -1 && id == other.id
						&& turtleSide != null && turtleSide == other.turtleSide) {
					return true;
				}
			}

			return false;
		}

	}

}
