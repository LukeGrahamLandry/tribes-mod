package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// client -> sever
public class PacketLeaveTribe {
    public PacketLeaveTribe(PacketBuffer buf) {

    }

    public void encode(PacketBuffer buf){

    }

    public PacketLeaveTribe(){

    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            // todo: require confirm command for leader to leave
            TribeActionResult result = TribesManager.leaveTribe(ctx.get().getSender());
            if (result == TribeActionResult.SUCCESS){
                ctx.get().getSender().sendMessage(new StringTextComponent("You left your tribe"), ctx.get().getSender().getUniqueID());
            } else {
                ctx.get().getSender().sendMessage(new StringTextComponent(result.toString()), ctx.get().getSender().getUniqueID());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
