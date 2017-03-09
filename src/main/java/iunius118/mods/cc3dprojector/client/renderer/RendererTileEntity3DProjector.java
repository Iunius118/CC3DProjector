package iunius118.mods.cc3dprojector.client.renderer;

import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RendererTileEntity3DProjector<T> extends TileEntitySpecialRenderer<TileEntity3DProjector> {

	@Override
	public void renderTileEntityAt(TileEntity3DProjector te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te == null) {
			return;
		}
	}

}
