package daripher.skilltree.skill.bonus.player.strength;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public final class ProjectileCritChanceBonus implements SkillBonus<ProjectileCritChanceBonus> {
    public float chance;
    private AttributeModifier.Operation operation;

    public ProjectileCritChanceBonus(float chance, AttributeModifier.Operation operation) {
        this.chance = chance;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.PROJECTILE_CRIT_CHANCE.get();
    }

    @Override
    public ProjectileCritChanceBonus copy() {
        return new ProjectileCritChanceBonus(chance, operation);
    }

    @Override
    public ProjectileCritChanceBonus multiply(double multiplier) {
        chance *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ProjectileCritChanceBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<ProjectileCritChanceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ProjectileCritChanceBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedChance = otherBonus.chance + this.chance;
        return new ProjectileCritChanceBonus(mergedChance, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("projectile_crit_chance", chance, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return chance > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ProjectileCritChanceBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, chance).setNumericResponder(value -> setChance(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getChance(Player player) {
        return chance;
    }

    private void setChance(Consumer<ProjectileCritChanceBonus> consumer, float value) {
        chance = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<ProjectileCritChanceBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ProjectileCritChanceBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = json.get("chance").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new ProjectileCritChanceBonus(chance, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileCritChanceBonus pcBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", pcBonus.chance);
            SerializationHelper.serializeOperation(json, pcBonus.operation);
        }

        @Override
        public ProjectileCritChanceBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new ProjectileCritChanceBonus(chance, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileCritChanceBonus pcBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", pcBonus.chance);
            SerializationHelper.serializeOperation(tag, pcBonus.operation);
            return tag;
        }

        @Override
        public ProjectileCritChanceBonus deserialize(FriendlyByteBuf buf) {
            float chance = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new ProjectileCritChanceBonus(chance, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileCritChanceBonus pcBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(pcBonus.chance);
            buf.writeInt(pcBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ProjectileCritChanceBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}