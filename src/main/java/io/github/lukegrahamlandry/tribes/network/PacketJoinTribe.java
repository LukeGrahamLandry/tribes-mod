package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketJoinTribe {
    private String tribeName;

    // Read tribe name from PacketBuffer
    public PacketJoinTribe(PacketBuffer buf) {
        this.tribeName = buf.readString(32767);
    }

    // Write tribe name to PacketBuffer
    public void toBytes(PacketBuffer buf){
        buf.writeString(this.tribeName);
    }

    public PacketJoinTribe(String tribeNameIn){
        this.tribeName = tribeNameIn;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            TribeActionResult result = TribesManager.joinTribe(tribeName, ctx.get().getSender());
            if (result == TribeActionResult.SUCCESS){
                ctx.get().getSender().sendMessage(new StringTextComponent("You joined the tribe: " + tribeName), ctx.get().getSender().getUniqueID());
            } else {
                ctx.get().getSender().sendMessage(new StringTextComponent(result.toString()), ctx.get().getSender().getUniqueID());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
