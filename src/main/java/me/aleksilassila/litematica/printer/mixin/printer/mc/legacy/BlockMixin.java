//#if MC < 11802
//$$ package me.aleksilassila.litematica.printer.mixin.printer.mc.legacy;
//$$
//$$ import me.aleksilassila.litematica.printer.compat.legacy.LegacyApi;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$
//$$ @Mixin(net.minecraft.world.level.block.Block.class)
//$$ public abstract class BlockMixin implements LegacyApi.BlockApi {
//$$     @Shadow
//$$     @Final
//$$     protected float destroySpeed;
//$$
//$$     @Override
//$$     public float defaultDestroyTime() {
//$$         return this.destroySpeed;
//$$     }
//$$ }
//#endif
