package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketCreateTribe;
import io.github.lukegrahamlandry.tribes.network.PacketLeaveTribe;
import io.github.lukegrahamlandry.tribes.network.PacketSendEffects;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class MyTribeScreen extends TribeScreen {
    private final String tribeName;
    private final String rank;
    private final String owner;
    private final int members;
    private final int tier;
    private Button effectsButton;
    private Button leaveButton;
    private Button backButton;
    private Button nextButton;
    private int page = 0;

    public MyTribeScreen(String tribeName, String rank, String owner, int members, int tier) {
        super(".joinTribeScreen", "textures/gui/join_tribe.png", 185, 160, false);
        this.tribeName = tribeName;
        this.rank = rank;
        this.owner = owner;
        this.members = members;
        this.tier = tier;
    }

    @Override
    public void tick() {

    }

    @Override
    protected void init() {
        // this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;


        this.effectsButton = this.addButton(new Button(this.guiLeft + 15, this.guiTop + 90, 150, 20, new StringTextComponent("Effects"), (p_214318_1_) -> {
            if (this.effectsButton.active){
                NetworkHandler.INSTANCE.sendToServer(new PacketSendEffects());
                this.closeScreen();
            }
        }));
        this.effectsButton.active = this.rank.equals("leader");

        this.leaveButton = this.addButton(new Button(this.guiLeft + 15, this.guiTop + 125, 150, 20, new StringTextComponent("Leave Tribe"), (p_214318_1_) -> {
            this.closeScreen();
            NetworkHandler.INSTANCE.sendToServer(new PacketLeaveTribe());
        }));

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        int x = (this.width - this.xSize) / 2 + 15;
        int baseY = (this.height - this.ySize) / 2 + 20;

        this.font.func_243248_b(matrixStack, new StringTextComponent(this.tribeName), x, baseY, 4210752);
        this.font.func_243248_b(matrixStack, new StringTextComponent("Rank: " + this.rank), x, baseY + 15, 4210752);
        this.font.func_243248_b(matrixStack, new StringTextComponent("Owner: " + this.owner), x, baseY + 30, 4210752);
        this.font.func_243248_b(matrixStack, new StringTextComponent("Members: " + this.members), x, baseY + 45, 4210752);
        this.font.func_243248_b(matrixStack, new StringTextComponent("Tier: " + this.tier), x, baseY + 60, 4210752);
    }
}
