package io.github.lukegrahamlandry.tribes.item;

import io.github.lukegrahamlandry.tribes.client.CreateTribeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class GUIItem extends Item {
    public GUIItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(worldIn.isRemote){
            //DistExecutor.safeRunWhenOn(Dist.CLIENT, this::openScreen);
            Minecraft.getInstance().displayGuiScreen(new CreateTribeScreen());
        }
        return ActionResult.resultSuccess(new ItemStack(this));
    }

    /*public DistExecutor.SafeRunnable openScreen() {
        return () -> Minecraft.getInstance().displayGuiScreen(new CreateTribeScreen());
    }*/
}
