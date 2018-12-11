package iunius118.mods.cc3dprojector;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
@EventBusSubscriber
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

    @SideOnly(Side.CLIENT)
    public static Map<Peripheral3DProjector.Identification, Pair<List<Map<Integer, Object>>, ITurtleAccess>> queue3DModel = new HashMap();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @ObjectHolder(MOD_ID)
    public static class BLOCKS
    {
        @ObjectHolder(NAME_BLOCK_3D_PROJECTOR)
        public static final Block block_3d_projector = null;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {

    }

    public static class CommonProxy {
        public void preInit(FMLPreInitializationEvent event) {

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
        }

        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

        }

        @SubscribeEvent
        public void registerModels(ModelRegistryEvent event) {

        }

        @SubscribeEvent
        public void onModelBakeEvent(ModelBakeEvent event) {

        }

        @SubscribeEvent
        public void onTextureStitchEvent(TextureStitchEvent.Pre event) {

        }
    }
}
