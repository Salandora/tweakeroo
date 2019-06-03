package fi.dy.masa.tweakeroo;

import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.tweakeroo.event.WorldLoadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.rift.listener.client.ClientTickable;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.event.InputHandler;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import net.minecraft.client.Minecraft;

public class Tweakeroo implements ClientTickable, InitializationListener
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public static int renderCountItems;
    public static int renderCountXPOrbs;

    @Override
    public void onInitialization()
    {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.tweakeroo.json");

        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }

    @Override
    public void clientTick(Minecraft mc)
    {
        PlacementTweaks.onTick(mc);
        MiscTweaks.onTick(mc);

        // Reset the counters after rendering each frame
        renderCountItems = 0;
        renderCountXPOrbs = 0;
    }

    private static class InitHandler implements IInitializationHandler
    {
        @Override
        public void registerModHandlers()
        {
            ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());

            InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
            InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
            InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.getInstance());

            IRenderer renderer = new RenderHandler();
            RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
            RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);

            WorldLoadListener listener = new WorldLoadListener();
            WorldLoadHandler.getInstance().registerWorldLoadPreHandler(listener);

            Callbacks.init(Minecraft.getInstance());
        }
    }
}
