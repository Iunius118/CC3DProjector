package iunius118.mods.cc3dprojector.client.renderer;

import org.apache.commons.lang3.tuple.Pair;

import dan200.computercraft.api.turtle.ITurtleAccess;
import iunius118.mods.cc3dprojector.CC3DProjector;
import iunius118.mods.cc3dprojector.peripheral.Peripheral3DProjector;
import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RendererTileEntity3DProjector<T> extends TileEntitySpecialRenderer<TileEntity3DProjector> {

	@Override
	public void renderTileEntityAt(TileEntity3DProjector te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te != null && te.model != null) {
			CC3DProjector.queue3DModel.put(new Peripheral3DProjector.Identification(te), Pair.of(te.model, (ITurtleAccess)null));
		}
	}

}
