package net.github.iunius118.cc3dprojector.peripheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.github.iunius118.cc3dprojector.CC3DProjector;
import net.github.iunius118.cc3dprojector.block.ThreeDProjectorBlock;
import net.github.iunius118.cc3dprojector.tileentity.ThreeDProjectorTileEntity;
import net.github.iunius118.cc3dprojector.upgrade.ThreeDProjectorTurtle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreeDProjectorPeripheral implements IPeripheral {
    public static final String TAG_MODEL = "model";
    public static final String TAG_IS_RAW = "isRaw";

    private final PeripheralType type;
    private final ThreeDProjectorTileEntity tileentity;
    private final ITurtleAccess turtleAccess;
    private final TurtleSide turtleSide;


    public ThreeDProjectorPeripheral(ThreeDProjectorTileEntity tile) {
        type = net.github.iunius118.cc3dprojector.peripheral.PeripheralType.TILEENTITY;
        tileentity = tile;
        turtleAccess = null;
        turtleSide = TurtleSide.Left;
    }

    public ThreeDProjectorPeripheral(ITurtleAccess turtle, TurtleSide side) {
        type = net.github.iunius118.cc3dprojector.peripheral.PeripheralType.UPGRADE;
        tileentity = null;
        turtleAccess = turtle;
        turtleSide = side;
    }

    @Nonnull
    @Override
    public String getType() {
        return "3d_projector";
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[] {"write", "read", "clear"};
    }

    @Nullable
    @Override
    public Object[] callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull Object[] arguments) throws LuaException {
        switch (method) {
            case 0: 	// write( table )
                if (arguments.length == 0 || !(arguments[0] instanceof Map)) {
                    throw new LuaException("Expected table");
                } else {
                    ModelProgramProcessor processor = new ModelProgramProcessor();
                    byte[] buf = processor.compile((Map)arguments[0]);
                    setModelProgram(buf, computer.getID());
                }

                break;

            case 1: 	// read() -> table
                byte[] buf = getModelProgram();

                if (buf != null) {
                    ModelProgramProcessor processor = new ModelProgramProcessor();
                    List<Map<Integer, Object>> list = processor.decompile(buf);
                    Map<Integer, Map<Integer, Object>> map = new HashMap<>();

                    for (int i = 0; i < list.size(); i++) {
                        map.put(i + 1, list.get(i));
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
    public boolean equals(IPeripheral other) {
        if (other instanceof ThreeDProjectorPeripheral) {
            ThreeDProjectorPeripheral old = (ThreeDProjectorPeripheral)other;

            if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.UPGRADE && this.turtleAccess != null && old.turtleAccess != null) {
                BlockPos oldPos = old.turtleAccess.getPosition();
                return this.turtleAccess.getPosition().equals(oldPos) && (this.turtleSide == old.turtleSide);
            } else {
                return old == this;
            }
        }

        return false;
    }

    /**
     * Write model binary code to NBT and Change peripheral state
     *  */
    private void setModelProgram(byte[] buf, int computerID) {
        NBTTagCompound tag = null;

        if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.TILEENTITY && tileentity != null) {
            tag = tileentity.getTileData();
        } else if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.UPGRADE && turtleAccess != null) {
            tag = turtleAccess.getUpgradeNBTData(turtleSide);
        }

        if (tag != null) {
            tag.setBoolean(TAG_IS_RAW, true);

            if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.TILEENTITY) {
                IBlockState state = CC3DProjector.BLOCKS.block_3d_projector.getDefaultState();
                tileentity.getWorld().setBlockState(tileentity.getPos(), state.withProperty(ThreeDProjectorBlock.IS_ON, Boolean.FALSE));
                tileentity.getWorld().setBlockState(tileentity.getPos(), state.withProperty(ThreeDProjectorBlock.IS_ON, Boolean.TRUE));
                tag.setByteArray(TAG_MODEL, buf);
            } else {    // type == PeripheralType.UPGRADE
                tag.setByteArray(TAG_MODEL, buf);
                tag.setBoolean(ThreeDProjectorTurtle.TAG_IS_ON, true);
                tag.setInteger(ThreeDProjectorTurtle.TAG_COMPUTER_ID, computerID);
                turtleAccess.updateUpgradeNBTData(turtleSide);
            }
        }
    }

    /**
     * Read model binary code from NBT
     *  */
    private byte[] getModelProgram() {
        byte[] buf;
        NBTTagCompound tag = null;

        if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.TILEENTITY && tileentity != null) {
            tag = tileentity.getTileData();
        } else if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.UPGRADE && turtleAccess != null) {
            tag = turtleAccess.getUpgradeNBTData(turtleSide);
        }

        if (tag != null) {
            buf = tag.getByteArray(TAG_MODEL);
            if (buf.length > 0) {
                boolean isRaw = tag.getBoolean(ThreeDProjectorPeripheral.TAG_IS_RAW);

                if (!isRaw) {
                    buf = ModelProgramProcessor.inflate(buf);
                }

                return buf;
            }
        }

        return null;
    }

    /**
     * Clear model binary code in NBT and Change peripheral state
     *  */
    private void clearModelProgram() {
        NBTTagCompound tag = null;

        if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.TILEENTITY && tileentity != null) {
            tag = tileentity.getTileData();
        } else if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.UPGRADE && turtleAccess != null) {
            tag = turtleAccess.getUpgradeNBTData(turtleSide);
        }

        if (tag != null) {
            tag.removeTag(TAG_MODEL);

            if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.TILEENTITY) {
                IBlockState state = CC3DProjector.BLOCKS.block_3d_projector.getDefaultState();
                tileentity.getWorld().setBlockState(tileentity.getPos(), state.withProperty(ThreeDProjectorBlock.IS_ON, Boolean.FALSE));
            } else {    // type == PeripheralType.UPGRADE
                tag.setBoolean(ThreeDProjectorTurtle.TAG_IS_ON, false);
                turtleAccess.updateUpgradeNBTData(turtleSide);
            }
        }
    }

    public static class Identification {
        private final PeripheralType type;
        private final BlockPos pos;
        private final int id;
        private final TurtleSide turtleSide;

        public Identification(ThreeDProjectorTileEntity tile) {
            type = net.github.iunius118.cc3dprojector.peripheral.PeripheralType.TILEENTITY;
            pos = tile.getPos();
            id = -1;
            turtleSide = null;
        }

        public Identification(int computerID, TurtleSide side) {
            type = net.github.iunius118.cc3dprojector.peripheral.PeripheralType.UPGRADE;
            pos = null;
            id = computerID;
            turtleSide = side;
        }

        public PeripheralType getType() {
            return type;
        }

        public BlockPos getPos() {
            return pos;
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

            if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.TILEENTITY) {
                return pos != null && pos.equals(other.pos);
            } else if (type == net.github.iunius118.cc3dprojector.peripheral.PeripheralType.UPGRADE) {
                return id > -1 && id == other.id && turtleSide != null && turtleSide == other.turtleSide;
            }

            return false;
        }

    }
}
