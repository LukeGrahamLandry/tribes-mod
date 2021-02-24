package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCreateTribe {
    private String tribeName;

    //Read tribe name from PacketBuffer
    public PacketCreateTribe(PacketBuffer buf) {
        this.tribeName = buf.readString(24);
    }

    //Write tribe name to PacketBuffer
    public void toBytes(PacketBuffer buf){
        buf.writeString(this.tribeName);
    }

    public PacketCreateTribe(String tribeNameIn){
        this.tribeName=tribeNameIn;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            if(TribesManager.createNewTribe(tribeName, ctx.get().getSender())== TribeActionResult.SUCCESS){
                ctx.get().getSender().sendMessage(new StringTextComponent("You are now in " + tribeName), ctx.get().getSender().getUniqueID());
            }else if(TribesManager.createNewTribe(tribeName, ctx.get().getSender())== TribeActionResult.IN_TRIBE){
                ctx.get().getSender().sendMessage(new StringTextComponent("You're already in a tribe"), ctx.get().getSender().getUniqueID());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
