package me.aleksilassila.litematica.printer.mixin.printer.mc;

import me.aleksilassila.litematica.printer.config.Configs;
import me.aleksilassila.litematica.printer.utils.minecraft.BeaconEffectSync;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMixin {
    //#if MC >= 11802
    @Shadow
    @Final
    private List<?> beaconButtons;
    //#endif

    @Inject(method = "init", at = @At("HEAD"))
    private void litematica_printer$syncBeaconEffects(CallbackInfo ci) {
        BeaconEffectSync.syncFromConfig();
    }

    //#if MC >= 11802
    @Inject(method = "updateButtons", at = @At("TAIL"))
    private void litematica_printer$unlockBeaconEffects(CallbackInfo ci) {
        if (!Configs.Special.UNLOCK_BEACON_EFFECTS.getBooleanValue()) {
            return;
        }
        for (Object button : this.beaconButtons) {
            ((BeaconButtonAccessor) button).litematica_printer$updateStatus(4);
        }
    }
    //#else
    // 1.15.2 creates power buttons in tick() from the menu's level count.
    // Keep the confirm button's payment check untouched.
    //$$ @org.spongepowered.asm.mixin.injection.Redirect(method = "tick", at = @At(value = "INVOKE",
    //$$         target = "Lnet/minecraft/world/inventory/BeaconMenu;getLevels()I"))
    //$$ private int printer$unlockedLevels(net.minecraft.world.inventory.BeaconMenu menu) {
    //$$     return Configs.Special.UNLOCK_BEACON_EFFECTS.getBooleanValue() ? 4 : menu.getLevels();
    //$$ }
    //#endif
}
