package iunius118.mods.cc3dprojector;

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
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(
    modid = CC3DProjector.MOD_ID,
    name = CC3DProjector.MOD_NAME,
    version = CC3DProjector.MOD_VERSION,
    dependencies = CC3DProjector.MOD_DEPENDENCIES
)
@Mod.EventBusSubscriber
public class CC3DProjector {
    public static final String MOD_ID = "cc3dprojector";
    public static final String MOD_NAME = "CC3DProjector";
    public static final String MOD_VERSION = "1.12-1.0.0.0";
    public static final String MOD_DEPENDENCIES = "required-after:computercraft";

    public static Logger logger;

    @Mod.Instance(MOD_ID)
    public static CC3DProjector INSTANCE;

    @SidedProxy
    public static CommonProxy proxy;

    public static final String NAME_BLOCK_3D_PROJECTOR = "3dprojector";

    public static final Turtle3DProjector turtle3DProjector = new Turtle3DProjector();

    @SideOnly(Side.CLIENT)
    public static Map<Peripheral3DProjector.Identification, Pair<List<Map<Integer, Object>>, ITurtleAccess>> queue3DModel = new HashMap<>();

    @ObjectHolder(MOD_ID)
    public static class BLOCKS
    {
        @ObjectHolder(NAME_BLOCK_3D_PROJECTOR)
        public static final Block block_3d_projector = null;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new Block3DProjector().setRegistryName(NAME_BLOCK_3D_PROJECTOR).setUnlocalizedName(MOD_ID + "." + NAME_BLOCK_3D_PROJECTOR)
        );

        GameRegistry.registerTileEntity(TileEntity3DProjector.class, MOD_ID + ":" + NAME_BLOCK_3D_PROJECTOR);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemBlock(BLOCKS.block_3d_projector).setRegistryName(NAME_BLOCK_3D_PROJECTOR)
        );
    }

    public static class CommonProxy {
        public void preInit(FMLPreInitializationEvent event) {
            registerPeripherals();
        }

        public void registerPeripherals() {
            ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
            ComputerCraftAPI.registerTurtleUpgrade(turtle3DProjector);
        }
    }

    @SideOnly(Side.SERVER)
    public static class ServerProxy extends CommonProxy {

    }

    @SideOnly(Side.CLIENT)
    public static class ClientProxy extends CommonProxy {
        @Override
        public void preInit(FMLPreInitializationEvent event) {
            super.preInit(event);

            MinecraftForge.EVENT_BUS.register(this);
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntity3DProjector.class, new RendererTileEntity3DProjector());
        }

        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

        }

        @SubscribeEvent
        public void registerModels(ModelRegistryEvent event) {
            OBJLoader.INSTANCE.addDomain(MOD_ID);

            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCKS.block_3d_projector), 0, new ModelResourceLocation(BLOCKS.block_3d_projector.getRegistryName(), "inventory"));
        }

        @SubscribeEvent
        public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
            event.getMap().registerSprite(new ResourceLocation(CC3DProjector.MOD_ID, "upgrades/3dprojector_side"));
            event.getMap().registerSprite(new ResourceLocation(CC3DProjector.MOD_ID, "upgrades/3dprojector_top_off"));
            event.getMap().registerSprite(new ResourceLocation(CC3DProjector.MOD_ID, "upgrades/3dprojector_top_on"));
        }

        @SubscribeEvent
        public void onModelBakeEvent(ModelBakeEvent event) {
            loadModel(event, new ResourceLocation(CC3DProjector.MOD_ID, "upgrade/3dprojector_left_off"));
            loadModel(event, new ResourceLocation(CC3DProjector.MOD_ID, "upgrade/3dprojector_left_on"));
            loadModel(event, new ResourceLocation(CC3DProjector.MOD_ID, "upgrade/3dprojector_right_off"));
            loadModel(event, new ResourceLocation(CC3DProjector.MOD_ID, "upgrade/3dprojector_right_on"));
        }

        private void loadModel(ModelBakeEvent event, ResourceLocation location) {
            try {
                IModel model = ModelLoaderRegistry.getModel(location);
                IBakedModel bakedModel = model.bake(model.getDefaultState(),
                        DefaultVertexFormats.ITEM,
                        ModelLoader.defaultTextureGetter());
                ModelResourceLocation modelLocation = new ModelResourceLocation(location, "inventory");
                event.getModelRegistry().putObject(modelLocation, bakedModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SubscribeEvent
        public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
            if (queue3DModel.size() < 1) {
                return;
            }

            EntityPlayer player = Minecraft.getMinecraft().player;
            float partialTicks = event.getPartialTicks();
            double playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            double playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

            Map<Peripheral3DProjector.Identification, Pair<List<Map<Integer, Object>>, ITurtleAccess>> queue3DModelCopy = new HashMap<>();	 // Model cache for Turtle Upgrades

            // Render models from queue
            for(Map.Entry<Peripheral3DProjector.Identification, Pair<List<Map<Integer, Object>>, ITurtleAccess>> entry : queue3DModel.entrySet()) {
                Peripheral3DProjector.Identification id = entry.getKey();
                ITurtleAccess turtle = entry.getValue().getValue();

                if (id.getType()== PeripheralType.UPGRADE && turtle == null) {
                    continue;
                }

                // System.out.println(id.type + " pos:" + id.pos +  " id:" + id.id + " side:" + id.turtleSide + " ta:" + turtle);

                List<Map<Integer, Object>> model = entry.getValue().getKey();
                Vec3d pos;
                float yaw = 0;

                // Get visual position
                if (turtle == null) {
                    pos = new Vec3d(id.getPos()).subtract(playerX, playerY, playerZ);
                } else {
                    pos = turtle.getVisualPosition(partialTicks).subtract(playerX, playerY, playerZ);
                    yaw = turtle.getVisualYaw(partialTicks);
                }

                Renderer3DModel.doRender(event, pos, yaw, model, turtle != null);	// Render model

                if (id.getType() == PeripheralType.UPGRADE) {	// Remain models of active Turtle Upgrades
                    queue3DModelCopy.put(id, Pair.of(model, null));
                }
            }

            // Flip model queue to remaining models of active Turtle Upgrades
            queue3DModel = queue3DModelCopy;
        }
    }
}
