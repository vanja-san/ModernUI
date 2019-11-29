package icyllis.modern.system;

import icyllis.modern.api.ModernUIApi;
import icyllis.modern.ui.font.TrueTypeRenderer;
import icyllis.modern.ui.master.GlobalAnimationManager;
import icyllis.modern.ui.test.ContainerProvider;
import icyllis.modern.ui.test.GuiTest;
import icyllis.modern.ui.test.UILibs;
import icyllis.modern.ui.test.ContainerTest;
import icyllis.modern.vanilla.GuiChatBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class EventsHandler {

    @SubscribeEvent
    public static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if(!event.getPlayer().getEntityWorld().isRemote && event.getItemStack().getItem().equals(Items.DIAMOND)) {
            ModernUIApi.INSTANCE.network().openGUI((ServerPlayerEntity) event.getPlayer(), new ContainerProvider(), new BlockPos(-155,82,-121));
        }
    }

    @SubscribeEvent
    public static void onContainerClosed(PlayerContainerEvent.Close event) {
        //ModernUI.logger.info("Container closed: {}", event.getContainer());
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class ClientEventHandler {

        @SubscribeEvent
        public static void onRenderTick(TickEvent.RenderTickEvent event) {
            if(event.phase == TickEvent.Phase.START) {
                GlobalAnimationManager.INSTANCE.tick(event.renderTickTime);
                TrueTypeRenderer.INSTANCE.init();
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if(event.phase == TickEvent.Phase.START) {
                GlobalAnimationManager.INSTANCE.tick();
                if (Minecraft.getInstance().gameSettings.keyBindDrop.isPressed()) {
                    Minecraft.getInstance().displayGuiScreen(new GuiChatBar());
                }
            }
        }

        @SubscribeEvent
        public static void onGuiOpen(GuiOpenEvent event) {
            if(event.getGui() instanceof ChatScreen)
                ModernUI.LOGGER.info(event.getGui().getClass().getSimpleName());
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModSetupHandler {

        @SubscribeEvent
        public static void setupCommon(FMLCommonSetupEvent event) {

        }

    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientSetupHandler {

        @SubscribeEvent
        public static void setupClient(FMLClientSetupEvent event) {
            ModernUIApi.INSTANCE.gui().registerContainerGui(UILibs.TEST_CONTAINER_SCREEN, ContainerTest::new, GuiTest::new);
        }
    }
}