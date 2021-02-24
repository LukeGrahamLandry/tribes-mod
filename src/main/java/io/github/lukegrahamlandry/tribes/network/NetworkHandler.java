package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    //Declaration of new Network Instance
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    //Function increments the ID ensuring no two packets have the same ID
    public static int nextID() {
        return ID++;
    }

    public static void registerMessages(){
        //Initialization of new Network Instance
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TribesMain.MOD_ID, "tribes"), () -> "1.0", s -> true, s -> true);

        //Register Create Tribe Packet
        INSTANCE.registerMessage(nextID(), PacketCreateTribe.class, PacketCreateTribe::toBytes, PacketCreateTribe::new, PacketCreateTribe::handle);
    }
}
