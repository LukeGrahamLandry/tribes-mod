package io.github.lukegrahamlandry.tribes.item;

import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import io.github.lukegrahamlandry.tribes.tribe_data.claim.LandClaimWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TribeTalismanItem extends Item {
    public TribeTalismanItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
        p_77624_3_.add(new TranslationTextComponent("item.tribes.talisman.desc").withStyle(TextFormatting.YELLOW));
        super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        if (!ctx.getLevel().isClientSide()){
            BlockPos pos = ctx.getClickedPos();

            Block block = ctx.getLevel().getBlockState(pos).getBlock();

            if (BlockTags.BANNERS.contains(block)){
                if (!TribesConfig.bannerClaimsEnabled()){
                    ctx.getPlayer().sendMessage(TribeErrorType.BANNER_CLAIM_DISABLED.getText(), ctx.getPlayer().getUUID());
                    return super.useOn(ctx);
                }

                if (!LandClaimWrapper.getHemisphereManager().canClaim(ctx.getPlayer(), pos)){
                    ctx.getPlayer().sendMessage(TribeErrorType.CANT_CLAIM_IN_HEMI.getText(), ctx.getPlayer().getUUID());
                    return super.useOn(ctx);
                }

                Tribe tribe = TribesManager.getTribeOf(ctx.getPlayer().getUUID());
                if (tribe == null){
                    ctx.getPlayer().sendMessage(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), ctx.getPlayer().getUUID());
                    return super.useOn(ctx);
                }
                if (LandClaimWrapper.getBannerManager().canClaim(ctx.getPlayer(), pos)){
                    TribeErrorType result = tribe.bannerClaim(ctx.getPlayer(), pos);
                    if (result == TribeErrorType.SUCCESS){
                        tribe.broadcastMessage(TribeSuccessType.BANNER_CLAIM, ctx.getPlayer().getUUID(), TribesConfig.getBannerClaimRadius(), pos.getX(), pos.getY(), pos.getZ());
                    } else {
                        ctx.getPlayer().sendMessage(result.getText(), ctx.getPlayer().getUUID());
                    }
                } else {
                    ctx.getPlayer().displayClientMessage(TribeErrorType.OVERLAPPING_CLAIM.getText(), false);
                }
            } else {
                ctx.getPlayer().displayClientMessage(TribeErrorType.MUST_CLICK_BANNER.getText(), false);
            }



        }
        return super.useOn(ctx);
    }
}
