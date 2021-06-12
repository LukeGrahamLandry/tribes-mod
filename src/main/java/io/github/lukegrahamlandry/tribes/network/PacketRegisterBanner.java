package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.init.BannarInit;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// server -> client
// unused
public class PacketRegisterBanner {
    private String name;

    public PacketRegisterBanner(PacketBuffer buf) {
        this.name = buf.readString();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeString(this.name);
    }

    public PacketRegisterBanner(String name){
        this.name = name;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            TribesMain.LOGGER.debug(name);
            BannerPattern pattern = BannerPattern.create(name.toLowerCase(), name, name, true);
            BannarInit.patterns.put(name, pattern);
        });
        ctx.get().setPacketHandled(true);
    }
}
