package iunius118.mods.cc3dprojector;

import java.io.IOException;

import com.google.common.base.Function;

import dan200.computercraft.api.ComputerCraftAPI;
import iunius118.mods.cc3dprojector.block.Block3DProjector;
import iunius118.mods.cc3dprojector.peripheral.PeripheralProvider;
import iunius118.mods.cc3dprojector.tileentity.TileEntity3DProjector;
import iunius118.mods.cc3dprojector.upgrade.Turtle3DProjector;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
	modid = CC3DProjector.MOD_ID,
	name = CC3DProjector.MOD_NAME,
	version = CC3DProjector.MOD_VERSION,
	dependencies = CC3DProjector.MOD_DEPENDENCIES
)
public class CC3DProjector {

	public static final String MOD_ID = "cc3dprojector";
	public static final String MOD_NAME = "CC3DProjector";
	public static final String MOD_VERSION = "0.0.1";
	public static final String MOD_DEPENDENCIES = "after:ComputerCraft";

	public static Block block3DProjector;
	public static final String NAME_BLOCK_3D_PROJECTOR = "3dprojector";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		block3DProjector = new Block3DProjector().setUnlocalizedName(NAME_BLOCK_3D_PROJECTOR);
		GameRegistry.registerBlock(block3DProjector, NAME_BLOCK_3D_PROJECTOR);
		GameRegistry.registerTileEntity(TileEntity3DProjector.class, NAME_BLOCK_3D_PROJECTOR);

		ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
		ComputerCraftAPI.registerTurtleUpgrade(new Turtle3DProjector());

		if (event.getSide().isClient()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block3DProjector), 0, new ModelResourceLocation(MOD_ID + ":" + NAME_BLOCK_3D_PROJECTOR, "inventory"));
			MinecraftForge.EVENT_BUS.register(this);
		}
	}

	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		event.map.registerSprite(new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_side"));
		event.map.registerSprite(new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_top"));
	}

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		ResourceLocation modelLeft  = new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_left");
		ResourceLocation modelRight = new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_right");
		ResourceLocation modeltop = new ResourceLocation(this.MOD_ID, "upgrade/3dprojector_top");

		loadModel(event, modelLeft);
		loadModel(event, modelRight);
		loadModel(event, modeltop);
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

}
