package net.github.iunius118.cc3dprojector.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.github.iunius118.cc3dprojector.tileentity.ThreeDProjectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PeripheralProvider implements IPeripheralProvider {
    @Nullable
    @Override
    public IPeripheral getPeripheral(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof ThreeDProjectorTileEntity) {
            return ((ThreeDProjectorTileEntity)tile).getPeripheral();
        }

        return null;
    }
}
