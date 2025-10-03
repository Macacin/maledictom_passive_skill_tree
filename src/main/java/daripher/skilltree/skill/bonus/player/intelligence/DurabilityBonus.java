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

public final class DurabilityBonus implements SkillBonus<DurabilityBonus> {
    public float multiplier;
    private AttributeModifier.Operation operation;

    public DurabilityBonus(float multiplier, AttributeModifier.Operation operation) {
        this.multiplier = multiplier;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.DURABILITY_BONUS.get();
    }

    @Override
    public DurabilityBonus copy() {
        return new DurabilityBonus(multiplier, operation);
    }

    @Override
    public DurabilityBonus multiply(double multiplier) {
        this.multiplier *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof DurabilityBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<DurabilityBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof DurabilityBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedMultiplier = otherBonus.multiplier + this.multiplier;
        return new DurabilityBonus(mergedMultiplier, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("durability_bonus", multiplier * 100, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return multiplier > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<DurabilityBonus> consumer) {
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

    private void setMultiplier(Consumer<DurabilityBonus> consumer, float value) {
        multiplier = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<DurabilityBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public DurabilityBonus deserialize(JsonObject json) throws JsonParseException {
            float multiplier = json.get("multiplier").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new DurabilityBonus(multiplier, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof DurabilityBonus dBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("multiplier", dBonus.multiplier);
            SerializationHelper.serializeOperation(json, dBonus.operation);
        }

        @Override
        public DurabilityBonus deserialize(CompoundTag tag) {
            float multiplier = tag.getFloat("multiplier");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new DurabilityBonus(multiplier, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof DurabilityBonus dBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("multiplier", dBonus.multiplier);
            SerializationHelper.serializeOperation(tag, dBonus.operation);
            return tag;
        }

        @Override
        public DurabilityBonus deserialize(FriendlyByteBuf buf) {
            float multiplier = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new DurabilityBonus(multiplier, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof DurabilityBonus dBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(dBonus.multiplier);
            buf.writeInt(dBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new DurabilityBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
}