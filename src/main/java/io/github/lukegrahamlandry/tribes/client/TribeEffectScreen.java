package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class TribeEffectScreen extends TribeScreen {
    private ConfirmButton confirmButton;
    public static List<Effect> posEffects = TribesConfig.getGoodEffects();
    public static List<Effect> negEffects = TribesConfig.getBadEffects();
    private static ArrayList<Effect> selGoodEffects = new ArrayList<>();
    private static ArrayList<Effect> selBadEffects = new ArrayList<>();
    private static int maxGoodEffects = TribesManager.getNumberOfGoodEffects(Minecraft.getInstance().player);
    private static int maxBadEffects = TribesManager.getNumberOfBadEffects(Minecraft.getInstance().player);
    private static int numSelectedGood;
    private static int numSelectedBad;

    public TribeEffectScreen() {
        super(".tribeEffectScreen", "textures/gui/tribe_effects_left.png", "textures/gui/tribe_effects_right.png", 175, 219, false);
    }

    @Override
    public void tick() {
        confirmButton.active = (selGoodEffects.size()!=0) || (selBadEffects.size()!=0);
    }

    @Override
    protected void init() {
        super.init();
        this.confirmButton = this.addButton(new ConfirmButton(this,this.guiLeft + (this.xSize - 11), (this.ySize/2 - 11), this.ySize));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int i=0;
        int k=0;
        for(Effect effect : posEffects){
            EffectButton tribeeffect$effectbutton;
            i=(k>=154) ? 82 : i;
            k=(k>=154) ? 0 : k;
            for(int j=1; j<=3;j++){
                tribeeffect$effectbutton = new EffectButton(this, this.guiLeft + 11 + i, this.guiTop + 36 + k, ySize, effect, j);
                this.font.drawString(matrixStack, String.valueOf(j), (float)(this.guiLeft + 13 + i), (float)(this.guiTop + 38 + k), 0xffffff);
                this.addButton(tribeeffect$effectbutton);
                i+=22;
            }
            i-=22*3;
            k+=22;
        }
        i=0;
        k=0;
        for(Effect effect : negEffects){
            EffectButton tribeeffect$effectbutton;
            i=(k>=154) ? 82 : i;
            k=(k>=154) ? 0 : k;
            for(int j=1; j<=3;j++){
                tribeeffect$effectbutton = new EffectButton(this, this.guiLeft + this.xSize + 16 + i, this.guiTop + 36 + k, ySize, effect, j);
                this.font.drawString(matrixStack, String.valueOf(j), (float)(this.guiLeft + this.xSize + 18 + i), (float)(this.guiTop + 38 + k), 0xffffff);
                this.addButton(tribeeffect$effectbutton);
                i+=22;
            }
            i-=22*3;
            k+=22;
        }
    }

    private void addEffect(Effect effect, boolean isGood){
        if(isGood){
            selGoodEffects.add(effect);
            numSelectedGood++;
        }else{
            selBadEffects.add(effect);
            numSelectedBad++;
        }
    }

    private void removeEffect(Effect effect, boolean isGood){
        if(isGood){
            selGoodEffects.remove(effect);
            numSelectedGood--;
        }else{
            selBadEffects.remove(effect);
            numSelectedBad--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static
    class ConfirmButton extends GuiButton.SpriteButton {
        TribeScreen screen;
        public ConfirmButton(TribeScreen screen,int x, int y, int ySizeIn) {
            super(screen, x, y, 90, 220, ySizeIn);
            this.screen = screen;
        }

        public void onPress() {
            TribesManager.setTribeEffects(Minecraft.getInstance().player, selGoodEffects, selBadEffects);
        }

        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            screen.renderTooltip(matrixStack, DialogTexts.GUI_DONE, mouseX, mouseY);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static
    class EffectButton extends GuiButton {
        private final TribeEffectScreen screen;
        private final Effect effect;
        private final TextureAtlasSprite effectSprite;
        private final ITextComponent effectName;

        public EffectButton(TribeScreen screen, int x, int y, int ySizeIn, Effect p_i50827_4_, int amplifier) {
            super(screen, x, y, ySizeIn);
            this.effect = p_i50827_4_;
            this.effectSprite = Minecraft.getInstance().getPotionSpriteUploader().getSprite(p_i50827_4_);
            this.effectName = this.getEffectName(p_i50827_4_);
            this.screen = (TribeEffectScreen) screen;
        }

        private ITextComponent getEffectName(Effect p_243337_1_) {
            IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(p_243337_1_.getName());
            if (p_243337_1_ != Effects.REGENERATION) {
                iformattabletextcomponent.appendString(" II");
            }

            return iformattabletextcomponent;
        }

        public void onPress() {
            if(posEffects.contains(effect)){
                if(numSelectedGood < maxGoodEffects){
                    if(!isSelected()){
                        screen.addEffect(effect, true);
                        this.setSelected(true);
                    }else{
                        screen.removeEffect(effect, true);
                        this.setSelected(false);
                    }
                }
            }else if(negEffects.contains(effect)){
                if(numSelectedBad < maxBadEffects){
                    if(!isSelected()){
                        screen.addEffect(effect, false);
                        this.setSelected(true);
                    }else{
                        screen.removeEffect(effect, false);
                        this.setSelected(false);
                    }
                }
            }
        }

        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            screen.renderTooltip(matrixStack, this.effectName, mouseX, mouseY);
        }

        protected void func_230454_a_(MatrixStack p_230454_1_) {
            Minecraft.getInstance().getTextureManager().bindTexture(this.effectSprite.getAtlasTexture().getTextureLocation());
            blit(p_230454_1_, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.effectSprite);
        }
    }
}
