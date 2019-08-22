package net.github.iunius118.cc3dprojector.tileentity;

import dan200.computercraft.api.lua.LuaException;
import net.github.iunius118.cc3dprojector.peripheral.ModelProgramProcessor;
import net.github.iunius118.cc3dprojector.peripheral.ThreeDProjectorPeripheral;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ThreeDProjectorTileEntity extends TileEntity implements ITickable {
    public List<Map<Integer, Object>> model = null;    // Model cache

    public ThreeDProjectorPeripheral getPeripheral() {
        return new ThreeDProjectorPeripheral(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        // Read model from NBT
        NBTTagCompound tag = this.getTileData();
        boolean isRaw = tag.getBoolean(ThreeDProjectorPeripheral.TAG_IS_RAW);
        byte[] buf = tag.getByteArray(ThreeDProjectorPeripheral.TAG_MODEL);

        if (buf.length > 0) {
            // Decompile and Cache model
            ModelProgramProcessor processor = new ModelProgramProcessor();

            if (!isRaw) {
                buf = ModelProgramProcessor.inflate(buf);
            }

            try {
                model = processor.decompile(buf);
            } catch (LuaException e) {
                model = null;
            }
        } else {
            model = null;
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        // CC3DProjector.logger.info("getDescriptionPacket");
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        // CC3DProjector.logger.info("onDataPacket");
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void update() {

    }
}
