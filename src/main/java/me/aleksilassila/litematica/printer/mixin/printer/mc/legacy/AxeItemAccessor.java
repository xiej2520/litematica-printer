//#if MC < 11802
//$$ package me.aleksilassila.litematica.printer.mixin.printer.mc.legacy;
//$$
//$$ import net.minecraft.world.item.AxeItem;
//$$ import net.minecraft.world.level.block.Block;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//$$ import java.util.Map;
//$$
//$$ @Mixin(AxeItem.class)
//$$ public interface AxeItemAccessor {
//$$     @Accessor("STRIPABLES")
//$$     static Map<Block, Block> printer$strippables() { throw new AssertionError(); }
//$$ }
//#endif
