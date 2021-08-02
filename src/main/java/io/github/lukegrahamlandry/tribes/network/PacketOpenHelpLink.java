package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.client.gui.HelpScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenHelpLink {
    public PacketOpenHelpLink() {
    }

    public static PacketOpenHelpLink decode(FriendlyByteBuf buf) {
        return new PacketOpenHelpLink();
    }

    public static void encode(PacketOpenHelpLink packet, FriendlyByteBuf buf) {

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