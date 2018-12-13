package iunius118.mods.cc3dprojector.upgrade;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import iunius118.mods.cc3dprojector.CC3DProjector;
import iunius118.mods.cc3dprojector.peripheral.ModelProgramProcessor;
import iunius118.mods.cc3dprojector.peripheral.Peripheral3DProjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Map;

public class Turtle3DProjector implements ITurtleUpgrade {
    public static final String TAG_COMPUTER_ID = "ID";
    public static final String TAG_IS_ON = "isOn";
    public static final String TAG_IS_MODEL_DECOMPILED = "isDec";

    @SideOnly(Side.CLIENT)
    private ModelResourceLocation modelLeftOff;
    @SideOnly(Side.CLIENT)
    private ModelResourceLocation modelLeftOn;
    @SideOnly(Side.CLIENT)
    private ModelResourceLocation modelRightOff;
    @SideOnly(Side.CLIENT)
    private ModelResourceLocation modelRightOn;

    public Turtle3DProjector() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            modelLeftOff  = new ModelResourceLocation(new ResourceLocation(CC3DProjector.MOD_ID, "upgrade/3dprojector_left_off"), "inventory");
            modelLeftOn  = new ModelResourceLocation(new ResourceLocation(CC3DProjector.MOD_ID, "upgrade/3dprojector_left_on"), "inventory");
            modelRightOff = new ModelResourceLocation(new ResourceLocation(CC3DProjector.MOD_ID, "upgrade/3dprojector_right_off"), "inventory");
            modelRightOn = new ModelResourceLocation(new ResourceLocation(CC3DProjector.MOD_ID, "upgrade/3dprojector_right_on"), "inventory");
        }
    }

    @Override
    @Nonnull
    public ResourceLocation getUpgradeID() {
        return new ResourceLocation(CC3DProjector.MOD_ID, CC3DProjector.NAME_BLOCK_3D_PROJECTOR);
    }

    @Override
    public int getLegacyUpgradeID() {
        return -1;
    }

    @Override
    @Nonnull
    public String getUnlocalisedAdjective() {
        return "upgrade." + CC3DProjector.MOD_ID + "." + CC3DProjector.NAME_BLOCK_3D_PROJECTOR + ".adjective";
    }

    @Override
    @Nonnull
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.Peripheral;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingItem() {
        return new ItemStack(CC3DProjector.BLOCKS.block_3d_projector);
    }

    @Override
    @Nullable
    public IPeripheral createPeripheral(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side) {
        return new Peripheral3DProjector(turtle, side);
    }

    @Override
    @Nonnull
    public TurtleCommandResult useTool(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side, @Nonnull TurtleVerb verb, @Nonnull EnumFacing direction) {
        return TurtleCommandResult.failure();
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nonnull
    public Pair<IBakedModel, Matrix4f> getModel(@Nullable ITurtleAccess turtle, @Nonnull TurtleSide side) {
        Minecraft mc = Minecraft.getMinecraft();
        ModelManager modelManager = mc.getRenderItem().getItemModelMesher().getModelManager();
        NBTTagCompound tag = (turtle != null) ? turtle.getUpgradeNBTData(side) : null;

        if (tag != null && tag.getBoolean(Turtle3DProjector.TAG_IS_ON)) {
            List<Map<Integer, Object>> model = null;
            int id = tag.getInteger(TAG_COMPUTER_ID);
            Peripheral3DProjector.Identification projectorID = new Peripheral3DProjector.Identification(id, side);

            if (!tag.getBoolean(Turtle3DProjector.TAG_IS_MODEL_DECOMPILED)) {
                // Read 3D model from NBT
                boolean isRaw = tag.getBoolean(Peripheral3DProjector.TAG_IS_RAW);
                byte[] buf = tag.getByteArray(Peripheral3DProjector.TAG_MODEL);

                if (buf.length > 0) {
                    // Decompile 3D model
                    ModelProgramProcessor processor = new ModelProgramProcessor();

                    if (!isRaw) {
                        buf = ModelProgramProcessor.inflate(buf);
                    }

                    try {
                        model = processor.decompile(buf);
                    } catch (LuaException ignored) {
                    }

                    // Queue 3D model to render
                    if (model != null) {
                        CC3DProjector.queue3DModel.put(projectorID, Pair.of(model, turtle));
                    }

                    tag.setBoolean(TAG_IS_MODEL_DECOMPILED, true);
                }
            } else {
                // if model is queued
                Pair<List<Map<Integer, Object>>, ITurtleAccess>value = CC3DProjector.queue3DModel.get(projectorID);

                if (value != null) { // and queued model is available
                    // Activate queued model
                    CC3DProjector.queue3DModel.put(projectorID, Pair.of(value.getKey(), turtle));
                } else {
                    tag.setBoolean(TAG_IS_MODEL_DECOMPILED, false);
                }
            }

            // Return upgrade model (ON)
            if (side == TurtleSide.Left) {
                return Pair.of(modelManager.getModel(modelLeftOn), null);
            } else {
                return Pair.of(modelManager.getModel(modelRightOn), null);
            }

        } else {
            // Return upgrade model (OFF)
            if (side == TurtleSide.Left) {
                return Pair.of(modelManager.getModel(modelLeftOff), null);
            } else {
                return Pair.of(modelManager.getModel(modelRightOff), null);
            }
        }
    }
}
