package io.github.lukegrahamlandry.tribes.network;

import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.List;

public class PacketUtil {
    public static void writeStringList(PacketBuffer buf, List<String> data){
        buf.writeInt(data.size());
        for (String s : data){
            buf.writeUtf(s);
        }
    }

    public static List<String> readStringList(PacketBuffer buf){
        List<String> data = new ArrayList<>();
        int size = buf.readInt();
        for (int i=0;i<size;i++){
            data.add(buf.readUtf(32767));
        }

        return data;
    }
}
