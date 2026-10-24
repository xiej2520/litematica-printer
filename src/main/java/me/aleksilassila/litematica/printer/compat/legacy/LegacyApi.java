//#if MC < 11802
//$$ package me.aleksilassila.litematica.printer.compat.legacy;
//$$
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.core.Direction;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.entity.player.Abilities;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.item.Item;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.chunk.LevelChunkSection;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import net.minecraft.world.level.dimension.DimensionType;
//$$ import net.minecraft.world.phys.Vec3;
//$$
//$$ import java.util.stream.Stream;
//$$ import java.util.stream.StreamSupport;
//$$
//$$ /**
//$$  * Methods that the shared (1.18.2+) sources call directly but Minecraft 1.15.2 lacks.
//$$  * Loom injects these interfaces at compile time (versions/1.15.2/legacy-interfaces.json);
//$$  * the mixins in {@code mixin.printer.mc.legacy} add them to the classes at runtime.
//$$  */
//$$ public final class LegacyApi {
//$$     private LegacyApi() {
//$$     }
//$$
//$$     public interface BlockStateApi {
//$$         default boolean is(Block block) {
//$$             return ((BlockState) (Object) this).getBlock() == block;
//$$         }
//$$
//$$         default boolean requiresCorrectToolForDrops() {
//$$             return !((BlockState) (Object) this).getMaterial().isAlwaysDestroyable();
//$$         }
//$$     }
//$$
//$$     public interface BlockApi {
//$$         default float defaultDestroyTime() {
//$$             throw new AssertionError("implemented by BlockMixin");
//$$         }
//$$     }
//$$
//$$     public interface ItemStackApi {
//$$         default boolean is(Item item) {
//$$             return ((ItemStack) (Object) this).getItem() == item;
//$$         }
//$$     }
//$$
//$$     public interface EntityApi {
//$$         default boolean isOnGround() {
//$$             return ((Entity) (Object) this).onGround;
//$$         }
//$$
//$$         default float getXRot() {
//$$             return ((Entity) (Object) this).xRot;
//$$         }
//$$
//$$         default float getYRot() {
//$$             return ((Entity) (Object) this).yRot;
//$$         }
//$$
//$$         default Vec3 getEyePosition() {
//$$             return ((Entity) (Object) this).getEyePosition(1.0F);
//$$         }
//$$
//$$         default BlockPos blockPosition() {
//$$             return new BlockPos((Entity) (Object) this);
//$$         }
//$$     }
//$$
//$$     public interface PlayerApi {
//$$         default Inventory getInventory() {
//$$             return ((Player) (Object) this).inventory;
//$$         }
//$$
//$$         default Abilities getAbilities() {
//$$             return ((Player) (Object) this).abilities;
//$$         }
//$$     }
//$$
//$$     public interface ContainerMenuApi {
//$$         /** 1.15.2 keeps the cursor stack on the player inventory, not the menu. */
//$$         default ItemStack getCarried() {
//$$             Player player = Minecraft.getInstance().player;
//$$             return player == null ? ItemStack.EMPTY : player.inventory.getCarried();
//$$         }
//$$
//$$         /** Locally observed revision; 1.15.2 has no server state ID. */
//$$         default int getStateId() {
//$$             return LegacyMenuProtocol.revision((AbstractContainerMenu) (Object) this);
//$$         }
//$$     }
//$$
//$$     public interface SlotApi {
//$$         default int getContainerSlot() {
//$$             throw new AssertionError("implemented by SlotMixin");
//$$         }
//$$     }
//$$
//$$     public interface LevelApi {
//$$         default int getMinBuildHeight() {
//$$             return 0;
//$$         }
//$$
//$$         default DimensionType dimension() {
//$$             return ((Level) (Object) this).dimension.getType();
//$$         }
//$$     }
//$$
//$$     public interface LevelChunkSectionApi {
//$$         default boolean hasOnlyAir() {
//$$             return ((LevelChunkSection) (Object) this).isEmpty();
//$$         }
//$$     }
//$$
//$$     public interface PlaneApi {
//$$         default Stream<Direction> stream() {
//$$             return StreamSupport.stream(((Direction.Plane) (Object) this).spliterator(), false);
//$$         }
//$$     }
//$$
//$$     public interface DimensionTypeApi {
//$$         default ResourceLocation location() {
//$$             return DimensionType.getName((DimensionType) (Object) this);
//$$         }
//$$     }
//$$ }
//#endif
