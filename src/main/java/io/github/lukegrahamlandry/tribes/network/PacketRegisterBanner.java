package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.init.BannarInit;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.BannerPattern;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// server -> client
// unused
public class PacketRegisterBanner {
    private String name;

    public PacketRegisterBanner(PacketBuffer buf) {
        this.name = buf.readUtf(32767);
    }

    public void toBytes(PacketBuffer buf){
        buf.writeUtf(this.name);
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
