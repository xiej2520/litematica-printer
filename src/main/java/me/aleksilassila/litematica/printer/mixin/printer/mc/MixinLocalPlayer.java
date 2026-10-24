package me.aleksilassila.litematica.printer.mixin.printer.mc;

import com.mojang.authlib.GameProfile;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import me.aleksilassila.litematica.printer.config.Configs;
import me.aleksilassila.litematica.printer.runtime.RuntimeAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
//#if MC >= 260300
//$$ import net.minecraft.world.level.block.entity.SignTextSlot;
//$$ import java.util.List;
//#endif
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer extends AbstractClientPlayer {
    @Final
    @Shadow
    public ClientPacketListener connection;

    @Final
    @Shadow
    protected Minecraft minecraft;

    @Unique
    private boolean litematica_printer$runtimeResetDone;

    //#if MC == 11902
    //$$ public MixinLocalPlayer(ClientLevel world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
    //$$    super(world, profile, publicKey);
    //$$ }
    //#else
    public MixinLocalPlayer(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }
    //#endif

    //#if MC >= 11700
    @Inject(at = @At("HEAD"), method = "resetPos")
    //#else
    // 1.15.2 inherits resetPos instead of overriding it; initialize once on
    // the first player tick, after Minecraft has installed the new player.
    //$$ @Inject(at = @At("HEAD"), method = "tick")
    //#endif
    public void init(CallbackInfo ci) {
        if (!this.litematica_printer$runtimeResetDone) {
            RuntimeAccess.get().reset("local_player_init");
            this.litematica_printer$runtimeResetDone = true;
        }
    }

    @Inject(method = "closeContainer", at = @At("HEAD"), cancellable = true)
    private void printer$drainContainerSync(CallbackInfo ci) {
        if (RuntimeAccess.get().containerSync().guardClose()
                || RuntimeAccess.get().chestTrackerAdapter().guardClose()) ci.cancel();
    }

    @Inject(method = "openTextEdit", at = @At("HEAD"), cancellable = true)
    //#if MC >= 260300
    //$$ public void openTextEdit(SignBlockEntity sign, SignTextSlot slot, CallbackInfo ci) {
    //$$     openEditSignScreen(sign, slot == SignTextSlot.FRONT, ci);
    //$$ }
    //#elseif MC > 11904
    public void openTextEdit(SignBlockEntity sign, boolean front, CallbackInfo ci) {
        openEditSignScreen(sign, front, ci);
    }
    //#else
    //$$ public void openTextEdit(SignBlockEntity sign, CallbackInfo ci) {
    //$$    openEditSignScreen(sign, false, ci);
    //$$ }
    //#endif

    @Unique
    public void openEditSignScreen(SignBlockEntity sign, boolean front, CallbackInfo ci) {
        if (!Configs.Core.WORK_SWITCH.getBooleanValue()) {
            return;
        }
        getTargetSignEntity(sign)
                .filter(signBlockEntity -> RuntimeAccess.get().actionBroker().consumePrintSignEdit(sign.getBlockPos()))
                .ifPresent(signBlockEntity ->
        {
            //#if MC >= 260300
            //$$ List<net.minecraft.network.chat.Component> messages = signBlockEntity.getText(front ? SignTextSlot.FRONT : SignTextSlot.BACK).getMessages(false);
            //$$ String line1 = messages.get(0).getString();
            //$$ String line2 = messages.get(1).getString();
            //$$ String line3 = messages.get(2).getString();
            //$$ String line4 = messages.get(3).getString();
            //#elseif MC > 11904
            String line1 = signBlockEntity.getText(front).getMessage(0, false).getString();
            String line2 = signBlockEntity.getText(front).getMessage(1, false).getString();
            String line3 = signBlockEntity.getText(front).getMessage(2, false).getString();
            String line4 = signBlockEntity.getText(front).getMessage(3, false).getString();
            //#elseif MC >= 11700
            //$$ String line1 = signBlockEntity.getMessage(0, false).getString();
            //$$ String line2 = signBlockEntity.getMessage(1, false).getString();
            //$$ String line3 = signBlockEntity.getMessage(2, false).getString();
            //$$ String line4 = signBlockEntity.getMessage(3, false).getString();
            //#else
            //$$ net.minecraft.network.chat.Component line1 = signBlockEntity.getMessage(0);
            //$$ net.minecraft.network.chat.Component line2 = signBlockEntity.getMessage(1);
            //$$ net.minecraft.network.chat.Component line3 = signBlockEntity.getMessage(2);
            //$$ net.minecraft.network.chat.Component line4 = signBlockEntity.getMessage(3);
            //#endif
            //#if MC >= 260300
            //$$ ServerboundSignUpdatePacket packet = new ServerboundSignUpdatePacket(sign.getBlockPos(),
            //$$         List.of(line1, line2, line3, line4), front ? SignTextSlot.FRONT : SignTextSlot.BACK);
            //#else
            ServerboundSignUpdatePacket packet = new ServerboundSignUpdatePacket(sign.getBlockPos(),
                    //#if MC > 11904
                    front,
                    //#endif
                    line1,
                    line2,
                    line3,
                    line4
            );
            //#endif
            this.connection.send(packet);
            ci.cancel();
        });
    }

    @Unique
    private Optional<SignBlockEntity> getTargetSignEntity(SignBlockEntity sign) {
        WorldSchematic worldSchematic = SchematicWorldHandler.getSchematicWorld();
        if (sign.getLevel() == null || worldSchematic == null) {
            return Optional.empty();
        }
        BlockEntity targetBlockEntity = worldSchematic.getBlockEntity(sign.getBlockPos());
        if (targetBlockEntity instanceof SignBlockEntity targetSignEntity) {
            return Optional.of(targetSignEntity);
        }
        return Optional.empty();
    }
}
