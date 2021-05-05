package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
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

@OnlyIn(Dist.CLIENT)
public abstract class GuiButton extends AbstractButton {
    private boolean selected;
    private static TribeScreen screen;
    private int ySize;

    protected GuiButton(TribeScreen screenIn, int x, int y, int ySizeIn) {
        super(x, y, 22, 22, StringTextComponent.EMPTY);
        screen = screenIn;
        ySize = ySizeIn;
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

        protected SpriteButton(TribeScreen screen, int x, int y, int u, int v, int ySizeIn) {
            super(screen, x, y, ySizeIn);
            this.u = u;
            this.v = v;
        }

        protected void func_230454_a_(MatrixStack p_230454_1_) {
            this.blit(p_230454_1_, this.x + 2, this.y + 2, this.u, this.v, 18, 18);
        }
    }
}
