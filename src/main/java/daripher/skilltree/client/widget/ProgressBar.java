package daripher.skilltree.client.widget;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.screen.ScreenHelper;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ProgressBar extends Button {
    public boolean showProgressInNumbers;

    public ProgressBar(int x, int y, OnPress pressFunc) {
        super(x, y, 235, 19, Component.empty(), pressFunc, Supplier::get);
    }

    private static int getCurrentLevel() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) return 0;
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        return capability.getCurrentLevel();  // NEW: Из PlayerSkills, не skills.size() + points
    }

    private static boolean isMaxLevel(int currentLevel) {
        return currentLevel == Integer.MAX_VALUE;
    }

    @Override
    public void renderWidget(
            @NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        renderCurrentLevel(graphics);
        renderNextLevel(graphics);
        renderProgress(graphics);
    }

    protected void renderBackground(GuiGraphics graphics) {
        float experienceProgress = getExperienceProgress();
        int filledBarWidth = (int) (experienceProgress * 183);
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/progress_bars.png");
        graphics.blit(texture, getX() + 26, getY() + 7, 0, 0, 182, 5);
        if (filledBarWidth == 0) return;
        graphics.blit(texture, getX() + 26, getY() + 7, 0, 5, filledBarWidth, 5);
    }

    protected void renderProgress(GuiGraphics graphics) {
        if (showProgressInNumbers) {
            int cost = SkillTreeClientData.getSkillPointCost(getCurrentLevel());
            int exp = getLocalPlayer().totalExperience;
            String text = exp + "/" + cost;
            ScreenHelper.drawCenteredOutlinedText(
                    graphics, text, getX() + width / 2, getTextY(), 0xFCE266);
        } else {
            float experienceProgress = getExperienceProgress();
            String text = (int) (experienceProgress * 100) + "%";
            ScreenHelper.drawCenteredOutlinedText(
                    graphics, text, getX() + width / 2, getTextY(), 0xFCE266);
        }
    }

    protected void renderNextLevel(GuiGraphics graphics) {
        int currentLevel = getCurrentLevel();
        if (isMaxLevel(currentLevel)) currentLevel--;
        int nextLevel = currentLevel + 1;
        ScreenHelper.drawCenteredOutlinedText(
                graphics, "" + nextLevel, getX() + width - 17, getTextY(), 0xFCE266);
    }

    protected void renderCurrentLevel(GuiGraphics graphics) {
        int currentLevel = getCurrentLevel();
        if (isMaxLevel(currentLevel)) currentLevel--;
        ScreenHelper.drawCenteredOutlinedText(
                graphics, "" + currentLevel, getX() + 17, getTextY(), 0xFCE266);
    }

    protected int getTextY() {
        return getY() + 5;
    }

    private float getExperienceProgress() {
        LocalPlayer player = getLocalPlayer();
        if (player == null) return 0F;
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        int currentXP = capability.getSkillExperience();  // NEW: Модовый XP
        int nextCost = capability.getNextLevelCost();  // NEW: Из PlayerSkills (формула)
        int level = getCurrentLevel();
        float progress = 1F;
        if (level < 100) {  // Или capability.getMaxLevel()
            progress = (float) currentXP / nextCost;
            progress = Math.min(1F, progress);
        }
        return progress;
    }

    private static LocalPlayer getLocalPlayer() {
        return Minecraft.getInstance().player;
    }
}
