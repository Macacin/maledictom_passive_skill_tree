package daripher.skilltree.skill.bonus.player.endurance;

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

public final class HungerReductionBonus implements SkillBonus<HungerReductionBonus> {
    public float reduction;
    private AttributeModifier.Operation operation;

    public HungerReductionBonus(float reduction, AttributeModifier.Operation operation) {
        this.reduction = reduction;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.HUNGER_REDUCTION.get();
    }

    @Override
    public HungerReductionBonus copy() {
        return new HungerReductionBonus(reduction, operation);
    }

    @Override
    public HungerReductionBonus multiply(double multiplier) {
        reduction *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof HungerReductionBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<HungerReductionBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof HungerReductionBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedReduction = otherBonus.reduction + this.reduction;
        return new HungerReductionBonus(mergedReduction, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("hunger_reduction", reduction, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return reduction > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<HungerReductionBonus> consumer) {
        editor.addLabel(0, 0, "Reduction", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, reduction).setNumericResponder(value -> setReduction(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getReduction(Player player) {
        return reduction;
    }

    private void setReduction(Consumer<HungerReductionBonus> consumer, float value) {
        reduction = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<HungerReductionBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public HungerReductionBonus deserialize(JsonObject json) throws JsonParseException {
            float reduction = json.get("reduction").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new HungerReductionBonus(reduction, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof HungerReductionBonus hBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("reduction", hBonus.reduction);
            SerializationHelper.serializeOperation(json, hBonus.operation);
        }

        @Override
        public HungerReductionBonus deserialize(CompoundTag tag) {
            float reduction = tag.getFloat("reduction");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new HungerReductionBonus(reduction, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof HungerReductionBonus hBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("reduction", hBonus.reduction);
            SerializationHelper.serializeOperation(tag, hBonus.operation);
            return tag;
        }

        @Override
        public HungerReductionBonus deserialize(FriendlyByteBuf buf) {
            float reduction = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new HungerReductionBonus(reduction, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof HungerReductionBonus hBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(hBonus.reduction);
            buf.writeInt(hBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new HungerReductionBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
}