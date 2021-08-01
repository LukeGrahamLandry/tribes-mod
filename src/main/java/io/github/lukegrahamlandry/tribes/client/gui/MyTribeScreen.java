package io.github.lukegrahamlandry.tribes.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketLeaveTribe;
import io.github.lukegrahamlandry.tribes.network.PacketSendEffects;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class MyTribeScreen extends TribeScreen {
    private final String tribeName;
    private final String rank;
    private final String owner;
    private final int members;
    private final int tier;
    private final List<String> goodTribes;
    private final List<String> badTribes;
    private Button effectsButton;
    private Button leaveButton;
    private Button backButton;
    private Button nextButton;
    private int page = 0;

    public MyTribeScreen(String tribeName, String rank, String owner, int members, int tier, List<String> goodTribes, List<String> badTribes) {
        super(".joinTribeScreen", "textures/gui/join_tribe.png", 185, 160, false);
        this.tribeName = tribeName;
        this.rank = rank;
        this.owner = owner;
        this.members = members;
        this.tier = tier;
        this.goodTribes = goodTribes;
        this.badTribes = badTribes;
    }

    @Override
    public void tick() {

    }

    @Override
    protected void init() {
        // this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;


        this.effectsButton = this.addButton(new Button(this.guiLeft + 15, this.guiTop + 90, 75, 20, new StringTextComponent("Effects"), (p_214318_1_) -> {
            if (this.effectsButton.active){
                NetworkHandler.INSTANCE.sendToServer(new PacketSendEffects());
                this.onClose();
            }
        }));
        this.effectsButton.active = this.rank.equals("leader");

        this.leaveButton = this.addButton(new Button(this.guiLeft + 15, this.guiTop + 125, 75, 20, new StringTextComponent("Leave Tribe"), (p_214318_1_) -> {
            this.onClose();
            NetworkHandler.INSTANCE.sendToServer(new PacketLeaveTribe());
        }));

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        int x = (this.width - this.xSize) / 2 + 15;
        int baseY = (this.height - this.ySize) / 2 + 20;

        this.write(matrixStack, this.tribeName, x, baseY, GREY);
        this.write(matrixStack, "Rank: " + this.rank, x, baseY + 15, GREY);
        this.write(matrixStack, "Owner: " + this.owner, x, baseY + 30, GREY);
        this.write(matrixStack, "Members: " + this.members, x, baseY + 45, GREY);
        this.write(matrixStack, "Tier: " + this.tier, x, baseY + 60, GREY);

        int y = baseY;
        int middleX = x + 85;
        int gap = 12;
        for (String name : this.goodTribes){
            this.write(matrixStack, name, middleX, y, GREEN);
            y += gap;
        }
        for (String name : this.badTribes){
            this.write(matrixStack, name, middleX, y, RED);
            y += gap;
        }
    }

    final int GREY = 4210752;
    final int GREEN = 0x00FF00;
    final int RED = 0xFF0000;

    private void write(MatrixStack matrixStack, String text, int x, int y, int color){
        this.font.draw(matrixStack, new StringTextComponent(text), x, y, color);
    }
}
