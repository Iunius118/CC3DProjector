package net.github.iunius118.cc3dprojector.client.renderer;

import net.github.iunius118.cc3dprojector.CC3DProjector;
import net.github.iunius118.cc3dprojector.peripheral.ThreeDProjectorPeripheral;
import net.github.iunius118.cc3dprojector.tileentity.ThreeDProjectorTileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.apache.commons.lang3.tuple.Pair;

public class ThreeDProjectorTileEntityRenderer<T> extends TileEntitySpecialRenderer<ThreeDProjectorTileEntity> {
    @Override
    public void render(ThreeDProjectorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        if (te.model != null) {
            CC3DProjector.queue3DModel.put(new ThreeDProjectorPeripheral.Identification(te), Pair.of(te.model, null));
        }
    }
}
