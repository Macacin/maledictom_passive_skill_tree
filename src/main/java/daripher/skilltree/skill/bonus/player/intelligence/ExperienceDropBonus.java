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

public final class ExperienceDropBonus implements SkillBonus<ExperienceDropBonus> {
    public float multiplier;
    private AttributeModifier.Operation operation;

    public ExperienceDropBonus(float multiplier, AttributeModifier.Operation operation) {
        this.multiplier = multiplier;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.EXPERIENCE_DROP.get();
    }

    @Override
    public ExperienceDropBonus copy() {
        return new ExperienceDropBonus(multiplier, operation);
    }

    @Override
    public ExperienceDropBonus multiply(double multiplier) {
        this.multiplier *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ExperienceDropBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<ExperienceDropBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ExperienceDropBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedMultiplier = otherBonus.multiplier + this.multiplier;
        return new ExperienceDropBonus(mergedMultiplier, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("experience_drop_bonus", multiplier * 100, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return multiplier > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ExperienceDropBonus> consumer) {
        editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, multiplier).setNumericResponder(value -> setMultiplier(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getMultiplier(Player player) {
        return multiplier;
    }

    private void setMultiplier(Consumer<ExperienceDropBonus> consumer, float value) {
        multiplier = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<ExperienceDropBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ExperienceDropBonus deserialize(JsonObject json) throws JsonParseException {
            float multiplier = json.get("multiplier").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new ExperienceDropBonus(multiplier, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ExperienceDropBonus edBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("multiplier", edBonus.multiplier);
            SerializationHelper.serializeOperation(json, edBonus.operation);
        }

        @Override
        public ExperienceDropBonus deserialize(CompoundTag tag) {
            float multiplier = tag.getFloat("multiplier");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new ExperienceDropBonus(multiplier, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ExperienceDropBonus edBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("multiplier", edBonus.multiplier);
            SerializationHelper.serializeOperation(tag, edBonus.operation);
            return tag;
        }

        @Override
        public ExperienceDropBonus deserialize(FriendlyByteBuf buf) {
            float multiplier = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new ExperienceDropBonus(multiplier, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ExperienceDropBonus edBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(edBonus.multiplier);
            buf.writeInt(edBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ExperienceDropBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
}