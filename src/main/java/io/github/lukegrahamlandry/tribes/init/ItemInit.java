package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.item.GUIItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TribesMain.MOD_ID);

    public static void init(IEventBus eventBus){
        ITEMS.register("gui_item", () -> new GUIItem(new Item.Properties().group(ItemGroup.MISC)));

        ITEMS.register(eventBus);
    }
}
