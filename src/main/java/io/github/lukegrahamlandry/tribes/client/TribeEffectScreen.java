package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.SaveEffectsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TribeEffectScreen extends TribeScreen {
    // Confirmation button
    private ConfirmButton confirmButton;
    // List of all positive and negative effects
    public static List<Effect> posEffects = TribesConfig.getGoodEffects();
    public static List<Effect> negEffects = TribesConfig.getBadEffects();
    // Map of selected effects and their amplifiers
    private Map<Effect, Integer> selGoodEffects;
    private Map<Effect, Integer> selBadEffects;
    // Maximum number of effects that can be selected; Both good and bad
    private int maxGoodEffects;
    private int maxBadEffects;
    // Current number of selected effects
    private static int numSelectedGood;
    private static int numSelectedBad;

    public TribeEffectScreen(int numGoodAllowed, int numBadAllowed, HashMap<Effect, Integer> currentEffects) {
        super(".tribeEffectScreen", "textures/gui/tribe_effects_left.png", "textures/gui/tribe_effects_right.png", 175, 219, false);

        // all this stuff is sent from the server by PacketOpenEffectGUI

        this.maxGoodEffects = numGoodAllowed;
        this.maxBadEffects = numBadAllowed;

        selGoodEffects = new HashMap<>();
        selBadEffects = new HashMap<>();
        currentEffects.forEach((effect, level) -> {
            if (posEffects.contains(effect)) selGoodEffects.put(effect, level);
            if (negEffects.contains(effect)) selBadEffects.put(effect, level);
        });
        calcNumSelected();
    }

    @Override
    public void tick() {
        calcNumSelected();
        confirmButton.active = numSelectedGood == maxGoodEffects && numSelectedBad == maxBadEffects;
    }

    @Override
    protected void init() {
        super.init();
        addButtons();
    }

    public void addButtons(){
        // Declare confirm button
        this.confirmButton = this.addButton(new ConfirmButton(this,this.guiLeft + (this.xSize - 11), (this.ySize/2 - 11), this.ySize));
        int i=0;
        int k=0;
        // Iteration through effects to create three buttons for three tiers of each effect
        for(Effect effect : posEffects){
            EffectButton tribeeffect$effectbutton;
            i=(k>=154) ? 82 : i;
            k=(k>=154) ? 0 : k;
            for(int j=1; j<=3;j++){
                tribeeffect$effectbutton = new EffectButton(this, this.guiLeft + 11 + i, this.guiTop + 36 + k, this.ySize, effect, true, j);
                this.addButton(tribeeffect$effectbutton);
                // Is the effect selected already?
                if(selGoodEffects.containsKey(effect) && selGoodEffects.get(effect)==j){
                    tribeeffect$effectbutton.setSelected(true);
                }else {
                    tribeeffect$effectbutton.setSelected(false);
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
                tribeeffect$effectbutton = new EffectButton(this, this.guiLeft + this.xSize + 16 + i, this.guiTop + 36 + k, ySize, effect, false, j);
                this.addButton(tribeeffect$effectbutton);
                if(selBadEffects.containsKey(effect) && selBadEffects.get(effect)==j){
                    tribeeffect$effectbutton.setSelected(true);
                }else {
                    tribeeffect$effectbutton.setSelected(false);
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
        this.font.drawString(matrixStack, "Benefits: " + numSelectedGood+"/"+maxGoodEffects, this.guiLeft + 15, this.guiTop + 20, 0x5d5d5d);
        this.font.drawString(matrixStack, "Drawbacks: " + numSelectedBad+"/"+maxBadEffects, this.guiLeft + 20 + xSize, this.guiTop + 20, 0x5d5d5d);
    }

    // Add effect to selected list
    private void addEffect(Effect effect, int amplifier, boolean isGood){
        if(isGood){
            selGoodEffects.put(effect, amplifier);
            numSelectedGood += amplifier;
        }else{
            selBadEffects.put(effect, amplifier);
            numSelectedBad += amplifier;
        }
    }

    // Remove effect from selected list
    private void removeEffect(Effect effect, int amplifier, boolean isGood){
        if(isGood){
            selGoodEffects.remove(effect, amplifier);
            numSelectedGood -= amplifier;
        }else{
            selBadEffects.remove(effect, amplifier);
            numSelectedBad -= amplifier;
        }
    }

    private void calcNumSelected() {
        numSelectedBad=0;
        numSelectedGood=0;
        for(Effect effect : selGoodEffects.keySet()){
            numSelectedGood+=selGoodEffects.get(effect);
        }
        for(Effect effect : selBadEffects.keySet()){
            numSelectedBad+=selBadEffects.get(effect);
        }
    }

    // Confirmation Button(Check Button)
    @OnlyIn(Dist.CLIENT)
    class ConfirmButton extends GuiButton.SpriteButton {
        TribeScreen screen;
        public ConfirmButton(TribeScreen screen,int x, int y, int ySizeIn) {
            super(screen, x, y, 90, 220, ySizeIn);
            this.screen = screen;
        }

        public void onPress() {
            if (this.active){
                NetworkHandler.INSTANCE.sendToServer(new SaveEffectsPacket(selGoodEffects, selBadEffects));
                selBadEffects.clear();
                selGoodEffects.clear();
                TribeEffectScreen.this.minecraft.displayGuiScreen(null);
            }
        }

        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            screen.renderTooltip(matrixStack, DialogTexts.GUI_DONE, mouseX, mouseY);
        }
    }

    // Effect Button
    @OnlyIn(Dist.CLIENT)
    class EffectButton extends GuiButton {
        private final TribeEffectScreen screen;
        private final Effect effect;
        private final boolean isGood;
        private final TextureAtlasSprite effectSprite;
        private final ITextComponent effectName;
        private final int amplifier;

        public EffectButton(TribeScreen screen, int x, int y, int ySizeIn, Effect p_i50827_4_, boolean isGoodIn, int amplifierIn) {
            super(screen, x, y, ySizeIn);
            this.effect = p_i50827_4_;
            this.isGood = isGoodIn;
            this.effectSprite = Minecraft.getInstance().getPotionSpriteUploader().getSprite(p_i50827_4_);
            this.effectName = this.getEffectName(p_i50827_4_);
            this.screen = (TribeEffectScreen) screen;
            this.amplifier = amplifierIn;
        }

        // Get the name of the effect based on the amplifier
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
            if (!this.isSelected()) {
                if (this.isGood) {
                    // Are the maximum number of effects selected?
                    if(TribeEffectScreen.this.selGoodEffects.size() < TribeEffectScreen.this.maxGoodEffects && (numSelectedGood + amplifier <= maxGoodEffects)){
                        TribeEffectScreen.this.addEffect(effect, amplifier, isGood);
                    }
                    // Are you selecting a different level of a selected effect?
                    if(TribeEffectScreen.this.selGoodEffects.containsKey(effect) && (numSelectedGood + amplifier <= maxGoodEffects)){
                        TribeEffectScreen.this.removeEffect(effect, TribeEffectScreen.this.selGoodEffects.get(effect), isGood);
                        TribeEffectScreen.this.addEffect(effect, amplifier, isGood);
                    }
                } else if(!this.isGood){
                    if(TribeEffectScreen.this.selBadEffects.size() < TribeEffectScreen.this.maxBadEffects && (numSelectedBad + amplifier <= maxBadEffects)) {
                        TribeEffectScreen.this.addEffect(effect, amplifier, isGood);
                    }
                    if(TribeEffectScreen.this.selBadEffects.containsKey(effect) && (numSelectedBad + amplifier <= maxBadEffects)){
                        TribeEffectScreen.this.removeEffect(effect, TribeEffectScreen.this.selBadEffects.get(effect), isGood);
                        TribeEffectScreen.this.addEffect(effect, amplifier, isGood);
                    }
                }
            }else if(this.isSelected()){
                TribeEffectScreen.this.removeEffect(effect, amplifier, isGood);
            }
            TribeEffectScreen.this.buttons.clear();
            TribeEffectScreen.this.children.clear();
            TribeEffectScreen.this.init();
            TribeEffectScreen.this.tick();
        }

        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            screen.renderTooltip(matrixStack, getEffectName(this.effect), mouseX, mouseY);
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
