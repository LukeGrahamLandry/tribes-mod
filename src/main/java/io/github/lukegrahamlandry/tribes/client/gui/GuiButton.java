package io.github.lukegrahamlandry.tribes.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// Generic Button for GUI screens
@OnlyIn(Dist.CLIENT)
public abstract class GuiButton extends AbstractButton {
    private boolean selected;
    private static TribeScreen screen;
    private int ySize;

    protected GuiButton(TribeScreen screenIn, int x, int y, int ySizeIn) {
        super(x, y, 22, 22, TextComponent.EMPTY);
        screen = screenIn;
        ySize = ySizeIn;
    }

    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Minecraft.getInstance().getTextureManager().bind(screen.getGuiTexture());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, screen.getGuiTexture());
        // RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int j = 0;
        if (!this.active) {
            j += this.width * 2;
        } else if (this.selected) {
            j += this.width * 1;
        } else if (this.isHovered()) {
            j += this.width * 3;
        }

        this.blit(matrixStack, this.x, this.y, j, ySize, this.width, this.height);
        this.renderIcon(matrixStack);
    }

    protected abstract void renderIcon(PoseStack p_230454_1_);

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selectedIn) {
        this.selected = selectedIn;
    }

    // Button that utilizes a sprite such as the effects sprite
    @OnlyIn(Dist.CLIENT)
    abstract static class SpriteButton extends GuiButton {
        private final int u;
        private final int v;

        protected SpriteButton(TribeScreen screen, int x, int y, int u, int v, int ySizeIn) {
            super(screen, x, y, ySizeIn);
            this.u = u;
            this.v = v;
        }

        protected void renderIcon(PoseStack p_230454_1_) {
            this.blit(p_230454_1_, this.x + 2, this.y + 2, this.u, this.v, 18, 18);
        }
    }
}
