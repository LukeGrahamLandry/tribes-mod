package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

// client -> server
public class PacketSendEffects {
    public PacketSendEffects(PacketBuffer buf) {

    }

    public void encode(PacketBuffer buf){

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
