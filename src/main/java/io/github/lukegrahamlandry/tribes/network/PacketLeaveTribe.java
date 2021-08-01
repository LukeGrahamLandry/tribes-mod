package io.github.lukegrahamlandry.tribes.network;

import com.mojang.brigadier.Command;
import io.github.lukegrahamlandry.tribes.commands.ConfirmCommand;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
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
            Tribe tribe = TribesManager.getTribeOf(ctx.get().getSender().getUUID());
            if (tribe != null){
                ConfirmCommand.add(ctx.get().getSender(), () -> {
                    TribesManager.leaveTribe(ctx.get().getSender());
                    ctx.get().getSender().sendMessage(TribeSuccessType.YOU_LEFT.getText(), ctx.get().getSender().getUUID());
                });
            } else {
                ctx.get().getSender().sendMessage(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), ctx.get().getSender().getUUID());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
