package me.aleksilassila.litematica.printer.mixin_plugin;

import me.aleksilassila.litematica.printer.mixin.printer.mc.MixinMultiPlayerGameMode;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UseItemMixinSignatureTest {
    @Test void theRequiredUseItemShadowMatchesTheActualVanillaDescriptor() throws Exception {
        //#if MC > 11802
        var vanilla = MultiPlayerGameMode.class.getDeclaredMethod("useItem", Player.class, InteractionHand.class);
        var shadow = MixinMultiPlayerGameMode.class.getDeclaredMethod("useItem", Player.class, InteractionHand.class);
        //#else
        //$$ var vanilla = MultiPlayerGameMode.class.getDeclaredMethod("useItem", Player.class, net.minecraft.world.level.Level.class, InteractionHand.class);
        //$$ var shadow = MixinMultiPlayerGameMode.class.getDeclaredMethod("useItem", Player.class, net.minecraft.world.level.Level.class, InteractionHand.class);
        //#endif
        assertEquals(vanilla.getReturnType(), shadow.getReturnType());
    }
}
