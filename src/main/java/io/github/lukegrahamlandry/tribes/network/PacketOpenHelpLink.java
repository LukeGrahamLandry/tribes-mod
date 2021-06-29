package io.github.lukegrahamlandry.tribes.network;

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
        ctx.get().enqueueWork(() -> doOpen(packet));
        ctx.get().setPacketHandled(true);
    }

    public static String helpLink = "https://github.com/LukeGrahamLandry/tribes-mod/blob/main/wiki.md";

    @OnlyIn(Dist.CLIENT)
    private static void doOpen(PacketOpenHelpLink packet){
        Minecraft.getInstance().displayGuiScreen(new ConfirmOpenLinkScreen(PacketOpenHelpLink::confirmLink, helpLink, true));
    }

    private static void confirmLink(boolean doOpen) {
        if (doOpen) {
            openLink(helpLink);
        }

        Minecraft.getInstance().displayGuiScreen(null);
    }

    private static void openLink(String uri) {
        Util.getOSType().openURI(uri);
    }
}