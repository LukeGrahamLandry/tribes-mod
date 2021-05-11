package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TribesMain.MOD_ID);

    // public static final RegistryObject<Block> ALTER = BLOCKS.register("alter", () -> new AlterBlock(AbstractBlock.Properties.from(Blocks.BLACK_WOOL)));
}
