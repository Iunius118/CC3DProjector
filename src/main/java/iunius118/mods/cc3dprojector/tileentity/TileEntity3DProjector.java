package iunius118.mods.cc3dprojector.tileentity;

import java.util.List;
import java.util.Map;

import dan200.computercraft.api.lua.LuaException;
import iunius118.mods.cc3dprojector.peripheral.ModelProgramProcessor;
import iunius118.mods.cc3dprojector.peripheral.Peripheral3DProjector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntity3DProjector extends TileEntity implements ITickable {

	public List<Map<Integer, Object>> model = null;

	public TileEntity3DProjector() {

	}

	public Peripheral3DProjector getPeripheral(){
		return new Peripheral3DProjector(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		NBTTagCompound tag = this.getTileData();
		byte[] buf = tag.getByteArray(Peripheral3DProjector.TAG_MODEL);

		if (buf.length > 0) {
			ModelProgramProcessor processor = new ModelProgramProcessor();
			byte[] buf2 = processor.inflate(buf);
			try {
				model = processor.decompile(buf2);
			} catch (LuaException e) {
				model = null;
			}
		} else {
			model = null;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
	}

	@Override
	public Packet getDescriptionPacket() {
		System.out.println("getDescriptionPacket");
		NBTTagCompound compound = new NBTTagCompound();
		this.writeToNBT(compound);
		return new S35PacketUpdateTileEntity(this.pos, this.getBlockMetadata(), compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		System.out.println("onDataPacket");
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public void update() {

	}

}
