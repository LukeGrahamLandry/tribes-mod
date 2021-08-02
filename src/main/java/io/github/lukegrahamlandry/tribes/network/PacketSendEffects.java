package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.function.Supplier;

// client -> server
public class PacketSendEffects {
    public PacketSendEffects(FriendlyByteBuf buf) {

    }

    public void encode(FriendlyByteBuf buf){

    }

    public PacketSendEffects(){

    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketOpenEffectGUI(ctx.get().getSender()));
        });
        ctx.get().setPacketHandled(true);
    }
}
