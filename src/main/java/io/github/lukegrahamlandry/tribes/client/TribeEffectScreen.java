package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TribeEffectScreen extends TribeScreen {
    private ConfirmButton confirmButton;
    public static List<Effect> posEffects = TribesConfig.getGoodEffects();
    public static List<Effect> negEffects = TribesConfig.getBadEffects();
    private static Map<Effect, Integer> selGoodEffects = new HashMap<>();
    private static Map<Effect, Integer> selBadEffects = new HashMap<>();
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
        int i=0;
        int k=0;
        for(Effect effect : posEffects){
            EffectButton tribeeffect$effectbutton;
            i=(k>=154) ? 82 : i;
            k=(k>=154) ? 0 : k;
            for(int j=1; j<=3;j++){
                tribeeffect$effectbutton = new EffectButton(this, this.guiLeft + 11 + i, this.guiTop + 36 + k, ySize, effect, j);
                this.addButton(tribeeffect$effectbutton);
                if(selGoodEffects.containsKey(effect)){
                    tribeeffect$effectbutton.active=false;
                }
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
                this.addButton(tribeeffect$effectbutton);
                if(selBadEffects.containsKey(effect)){
                    tribeeffect$effectbutton.active=false;
                }
                i+=22;
            }
            i-=22*3;
            k+=22;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.buttons.forEach((b) -> {
            GuiButton button = (GuiButton) b;
            if(button instanceof EffectButton){
                this.font.drawString(matrixStack, String.valueOf(((EffectButton)button).getAmplifier()), (float)(button.x+2), (float)(button.y+2), 0xffffff);
            }
        });
    }

    private void addEffect(Effect effect, int amplifier, boolean isGood){
        if(isGood){
            selGoodEffects.put(effect, amplifier);
            numSelectedGood++;
        }else{
            selBadEffects.put(effect, amplifier);
            numSelectedBad++;
        }
    }

    private void removeEffect(Effect effect, int amplifier, boolean isGood){
        if(isGood){
            selGoodEffects.remove(effect, amplifier);
            numSelectedGood--;
        }else{
            selBadEffects.remove(effect, amplifier);
            numSelectedBad--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ConfirmButton extends GuiButton.SpriteButton {
        TribeScreen screen;
        public ConfirmButton(TribeScreen screen,int x, int y, int ySizeIn) {
            super(screen, x, y, 90, 220, ySizeIn);
            this.screen = screen;
        }

        public void onPress() {
            TribesManager.setTribeEffects(Minecraft.getInstance().player, selGoodEffects, selBadEffects);
            selBadEffects.clear();
            selGoodEffects.clear();
            TribeEffectScreen.this.minecraft.displayGuiScreen(null);
        }

        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            screen.renderTooltip(matrixStack, DialogTexts.GUI_DONE, mouseX, mouseY);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class EffectButton extends GuiButton {
        private final TribeEffectScreen screen;
        private final Effect effect;
        private final TextureAtlasSprite effectSprite;
        private final ITextComponent effectName;
        private final int amplifier;

        public EffectButton(TribeScreen screen, int x, int y, int ySizeIn, Effect p_i50827_4_, int amplifierIn) {
            super(screen, x, y, ySizeIn);
            this.effect = p_i50827_4_;
            this.effectSprite = Minecraft.getInstance().getPotionSpriteUploader().getSprite(p_i50827_4_);
            this.effectName = this.getEffectName(p_i50827_4_);
            this.screen = (TribeEffectScreen) screen;
            this.amplifier = amplifierIn;
        }

        private ITextComponent getEffectName(Effect effect) {
            IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effect.getName());
            if (this.getAmplifier() == 1) {
                iformattabletextcomponent.appendString(" I");
            }else if (this.getAmplifier() == 2) {
                iformattabletextcomponent.appendString(" II");
            }else if (this.getAmplifier() == 3) {
                iformattabletextcomponent.appendString(" III");
            }

            return iformattabletextcomponent;
        }

        public void onPress() {
            if(posEffects.contains(effect)){
                if(numSelectedGood < maxGoodEffects){
                    if(!isSelected()){
                        screen.addEffect(effect, getAmplifier(), true);
                        System.out.println(effectName.getString() + " " + this.getAmplifier());
                    }else{
                        screen.removeEffect(effect, getAmplifier(), true);
                        System.out.println(effectName.getString() + " " + this.getAmplifier());
                    }
                }
            }else if(negEffects.contains(effect)){
                if(numSelectedBad < maxBadEffects){
                    if(!isSelected()){
                        screen.addEffect(effect, getAmplifier(), false);
                        System.out.println(effectName.getString() + " " + this.getAmplifier());
                    }else{
                        screen.removeEffect(effect, getAmplifier(), false);
                        System.out.println(effectName.getString() + " " + this.getAmplifier());
                    }
                }
            }
            TribeEffectScreen.this.init();
            TribeEffectScreen.this.tick();
        }

        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            screen.renderTooltip(matrixStack, this.effectName, mouseX, mouseY);
        }

        public int getAmplifier(){
            return amplifier;
        }

        protected void func_230454_a_(MatrixStack p_230454_1_) {
            Minecraft.getInstance().getTextureManager().bindTexture(this.effectSprite.getAtlasTexture().getTextureLocation());
            blit(p_230454_1_, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.effectSprite);
        }
    }
}
