package daripher.skilltree.skill.bonus.player.constitution;

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

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class NegativeEffectReductionBonus implements SkillBonus<NegativeEffectReductionBonus> {
    public float amount;
    private AttributeModifier.Operation operation;

    public NegativeEffectReductionBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.NEGATIVE_EFFECT_REDUCTION.get();
    }

    @Override
    public NegativeEffectReductionBonus copy() {
        return new NegativeEffectReductionBonus(amount, operation);
    }

    @Override
    public NegativeEffectReductionBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof NegativeEffectReductionBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<NegativeEffectReductionBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof NegativeEffectReductionBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        return new NegativeEffectReductionBonus(mergedAmount, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("negative_effect_reduction", amount * 100, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<NegativeEffectReductionBonus> consumer) {
        editor.addLabel(0, 0, "Amount (%)", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount * 100).setNumericResponder(value -> setAmount(consumer, value.floatValue() / 100));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getReduction() {
        return amount;
    }

    private void setAmount(Consumer<NegativeEffectReductionBonus> consumer, float value) {
        amount = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<NegativeEffectReductionBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public NegativeEffectReductionBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = json.get("amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new NegativeEffectReductionBonus(amount, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof NegativeEffectReductionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", aBonus.amount);
            SerializationHelper.serializeOperation(json, aBonus.operation);
        }

        @Override
        public NegativeEffectReductionBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new NegativeEffectReductionBonus(amount, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof NegativeEffectReductionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", aBonus.amount);
            SerializationHelper.serializeOperation(tag, aBonus.operation);
            return tag;
        }

        @Override
        public NegativeEffectReductionBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new NegativeEffectReductionBonus(amount, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof NegativeEffectReductionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.amount);
            buf.writeInt(aBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new NegativeEffectReductionBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}