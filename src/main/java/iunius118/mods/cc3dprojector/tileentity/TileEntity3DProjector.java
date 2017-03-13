package iunius118.mods.cc3dprojector.tileentity;

import iunius118.mods.cc3dprojector.peripheral.Peripheral3DProjector;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntity3DProjector extends TileEntity implements ITickable {

	public TileEntity3DProjector() {

	}

	public Peripheral3DProjector getPeripheral(){
		return new Peripheral3DProjector(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
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
	public void update() {

	}

}
