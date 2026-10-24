package me.aleksilassila.litematica.printer.config;

//#if MC >= 11600
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
//#else
//$$ import io.github.prospector.modmenu.api.ModMenuApi;
//$$ import net.minecraft.client.gui.screens.Screen;
//$$ import java.util.function.Function;
//#endif
import me.aleksilassila.litematica.printer.gui.ConfigUi;

public class ModMenu implements ModMenuApi {
    @Override
    //#if MC >= 11600
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
    //#else
    //$$ public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    //#endif
        return ConfigUi::new;
    }
    //#if MC < 11600
    //$$ @Override
    //$$ public String getModId() { return "litematica-printer"; }
    //#endif
}
