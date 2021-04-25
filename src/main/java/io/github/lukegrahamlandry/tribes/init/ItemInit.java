package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.client.CreateTribeScreen;
import io.github.lukegrahamlandry.tribes.item.GUIItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TribesMain.MOD_ID);

    public static void init(IEventBus eventBus){
        ITEMS.register("create_gui_item", () -> new GUIItem(CreateTribeScreen::new));
        ITEMS.register("alter", () -> new BlockItem(BlockInit.ALTER.get(), new Item.Properties()));

        ITEMS.register(eventBus);
    }
}
