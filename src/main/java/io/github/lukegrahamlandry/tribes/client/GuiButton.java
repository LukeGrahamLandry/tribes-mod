package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class GuiButton extends AbstractButton {
    private boolean selected;
    private static TribeScreen screen;
    private int ySize;

    protected GuiButton(TribeScreen screenIn, int x, int y, int ySizeIn) {
        super(x, y, 17, 17, StringTextComponent.EMPTY);
        this.screen = screenIn;
        this.ySize = ySizeIn;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bindTexture(screen.getGuiTexture());
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int j = 0;
        if (!this.active) {
            j += this.width * 2;
        } else if (this.selected) {
            j += this.width * 1;
        } else if (this.isHovered()) {
            j += this.width * 3;
        }

        this.blit(matrixStack, this.x, this.y, j, ySize, this.width, this.height);
        this.func_230454_a_(matrixStack);
    }

    protected abstract void func_230454_a_(MatrixStack p_230454_1_);

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selectedIn) {
        this.selected = selectedIn;
    }

@OnlyIn(Dist.CLIENT)
abstract static class SpriteButton extends GuiButton {
    private final int u;
    private final int v;

    protected SpriteButton(TribeScreen screen, int x, int y, int u, int v) {
        super(screen, x, y, v);
        this.u = u;
        this.v = v;
    }

    protected void func_230454_a_(MatrixStack p_230454_1_) {
        this.blit(p_230454_1_, this.x + 2, this.y + 2, this.u, this.v, 17, 17);
    }
}

@OnlyIn(Dist.CLIENT)
static
class ConfirmButton extends GuiButton.SpriteButton {
    public ConfirmButton(TribeScreen screen, int x, int y) {
        super(screen, x, y, 67, 216);
    }

    public void onPress() {

    }

    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        screen.renderTooltip(matrixStack, DialogTexts.GUI_DONE, mouseX, mouseY);
    }
}

@OnlyIn(Dist.CLIENT)
static
class EffectButton extends GuiButton {
    private final Effect effect;
    private final TextureAtlasSprite field_212946_c;
    private final boolean field_212947_d;
    private final ITextComponent field_243336_e;

    public EffectButton(TribeScreen screen, int x, int y, int ySizeIn, Effect p_i50827_4_, boolean p_i50827_5_) {
        super(screen, x, y, ySizeIn);
        this.effect = p_i50827_4_;
        this.field_212946_c = Minecraft.getInstance().getPotionSpriteUploader().getSprite(p_i50827_4_);
        this.field_212947_d = p_i50827_5_;
        this.field_243336_e = this.func_243337_a(p_i50827_4_, p_i50827_5_);
    }

    private ITextComponent func_243337_a(Effect p_243337_1_, boolean p_243337_2_) {
        IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(p_243337_1_.getName());
        if (!p_243337_2_ && p_243337_1_ != Effects.REGENERATION) {
            iformattabletextcomponent.appendString(" II");
        }

        return iformattabletextcomponent;
    }

    public void onPress() {
        if (!this.isSelected()) {
            if (this.field_212947_d) {

            } else {

            }

            screen.init();
            screen.tick();
        }
    }

    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        screen.renderTooltip(matrixStack, this.field_243336_e, mouseX, mouseY);
    }

    protected void func_230454_a_(MatrixStack p_230454_1_) {
        Minecraft.getInstance().getTextureManager().bindTexture(this.field_212946_c.getAtlasTexture().getTextureLocation());
        blit(p_230454_1_, this.x + 2, this.y + 2, this.getBlitOffset(), 13, 13, this.field_212946_c);
    }
}
}
