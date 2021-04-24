package io.github.lukegrahamlandry.tribes.client;

import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

import java.util.Arrays;
import java.util.List;

public class TribeEffectScreen extends TribeScreen {
    private GuiButton.ConfirmButton confirmButton;
    List<Effect> posEffects = TribesConfig.getGoodEffects();
    List<Effect> negEffects = TribesConfig.getBadEffects();

    public TribeEffectScreen() {
        super(".tribeEffectScreen", "textures/gui/tribe_effects.png", 256, 216);
    }

    @Override
    public void tick() {
        int i=0;
        int k=0;
        for(Effect effect : posEffects){
            for(int j=0;j<3;j++) {
                GuiButton.EffectButton tribeeffect$effectbutton;
                if(i<=100){
                    tribeeffect$effectbutton = new GuiButton.EffectButton(this, this.guiLeft + 17 + (15*j), this.guiTop + 40 + i, ySize, effect, true);
                }else{
                    tribeeffect$effectbutton = new GuiButton.EffectButton(this, this.guiLeft + 60 + (15*j), this.guiTop + 40 + k, ySize, effect, true);
                }
                this.addButton(tribeeffect$effectbutton);
            }
            if(i<=100) i+=15; else k+=15;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.confirmButton = this.addButton(new GuiButton.ConfirmButton(this,this.guiLeft + 115, this.guiTop + 75));
    }
}
