package iunius118.mods.cc3dprojector.upgrade;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;
import iunius118.mods.cc3dprojector.CC3DProjector;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class Turtle3DProjector implements ITurtleUpgrade {

	@Override
	public ResourceLocation getUpgradeID() {
		return new ResourceLocation(CC3DProjector.MOD_ID, CC3DProjector.NAME_BLOCK_3D_PROJECTOR);
	}

	@Override
	public int getLegacyUpgradeID() {
		return -1;
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "upgrade." + CC3DProjector.NAME_BLOCK_3D_PROJECTOR + ".adjective";
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(CC3DProjector.block3DProjector);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return null;
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, EnumFacing direction) {
		return null;
	}

	@Override
	public Pair<IBakedModel, Matrix4f> getModel(ITurtleAccess turtle, TurtleSide side) {
		return null;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {

	}

}
