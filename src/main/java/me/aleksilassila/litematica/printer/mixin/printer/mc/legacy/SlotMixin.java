//#if MC < 11802
//$$ package me.aleksilassila.litematica.printer.mixin.printer.mc.legacy;
//$$
//$$ import me.aleksilassila.litematica.printer.compat.legacy.LegacyApi;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$
//$$ @Mixin(net.minecraft.world.inventory.Slot.class)
//$$ public abstract class SlotMixin implements LegacyApi.SlotApi {
//$$     @Shadow
//$$     @Final
//$$     private int slot;
//$$
//$$     @Override
//$$     public int getContainerSlot() {
//$$         return this.slot;
//$$     }
//$$ }
//#endif
