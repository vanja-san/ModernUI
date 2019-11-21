package icyllis.modern.core;

import icyllis.modern.api.ModernUIAPI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod(ModernUI.MODID)
public class ModernUI {

    public static final String MODID = "modernui";

    public static final Logger logger = LogManager.getLogger("ModernUI");
    public static final Marker MARKER = MarkerManager.getMarker("MAIN");

    public ModernUI() {
        ModernUI.logger.info(MARKER, "{} has been initialized", ModernUIAPI.INSTANCE.getDeclaringClass().getSimpleName());
        ScreenManager.INSTANCE.generateUITypes();
    }

}