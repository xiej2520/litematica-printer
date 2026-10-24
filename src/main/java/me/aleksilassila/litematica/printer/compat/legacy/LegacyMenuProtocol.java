//#if MC < 11802
//$$ package me.aleksilassila.litematica.printer.compat.legacy;
//$$
//$$ import me.aleksilassila.litematica.printer.runtime.RuntimeAccess;
//$$ import me.aleksilassila.litematica.printer.utils.mods.ChestTrackerBridge;
//$$ import me.aleksilassila.litematica.printer.utils.mods.QuickShulkerBridge;
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.client.multiplayer.ClientPacketListener;
//$$ import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.inventory.ClickType;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.Items;
//$$ import java.util.ArrayDeque;
//$$ import java.util.List;
//$$ import java.util.Map;
//$$ import java.util.UUID;
//$$ import java.util.WeakHashMap;
//$$
//$$ /** The pre-1.17 transaction protocol; counters are local observations, not wire state IDs. */
//$$ public final class LegacyMenuProtocol {
//$$     private static final Map<AbstractContainerMenu, Integer> revisions = new WeakHashMap<>();
//$$     private static final ArrayDeque<Snapshot> pending = new ArrayDeque<>();
//$$     private static ClientPacketListener connection;
//$$     private record Snapshot(AbstractContainerMenu menu, List<ItemStack> items) { }
//$$     private LegacyMenuProtocol() { }
//$$
//$$     private static Minecraft client() {
//$$         var client = Minecraft.getInstance();
//$$         if (connection != client.getConnection()) {
//$$             connection = client.getConnection();
//$$             revisions.clear();
//$$             pending.clear();
//$$         }
//$$         return client;
//$$     }
//$$
//$$     public static int revision(AbstractContainerMenu menu) {
//$$         client();
//$$         return revisions.getOrDefault(menu, 0);
//$$     }
//$$
//$$     public static void send(AbstractContainerMenu menu, int slot, int button, ClickType type) {
//$$         var client = client();
//$$         if (client.player == null || connection == null || client.player.containerMenu != menu) return;
//$$         // Vanilla executes the click before comparing this expected result. A deliberately
//$$         // unequal, nonce-tagged result requests an authoritative inventory + cursor reply.
//$$         // The item is never installed in a slot. Vanilla handles the rejected transaction's
//$$         // acknowledgement; no newer state-ID/changed-slot fields are sent on this protocol.
//$$         connection.send(packet(client.player, menu, slot, button, type));
//$$     }
//$$
//$$     /** The click packet of {@link #send}, for callers that order it among their own packets. */
//$$     public static ServerboundContainerClickPacket packet(Player player, AbstractContainerMenu menu, int slot, int button, ClickType type) {
//$$         var expectedResult = new ItemStack(Items.BARRIER);
//$$         expectedResult.getOrCreateTag().putString("litematica-printer:sync", UUID.randomUUID().toString());
//$$         return new ServerboundContainerClickPacket(menu.containerId, slot, button,
//$$                 type, expectedResult, menu.backup(player.inventory));
//$$     }
//$$
//$$     public static void content(int containerId, List<ItemStack> items) {
//$$         var client = client();
//$$         if (client.player == null) return;
//$$         var menu = containerId == 0 ? client.player.inventoryMenu : client.player.containerMenu;
//$$         if (menu.containerId != containerId) return;
//$$         // ServerPlayer.refreshContainer sends slots, THEN a separate (-1,-1) cursor.
//$$         // Do not expose the full reply to transfer planners with the previous cursor.
//$$         pending.addLast(new Snapshot(menu, items.stream().map(ItemStack::copy).toList()));
//$$     }
//$$
//$$     public static void slot(int containerId, int slot) {
//$$         var client = client();
//$$         if (client.player == null) return;
//$$         if (containerId != -1 || slot != -1) {
//$$             var menu = containerId == 0 ? client.player.inventoryMenu : client.player.containerMenu;
//$$             if (menu.containerId == containerId) revisions.merge(menu, 1, Integer::sum);
//$$             return;
//$$         }
//$$         while (!pending.isEmpty()) {
//$$             var snapshot = pending.removeFirst();
//$$             var menu = snapshot.menu();
//$$             if (menu != client.player.containerMenu && menu != client.player.inventoryMenu) continue;
//$$             int revision = revisions.merge(menu, 1, Integer::sum);
//$$             var cursor = client.player.inventory.getCarried().copy();
//$$             ChestTrackerBridge.onContainerContent(menu.containerId, revision, snapshot.items(), cursor);
//$$             QuickShulkerBridge.onInventoryContent(menu.containerId, snapshot.items(), cursor);
//$$             if (menu == client.player.containerMenu) {
//$$                 RuntimeAccess.get().containerSync().content(menu.containerId, revision, snapshot.items(), cursor);
//$$             }
//$$         }
//$$     }
//$$ }
//#endif
