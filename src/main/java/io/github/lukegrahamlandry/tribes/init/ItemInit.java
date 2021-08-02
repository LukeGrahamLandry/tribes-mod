package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.item.TribeCompass;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TribesMain.MOD_ID);

    public static RegistryObject<Item> TRIBE_COMPASS = ITEMS.register("tribe_compass", () -> new TribeCompass(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static RegistryObject<Item> ALTER = ITEMS.register("alter", () -> new BlockItem(BlockInit.ALTER.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
}
