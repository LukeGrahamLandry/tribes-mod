package io.github.lukegrahamlandry.tribes.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.SaveEffectsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TribeEffectScreen extends TribeScreen {
    // Confirmation button
    private ConfirmButton confirmButton;
    // List of all positive and negative effects
    public static List<MobEffect> posEffects = TribesConfig.getGoodEffects();
    public static List<MobEffect> negEffects = TribesConfig.getBadEffects();
    // Map of selected effects and their amplifiers
    private Map<MobEffect, Integer> selGoodEffects;
    private Map<MobEffect, Integer> selBadEffects;
    // Maximum number of effects that can be selected; Both good and bad
    private int maxGoodEffects;
    private int maxBadEffects;
    // Current number of selected effects
    private static int numSelectedGood;
    private static int numSelectedBad;

    int page = 0;
    private Button backButton;
    private Button nextButton;
    final int EFFECTS_PER_PAGE = 14;

    public TribeEffectScreen(int numGoodAllowed, int numBadAllowed, HashMap<MobEffect, Integer> currentEffects) {
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
        addRenderableWidgets();
    }

    public void addRenderableWidgets(){
        this.backButton = this.addRenderableWidget(new Button(this.guiLeft + (this.xSize - 11), (this.ySize/2 - 11) + 30, 20, 20, new TextComponent("<"), (p_214318_1_) -> {
            if (this.backButton.active){
                this.page--;

                TribeEffectScreen.this.clearWidgets();
                TribeEffectScreen.this.init();
                TribeEffectScreen.this.tick();
            }
        }));
        this.backButton.active = this.page > 0;
        this.nextButton = this.addRenderableWidget(new Button(this.guiLeft + (this.xSize - 11), (this.ySize/2 - 11) + 60, 20, 20, new TextComponent(">"), (p_214318_1_) -> {
            if (this.nextButton.active){
                this.page++;

                TribeEffectScreen.this.clearWidgets();
                TribeEffectScreen.this.init();
                TribeEffectScreen.this.tick();
            }
        }));
        int shown = ((this.page + 1) * EFFECTS_PER_PAGE);
        this.nextButton.active = shown < posEffects.size() || shown < negEffects.size();



        // Declare confirm button
        this.confirmButton = this.addRenderableWidget(new ConfirmButton(this,this.guiLeft + (this.xSize - 11), (this.ySize/2 - 11), this.ySize));

        int shift = this.page * EFFECTS_PER_PAGE;
        int i=0;
        int k=0;
        // Iteration through effects to create three buttons for three tiers of each effect

        for (int e=0;e<EFFECTS_PER_PAGE;e++){
            int index = shift + e;
            if (index >= posEffects.size()) break;
            MobEffect effect = posEffects.get(index);

            EffectButton tribeeffect$effectbutton;
            i=(k>=154) ? 82 : i;
            k=(k>=154) ? 0 : k;
            for(int j=1; j<=3;j++){
                tribeeffect$effectbutton = new EffectButton(this, this.guiLeft + 11 + i, this.guiTop + 36 + k, this.ySize, effect, true, j);
                this.addRenderableWidget(tribeeffect$effectbutton);
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
        for (int e=0;e<EFFECTS_PER_PAGE;e++){
            int index = shift + e;
            if (index >= negEffects.size()) break;
            MobEffect effect = negEffects.get(index);

            EffectButton tribeeffect$effectbutton;
            i=(k>=154) ? 82 : i;
            k=(k>=154) ? 0 : k;
            for(int j=1; j<=3;j++){
                tribeeffect$effectbutton = new EffectButton(this, this.guiLeft + this.xSize + 16 + i, this.guiTop + 36 + k, ySize, effect, false, j);
                this.addRenderableWidget(tribeeffect$effectbutton);
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.font.draw(matrixStack, "Benefits: " + numSelectedGood+"/"+maxGoodEffects, this.guiLeft + 15, this.guiTop + 20, 0x5d5d5d);
        this.font.draw(matrixStack, "Drawbacks: " + numSelectedBad+"/"+maxBadEffects, this.guiLeft + 20 + xSize, this.guiTop + 20, 0x5d5d5d);
    }

    // Add effect to selected list
    private void addEffect(MobEffect effect, int amplifier, boolean isGood){
        if(isGood){
            selGoodEffects.put(effect, amplifier);
            numSelectedGood += amplifier;
        }else{
            selBadEffects.put(effect, amplifier);
            numSelectedBad += amplifier;
        }
    }

    // Remove effect from selected list
    private void removeEffect(MobEffect effect, int amplifier, boolean isGood){
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
        for(MobEffect effect : selGoodEffects.keySet()){
            numSelectedGood+=selGoodEffects.get(effect);
        }
        for(MobEffect effect : selBadEffects.keySet()){
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
                TribeEffectScreen.this.minecraft.setScreen(null);
            }
        }

        public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
            screen.renderTooltip(matrixStack, CommonComponents.GUI_DONE, mouseX, mouseY);
        }

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {

        }
    }

    // Effect Button
    @OnlyIn(Dist.CLIENT)
    class EffectButton extends GuiButton {
        private final TribeEffectScreen screen;
        private final MobEffect effect;
        private final boolean isGood;
        private final TextureAtlasSprite effectSprite;
        private final Component effectName;
        private final int amplifier;

        public EffectButton(TribeScreen screen, int x, int y, int ySizeIn, MobEffect p_i50827_4_, boolean isGoodIn, int amplifierIn) {
            super(screen, x, y, ySizeIn);
            this.effect = p_i50827_4_;
            this.isGood = isGoodIn;
            this.effectSprite = Minecraft.getInstance().getMobEffectTextures().get(p_i50827_4_);
            this.effectName = this.getEffectName(p_i50827_4_);
            this.screen = (TribeEffectScreen) screen;
            this.amplifier = amplifierIn;
        }

        // Get the name of the effect based on the amplifier
        private Component getEffectName(MobEffect effect) {
            TranslatableComponent iformattabletextcomponent = new TranslatableComponent(effect.getDescriptionId());
            if (this.getAmplifier() == 1) {
                iformattabletextcomponent.append(" I");
            }else if (this.getAmplifier() == 2) {
                iformattabletextcomponent.append(" II");
            }else if (this.getAmplifier() == 3) {
                iformattabletextcomponent.append(" III");
            }

            return iformattabletextcomponent;
        }

        public void onPress() {
            TribesMain.LOGGER.debug(effect + " " + amplifier);
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
            TribeEffectScreen.this.clearWidgets();
            TribeEffectScreen.this.init();
            TribeEffectScreen.this.tick();
        }

        public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
            screen.renderTooltip(matrixStack, getEffectName(this.effect), mouseX, mouseY);
        }

        public int getAmplifier(){
            return amplifier;
        }

        protected void renderIcon(PoseStack matrixStack) {
            TribeEffectScreen.this.font.draw(matrixStack, String.valueOf(this.getAmplifier()), (float)(this.x+2), (float)(this.y+2), 0xffffff);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.effectSprite.atlas().location());
            blit(matrixStack, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.effectSprite);
        }

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {

        }
    }
}
