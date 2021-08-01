package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.client.gui.HelpScreen;
import io.github.lukegrahamlandry.tribes.client.gui.JoinTribeScreen;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.net.URI;
import java.util.HashMap;
import java.util.function.Supplier;

public class PacketOpenHelpLink {
    public PacketOpenHelpLink() {
    }

    public static PacketOpenHelpLink decode(PacketBuffer buf) {
        return new PacketOpenHelpLink();
    }

    public static void encode(PacketOpenHelpLink packet, PacketBuffer buf) {

    }

    public static void handle(PacketOpenHelpLink packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(PacketOpenHelpLink::doOpen);
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void doOpen(){
        Minecraft.getInstance().setScreen(new HelpScreen());
    }

}