package daripher.skilltree.skill.bonus.player.intelligence;

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

public final class MaxEnchantLevelBonus implements SkillBonus<MaxEnchantLevelBonus> {
    public float level;
    private AttributeModifier.Operation operation;

    public MaxEnchantLevelBonus(float level, AttributeModifier.Operation operation) {
        this.level = level;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.MAX_ENCHANT_LEVEL.get();
    }

    @Override
    public MaxEnchantLevelBonus copy() {
        return new MaxEnchantLevelBonus(level, operation);
    }

    @Override
    public MaxEnchantLevelBonus multiply(double multiplier) {
        level *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof MaxEnchantLevelBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<MaxEnchantLevelBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof MaxEnchantLevelBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedLevel = otherBonus.level + this.level;
        return new MaxEnchantLevelBonus(mergedLevel, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("max_enchant_level_bonus", level, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return level > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<MaxEnchantLevelBonus> consumer) {
        editor.addLabel(0, 0, "Level", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, level).setNumericResponder(value -> setLevel(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getLevelBonus(Player player) {
        return level;
    }

    private void setLevel(Consumer<MaxEnchantLevelBonus> consumer, float value) {
        level = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<MaxEnchantLevelBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public MaxEnchantLevelBonus deserialize(JsonObject json) throws JsonParseException {
            float level = json.get("level").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new MaxEnchantLevelBonus(level, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof MaxEnchantLevelBonus melBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("level", melBonus.level);
            SerializationHelper.serializeOperation(json, melBonus.operation);
        }

        @Override
        public MaxEnchantLevelBonus deserialize(CompoundTag tag) {
            float level = tag.getFloat("level");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new MaxEnchantLevelBonus(level, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof MaxEnchantLevelBonus melBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("level", melBonus.level);
            SerializationHelper.serializeOperation(tag, melBonus.operation);
            return tag;
        }

        @Override
        public MaxEnchantLevelBonus deserialize(FriendlyByteBuf buf) {
            float level = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new MaxEnchantLevelBonus(level, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof MaxEnchantLevelBonus melBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(melBonus.level);
            buf.writeInt(melBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new MaxEnchantLevelBonus(1f, AttributeModifier.Operation.ADDITION);
        }
    }
}