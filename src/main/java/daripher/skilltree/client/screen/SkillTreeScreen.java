package daripher.skilltree.client.screen;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.systems.RenderSystem;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillConnection;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SkillTreeScreen extends Screen {
  public static final int BACKGROUND_SIZE = 2048;
  private final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
  private final List<SkillConnection> skillConnections = new ArrayList<>();
  private final List<ResourceLocation> learnedSkills = new ArrayList<>();
  public final List<ResourceLocation> newlyLearnedSkills = new ArrayList<>();
  private final List<SkillButton> startingPoints = new ArrayList<>();
  private final PassiveSkillTree skillTree;
  private String search = "";
  public float renderAnimation;
  public int skillPoints;
  protected double scrollSpeedX;
  protected double scrollSpeedY;
  protected double scrollX;
  protected double scrollY;
  protected int maxScrollX;
  protected int maxScrollY;
  private Label pointsInfo;  // Расширим для Level + XP + Points
  private ProgressBar progressBar;  // NEW: Всегда visible для модового XP
  private Label levelInfo;  // NEW: Label для Level и XP
  private ScrollableComponentList statsInfo;
  private boolean firstInitDone;
  private boolean showStats;
  private boolean showProgressInNumbers;
  private int prevMouseX;
  private int prevMouseY;
  private float zoom = 1F;

  public SkillTreeScreen(ResourceLocation skillTreeId) {
    super(Component.empty());
    this.skillTree = SkillTreesReloader.getSkillTreeById(skillTreeId);
    this.minecraft = Minecraft.getInstance();
  }

  @Override
  public void init() {
    clearWidgets();
    // NEW: ProgressBar всегда visible, обновим в update
    progressBar =
            new ProgressBar(
                    width / 2 - 235 / 2,
                    height - 17,
                    b -> {
                      progressBar.showProgressInNumbers ^= true;
                      showProgressInNumbers ^= true;
                    });
    progressBar.showProgressInNumbers = showProgressInNumbers;
    progressBar.visible = true;  // NEW: Всегда показываем для модового XP
    addRenderableWidget(progressBar);
    addTopWidgets();
    // Удалить: if (!SkillTreeClientData.enable_exp_exchange) { ... }  // Нет зависимости от config
    if (!firstInitDone) firstInit();
    addSkillButtons();
    statsInfo = new ScrollableComponentList(48, height - 60);
    statsInfo.setComponents(getMergedSkillBonusesTooltips());
    addRenderableWidget(statsInfo);
    maxScrollX -= width / 2 - 80;
    maxScrollY -= height / 2 - 80;
    if (maxScrollX < 0) maxScrollX = 0;
    if (maxScrollY < 0) maxScrollY = 0;
    addSkillConnections();
    highlightSkillsThatCanBeLearned();
    updateSearch();
  }

  private void addTopWidgets() {
    Component pointsInfoText = Component.translatable("widget.skill_points_left", 100);  // Адаптируем позже
    Component confirmButtonText = Component.translatable("widget.confirm_button");
    Component cancelButtonText = Component.translatable("widget.cancel_button");
    Component showStatsButtonText = Component.translatable("widget.show_stats");
    int buttonWidth = Math.max(font.width(pointsInfoText), font.width(confirmButtonText));
    buttonWidth = Math.max(buttonWidth, font.width(cancelButtonText));
    buttonWidth += 20;
    int buttonsY = 8;
    Button showStatsButton =
            new Button(width - buttonWidth - 8, buttonsY, buttonWidth, 14, showStatsButtonText);
    showStatsButton.setPressFunc(b -> showStats ^= true);
    addRenderableWidget(showStatsButton);
    addRenderableWidget(new TextField(8, buttonsY, buttonWidth, 14, search))
            .setHint("Search...")
            .setResponder(
                    s -> {
                      search = s;
                      updateSearch();
                    });

    int infoWidth = 210;
    pointsInfo = new Label((width / 2) - (infoWidth / 2), buttonsY, infoWidth, 14, Component.empty());
    addRenderableWidget(pointsInfo);
    pointsInfo.active = false;
    pointsInfo.visible = true;
    buttonsY += 20;
    Button confirmButton =
            new Button(width / 2 - 8 - buttonWidth, buttonsY, buttonWidth, 14, confirmButtonText);
    confirmButton.setPressFunc(b -> confirmLearnSkills());
    addRenderableWidget(confirmButton);
    Button cancelButton = new Button(width / 2 + 8, buttonsY, buttonWidth, 14, cancelButtonText);
    cancelButton.setPressFunc(b -> cancelLearnSkills());
    addRenderableWidget(cancelButton);
    confirmButton.active = cancelButton.active = !newlyLearnedSkills.isEmpty();
  }

  private void updateSearch() {
    if (search.isEmpty()) {
      for (SkillButton button : skillButtons.values()) {
        button.searched = false;
      }
      return;
    }
    outerLoop:
    for (SkillButton button : skillButtons.values()) {
      for (MutableComponent component : button.getSkillTooltip(skillTree)) {
        if (component.getString().toLowerCase().contains(search.toLowerCase())) {
          button.searched = true;
          continue outerLoop;
        }
      }
      button.searched = false;
    }
  }

  @Override
  public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    updateScreen(partialTick);
    renderAnimation += partialTick;
    renderBackground(graphics);
    renderConnections(graphics, mouseX, mouseY);
    renderSkills(graphics, mouseX, mouseY, partialTick);
    renderOverlay(graphics);
    renderWidgets(graphics, mouseX, mouseY, partialTick);
    renderSkillTooltip(graphics, mouseX, mouseY, partialTick);
    prevMouseX = mouseX;
    prevMouseY = mouseY;
  }

  private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    IPlayerSkills skills = PlayerSkillsProvider.get(getPlayer());
    int points = skills.getSkillPoints();
    int level = skills.getCurrentLevel();
    int currentXP = skills.getSkillExperience();
    int nextCost = skills.getNextLevelCost();
    MutableComponent pointsLeft = Component.literal("Points: " + points).withStyle(Style.EMPTY.withColor(0xFCE266));
    MutableComponent levelText = Component.literal("Level: " + level).withStyle(Style.EMPTY.withColor(0xFFFFFF));
    pointsInfo.setMessage(levelText.append(" | ").append(pointsLeft));
    statsInfo.setX(width - statsInfo.getWidth() - 10);
    statsInfo.visible = showStats;
    for (Renderable widget : renderables) {
      if (widget instanceof SkillButton) continue;
      widget.render(graphics, mouseX, mouseY, partialTick);
    }
  }

  private void renderSkillTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    if (getWidgetAt(mouseX, mouseY).isPresent()) return;
    SkillButton skill = getSkillAt(mouseX, mouseY);
    if (skill == null) return;
    float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
    float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
    ScreenHelper.renderSkillTooltip(skillTree, skill, graphics, tooltipX, tooltipY, width, height);
  }

  private void renderSkills(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    graphics.pose().pushPose();
    graphics.pose().translate(scrollX, scrollY, 0);
    for (SkillButton widget : skillButtons.values()) {
      graphics.pose().pushPose();
      graphics
              .pose()
              .translate(widget.x + widget.getWidth() / 2d, widget.y + widget.getHeight() / 2d, 0F);
      graphics.pose().scale(zoom, zoom, 1F);
      graphics
              .pose()
              .translate(-widget.x - widget.getWidth() / 2d, -widget.y - widget.getHeight() / 2d, 0F);
      widget.render(graphics, mouseX, mouseY, partialTick);
      graphics.pose().popPose();
    }
    graphics.pose().popPose();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    Optional<GuiEventListener> widget = getWidgetAt(mouseX, mouseY);
    if (widget.isPresent()) {
      widget.get().setFocused(true);
      return widget.get().mouseClicked(mouseX, mouseY, button);
    }
    SkillButton skill = getSkillAt(mouseX, mouseY);
    if (skill == null) return false;
    if (button == 0) {
      playButtonSound();
      skillButtonPressed(skill);
      return true;
    } else if (button == 1) {
      ClientConfig.toggleFavoriteSkill(skill.skill);
      playButtonSound();
      return true;
    }
    return false;
  }

  private void playButtonSound() {
    getMinecraft()
            .getSoundManager()
            .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
  }

  @Override
  public void tick() {
    textFields().forEach(EditBox::tick);
    IPlayerSkills skills = PlayerSkillsProvider.get(getPlayer());
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyPressedOnTextField(keyCode, scanCode, modifiers)) {
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    textFields().toList().forEach(b -> b.keyReleased(keyCode, scanCode, modifiers));
    return super.keyReleased(keyCode, scanCode, modifiers);  // Фикс: keyReleased, не keyPressed
  }

  @Override
  public boolean charTyped(char character, int keyCode) {
    for (EditBox textField : textFields().toList()) {
      if (textField.charTyped(character, keyCode)) {
        return true;
      }
    }
    return false;
  }

  private boolean keyPressedOnTextField(int keyCode, int scanCode, int modifiers) {
    return textFields().toList().stream().anyMatch(b -> b.keyPressed(keyCode, scanCode, modifiers));
  }

  private Stream<EditBox> textFields() {
    return children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast);
  }

  public Optional<GuiEventListener> getWidgetAt(double mouseX, double mouseY) {
    Predicate<GuiEventListener> isSkillButton = SkillButton.class::isInstance;
    return super.getChildAt(mouseX, mouseY).filter(isSkillButton.negate());
  }

  public @Nullable SkillButton getSkillAt(double mouseX, double mouseY) {
    mouseX -= scrollX;
    mouseY -= scrollY;
    for (SkillButton button : skillButtons.values()) {
      double skillSize = button.skill.getSkillSize() * zoom;
      double skillX = button.x + button.getWidth() / 2d - skillSize / 2;
      double skillY = button.y + button.getHeight() / 2d - skillSize / 2;
      if (mouseX >= skillX
              && mouseY >= skillY
              && mouseX < skillX + skillSize
              && mouseY < skillY + skillSize) {
        return button;
      }
    }
    return null;
  }

  private List<Component> getMergedSkillBonusesTooltips() {
    List<SkillBonus<?>> bonuses = new ArrayList<>();
    learnedSkills.stream()
            .map(skillButtons::get)
            .map(button -> button.skill)
            .map(PassiveSkill::getBonuses)
            .flatMap(List::stream)
            .forEach(b -> addToMergeList(b, bonuses));
    return bonuses.stream()
            .sorted()
            .map(SkillBonus::getTooltip)
            .map(Component.class::cast)
            .toList();
  }

  private static void addToMergeList(SkillBonus<?> b, List<SkillBonus<?>> bonuses) {
    Optional<SkillBonus<?>> same = bonuses.stream().filter(b::canMerge).findAny();
    if (same.isPresent()) {
      bonuses.remove(same.get());
      bonuses.add(same.get().merge(b));
    } else {
      bonuses.add(b);
    }
  }

  protected void firstInit() {
    IPlayerSkills capability = PlayerSkillsProvider.get(getPlayer());
    List<PassiveSkill> skills = capability.getPlayerSkills();
    skills.stream().map(PassiveSkill::getId).forEach(learnedSkills::add);
    skillPoints = capability.getSkillPoints();
    updateProgressDisplay();
    firstInitDone = true;
  }

  public void addSkillButtons() {
    startingPoints.clear();
    skillButtons.clear();
    skillTree.getSkillIds().forEach(this::addSkillButton);
  }

  protected void addSkillButton(ResourceLocation skillId) {
    PassiveSkill skill = SkillsReloader.getSkillById(skillId);
    if (skill == null) return;
    float buttonX = getSkillButtonX(skill);
    float buttonY = getSkillButtonY(skill);
    SkillButton button = new SkillButton(this::getAnimation, buttonX, buttonY, skill);
    addRenderableWidget(button);
    skillButtons.put(skillId, button);
    if (skill.isStartingPoint()) startingPoints.add(button);
    if (isSkillLearned(skill)) button.skillLearned = true;
    if (maxScrollX < Mth.abs(skill.getPositionX()))
      maxScrollX = (int) Mth.abs(skill.getPositionX());
    if (maxScrollY < Mth.abs(skill.getPositionY()))
      maxScrollY = (int) Mth.abs(skill.getPositionY());
  }

  private float getSkillButtonX(PassiveSkill skill) {
    float skillX = skill.getPositionX();
    return skillX - skill.getSkillSize() / 2F + width / 2F + skillX * (zoom - 1);
  }

  private float getSkillButtonY(PassiveSkill skill) {
    float skillY = skill.getPositionY();
    return skillY - skill.getSkillSize() / 2F + height / 2F + skillY * (zoom - 1);
  }

  protected boolean isSkillLearned(PassiveSkill skill) {
    return learnedSkills.contains(skill.getId()) || newlyLearnedSkills.contains(skill.getId());
  }

  public void addSkillConnections() {
    skillConnections.clear();
    getTreeSkills().forEach(this::addSkillConnections);
  }

  private Stream<PassiveSkill> getTreeSkills() {
    return skillTree.getSkillIds().stream()
            .map(SkillsReloader::getSkillById)
            .filter(Objects::nonNull);
  }

  private void addSkillConnections(PassiveSkill skill) {
    if (skill == null) return;
    skill.getDirectConnections().stream()
            .filter(id -> skillButtons.containsKey(id))
            .forEach(id -> connectSkills(SkillConnection.Type.DIRECT, skill.getId(), id));
    skill.getLongConnections().stream()
            .filter(id -> skillButtons.containsKey(id))
            .forEach(id -> connectSkills(SkillConnection.Type.LONG, skill.getId(), id));
    skill.getOneWayConnections().stream()
            .filter(id -> skillButtons.containsKey(id))
            .forEach(id -> connectSkills(SkillConnection.Type.ONE_WAY, skill.getId(), id));
  }

  protected void connectSkills(
          SkillConnection.Type type, ResourceLocation skillId1, ResourceLocation skillId2) {
    SkillButton button1 = skillButtons.get(skillId1);
    SkillButton button2 = skillButtons.get(skillId2);
    if (button1 != null && button2 != null) {
      skillConnections.add(new SkillConnection(type, button1, button2));
    }
  }

  private void highlightSkillsThatCanBeLearned() {
    if (skillPoints == 0) return;
    if (learnedSkills.isEmpty() && newlyLearnedSkills.isEmpty()) {
      startingPoints.forEach(SkillButton::setCanLearn);
      return;
    }
    if (learnedSkills.size() + newlyLearnedSkills.size() >= SkillTreeClientData.max_skill_points)
      return;
    skillConnections.forEach(
            connection -> {
              SkillButton button1 = connection.getFirstButton();
              SkillButton button2 = connection.getSecondButton();
              if (button1.skillLearned == button2.skillLearned) return;
              if (connection.getType() != SkillConnection.Type.ONE_WAY) {
                if (!button1.skillLearned && canLearnSkill(button1.skill)) {
                  button1.setCanLearn();
                  button1.setActive();
                }
              }
              if (!button2.skillLearned && canLearnSkill(button2.skill)) {
                button2.setCanLearn();
                button2.setActive();
              }
            });
  }

  private boolean canLearnSkill(PassiveSkill skill) {
    Map<String, Integer> limitations = skillTree.getSkillLimitations();
    for (String tag : skill.getTags()) {
      int limit = limitations.getOrDefault(tag, 0);
      if (limit > 0 && getLearnedSkillsWithTag(tag) >= limit) return false;
    }
    return true;
  }

  private void updateProgressDisplay() {
    IPlayerSkills skills = PlayerSkillsProvider.get(getPlayer());
    skillPoints = skills.getSkillPoints();  // Синхронизируй field skillPoints с capability
    // Labels и bar обновятся в renderWidgets() автоматически
  }

  private long getLearnedSkillsWithTag(String tag) {
    return Streams.concat(learnedSkills.stream(), newlyLearnedSkills.stream())
            .map(SkillsReloader::getSkillById)
            .filter(Objects::nonNull)
            .filter(skill -> skill.getTags().contains(tag))
            .count();
  }

  private void confirmLearnSkills() {
    newlyLearnedSkills.forEach(id -> learnSkill(skillButtons.get(id).skill));
    newlyLearnedSkills.clear();
  }

  private void cancelLearnSkills() {
    skillPoints += newlyLearnedSkills.size();
    newlyLearnedSkills.clear();
    rebuildWidgets();
  }

  // Удалить: private void buySkillPoint() { ... }  // Нет покупки
  // Удалить: private boolean canBuySkillPoint(int currentLevel) { ... }
  // Удалить: private boolean isMaxLevel(int currentLevel) { ... }

  // Фикс: Теперь используй из PlayerSkills
  private int getCurrentLevel() {
    IPlayerSkills capability = PlayerSkillsProvider.get(getPlayer());
    return capability.getCurrentLevel();  // NEW: Из capability, не learned + points
  }

  protected void skillButtonPressed(SkillButton button) {
    Objects.requireNonNull(minecraft);
    PassiveSkill skill = button.skill;
    if (!newlyLearnedSkills.isEmpty()) {
      int lastLearned = newlyLearnedSkills.size() - 1;
      if (newlyLearnedSkills.get(lastLearned).equals(skill.getId())) {
        skillPoints++;
        newlyLearnedSkills.remove(lastLearned);
        rebuildWidgets();
        return;
      }
    }
    if (button.canLearn) {
      skillPoints--;  // Трата point (без XP-check)
      newlyLearnedSkills.add(skill.getId());
      rebuildWidgets();
      return;
    }
    ResourceLocation connectedTree = skill.getConnectedTreeId();
    if (connectedTree != null) {
      minecraft.setScreen(new SkillTreeScreen(connectedTree));
    }
  }

  protected void learnSkill(PassiveSkill skill) {
    learnedSkills.add(skill.getId());
    NetworkDispatcher.network_channel.sendToServer(new LearnSkillMessage(skill));
    rebuildWidgets();
  }

  private void updateScreen(float partialTick) {
    // Удалить: updateBuyPointButton();  // Нет кнопки
    scrollX += scrollSpeedX * partialTick;
    scrollX = Math.max(-maxScrollX * zoom, Math.min(maxScrollX * zoom, scrollX));
    scrollSpeedX *= 0.8;
    scrollY += scrollSpeedY * partialTick;
    scrollY = Math.max(-maxScrollY * zoom, Math.min(maxScrollY * zoom, scrollY));
    scrollSpeedY *= 0.8;
  }

  // Удалить: protected void updateBuyPointButton() { ... }

  private void renderOverlay(GuiGraphics graphics) {
    ResourceLocation texture =
            new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png");
    RenderSystem.enableBlend();
    graphics.blit(texture, 0, 0, 0, 0F, 0F, width, height, width, height);
    RenderSystem.disableBlend();
  }

  @Override
  public void renderBackground(GuiGraphics graphics) {
    ResourceLocation texture =
            new ResourceLocation("skilltree:textures/screen/skill_tree_background.png");
    graphics.pose().pushPose();
    graphics.pose().translate(scrollX / 3F, scrollY / 3F, 0);
    int size = BACKGROUND_SIZE;
    graphics.blit(
            texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
    graphics.pose().popPose();
  }

  @Override
  public boolean mouseDragged(
          double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
    if (mouseButton != 0 && mouseButton != 2) return false;
    if (maxScrollX > 0) scrollSpeedX += dragAmountX * 0.25;
    if (maxScrollY > 0) scrollSpeedY += dragAmountY * 0.25;
    return true;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (getWidgetAt(mouseX, mouseY).filter(ScrollableComponentList.class::isInstance).isEmpty()) {
      if (amount > 0 && zoom < 2F) zoom += 0.05f;
      if (amount < 0 && zoom > 0.25F) zoom -= 0.05f;
      rebuildWidgets();
    }
    return super.mouseScrolled(mouseX, mouseY, amount);
  }

  protected void renderConnections(GuiGraphics graphics, int mouseX, int mouseY) {
    skillConnections.stream()
            .filter(c -> c.getType() == SkillConnection.Type.DIRECT)
            .forEach(c -> renderDirectConnection(graphics, c));
    skillConnections.stream()
            .filter(c -> c.getType() == SkillConnection.Type.LONG)
            .forEach(c -> renderLongConnection(graphics, c, mouseX, mouseY));
    skillConnections.stream()
            .filter(c -> c.getType() == SkillConnection.Type.ONE_WAY)
            .forEach(c -> renderOneWayConnection(graphics, c));
  }

  private void renderDirectConnection(GuiGraphics graphics, SkillConnection c) {
    ScreenHelper.renderConnection(graphics, scrollX, scrollY, c, zoom, renderAnimation);
  }

  private void renderLongConnection(
          GuiGraphics graphics, SkillConnection connection, int mouseX, int mouseY) {
    SkillButton button1 = connection.getFirstButton();
    SkillButton button2 = connection.getSecondButton();
    SkillButton hoveredSkill = getSkillAt(mouseX, mouseY);
    boolean learned = isSkillLearned(button1.skill) || isSkillLearned(button2.skill);
    boolean hovered = hoveredSkill == button1 || hoveredSkill == button2;
    if (learned || hovered) {
      ScreenHelper.renderGatewayConnection(
              graphics, scrollX, scrollY, connection, learned, zoom, renderAnimation);
    }
  }

  private void renderOneWayConnection(GuiGraphics graphics, SkillConnection connection) {
    boolean highlighted = isSkillLearned(connection.getFirstButton().skill);
    ScreenHelper.renderOneWayConnection(
            graphics, scrollX, scrollY, connection, highlighted, zoom, renderAnimation);
  }

  public float getAnimation() {
    return renderAnimation;
  }

  private @Nonnull LocalPlayer getPlayer() {
    return Objects.requireNonNull(getMinecraft().player);
  }
}