package iunius118.mods.cc3dprojector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleAccess;
import iunius118.mods.cc3dprojector.block.Block3DProjector;
import iunius118.mods.cc3dprojector.client.renderer.Renderer3DModel;
import iunius118.mods.cc3dprojector.client.renderer.RendererTileEntity3DProjector;
import iunius118.mods.cc3dprojector.peripheral.Peripheral3DProjector;
import iunius118.mods.cc3dprojector.peripheral.PeripheralProvider;
import iunius118.mods.cc3dprojector.peripheral.PeripheralType;
import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import iunius118.mods.cc3dprojector.upgrade.Turtle3DProjector;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
	modid = CC3DProjector.MOD_ID,
	name = CC3DProjector.MOD_NAME,
	version = CC3DProjector.MOD_VERSION,
	dependencies = CC3DProjector.MOD_DEPENDENCIES
)
public class CC3DProjector {

	public static final String MOD_ID = "cc3dprojector";
	public static final String MOD_NAME = "CC3DProjector";
	public static final String MOD_VERSION = "%modVersion%";
	public static final String MOD_DEPENDENCIES = "after:ComputerCraft";

	public static Block block3DProjector;
	public static final String NAME_BLOCK_3D_PROJECTOR = "3dprojector";

	public static Turtle3DProjector turtle3DProjector = new Turtle3DProjector();

	@SideOnly(Side.CLIENT)
	public static Map<Peripheral3DProjector.Identification, Pair<List<Map<Integer, Object>>, ITurtleAccess>> queue3DModel = new HashMap();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		block3DProjector = new Block3DProjector().setUnlocalizedName(NAME_BLOCK_3D_PROJECTOR);
		GameRegistry.registerBlock(block3DProjector, NAME_BLOCK_3D_PROJECTOR);
		GameRegistry.registerTileEntity(TileEntity3DProjector.class, NAME_BLOCK_3D_PROJECTOR);

		ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
		ComputerCraftAPI.registerTurtleUpgrade(turtle3DProjector);

		if (event.getSide().isClient()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block3DProjector), 0, new ModelResourceLocation(MOD_ID + ":" + NAME_BLOCK_3D_PROJECTOR, "inventory"));
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntity3DProjector.class, new RendererTileEntity3DProjector());
			MinecraftForge.EVENT_BUS.register(this);
		}
	}

	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		event.map.registerSprite(new ResourceLocation(this.MOD_ID, "upgrades/3dprojector_side"));
		event.map.registerSprite(new ResourceLocation(this.MOD_ID, "upgrades/3dprojector_top_off"));
		event.map.registerSprite(new ResourceLocation(this.MOD_ID, "upgrades/3dprojector_top_on"));
	}

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		loadModel(event, new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_left_off"));
		loadModel(event, new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_left_on"));
		loadModel(event, new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_right_off"));
		loadModel(event, new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_right_on"));
	}

	private void loadModel(ModelBakeEvent event, ResourceLocation location) {
		try {
			IModel model = event.modelLoader.getModel(location);
			IBakedModel bakedModel = model.bake(model.getDefaultState(),
					DefaultVertexFormats.ITEM,
					new Function<ResourceLocation, TextureAtlasSprite>() {

						@Override
						public TextureAtlasSprite apply(ResourceLocation location) {
							Minecraft mc = Minecraft.getMinecraft();
							return mc.getTextureMapBlocks().getAtlasSprite(location.toString());
						}

					});
			ModelResourceLocation modelLocation = new ModelResourceLocation(location, "inventory");
			event.modelRegistry.putObject(modelLocation, bakedModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (queue3DModel.size() < 1) {
			return;
		}

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.partialTicks;
		double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.partialTicks;
		double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.partialTicks;

		Map<Peripheral3DProjector.Identification, Pair<List<Map<Integer, Object>>, ITurtleAccess>> queue3DModelCopy = new HashMap();

		for(Map.Entry<Peripheral3DProjector.Identification, Pair<List<Map<Integer, Object>>, ITurtleAccess>> entry : queue3DModel.entrySet()) {
			Peripheral3DProjector.Identification id = entry.getKey();
			ITurtleAccess turtle = entry.getValue().getValue();

			if (id.type == PeripheralType.UPGRADE && turtle == null) {
				return;
			}

			// System.out.println(id.type + " pos:" + id.pos +  " id:" + id.id + " side:" + id.turtleSide + " ta:" + turtle);

			List<Map<Integer, Object>> model = entry.getValue().getKey();
			Vec3 pos;
			float yaw = 0;

			if (turtle == null) {
				pos = new Vec3(id.pos).subtract(playerX, playerY, playerZ);
			} else {
				pos = turtle.getVisualPosition(event.partialTicks).subtract(playerX + 0.5D, playerY + 0.5D, playerZ + 0.5D);
				yaw = turtle.getVisualYaw(event.partialTicks);
			}

			Renderer3DModel.doRender(event, pos, yaw, model);

			if (id.type == PeripheralType.UPGRADE) {
				queue3DModelCopy.put(id, Pair.<List<Map<Integer, Object>>, ITurtleAccess>of(model, null));
			}
		}

		queue3DModel = queue3DModelCopy;
	}

}
