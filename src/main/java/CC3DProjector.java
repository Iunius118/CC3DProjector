import net.minecraftforge.fml.common.Mod;

@Mod(
        modid = CC3DProjector.MOD_ID,
        name = CC3DProjector.MOD_NAME,
        version = CC3DProjector.MOD_VERSION,
        dependencies = CC3DProjector.MOD_DEPENDENCIES
)
public class CC3DProjector {
    public static final String MOD_ID = "cc3dprojector";
    public static final String MOD_NAME = "CC3DProjector";
    public static final String MOD_VERSION = "1.12-1.0.0.0";
    public static final String MOD_DEPENDENCIES = "required-after:computercraft";
}
