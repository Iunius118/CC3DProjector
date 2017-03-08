package iunius118.mods.cc3dprojector.tileentity;

import iunius118.mods.cc3dprojector.peripheral.Peripheral3DProjector;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntity3DProjector extends TileEntity implements ITickable {

	public TileEntity3DProjector() {

	}

	public Peripheral3DProjector getPeripheral(){
		return new Peripheral3DProjector(this);
	}

	@Override
	public void update() {

	}

}
