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

public final class CurseChanceReductionBonus implements SkillBonus<CurseChanceReductionBonus> {
    public float reduction;
    private AttributeModifier.Operation operation;

    public CurseChanceReductionBonus(float reduction, AttributeModifier.Operation operation) {
        this.reduction = reduction;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.CURSE_CHANCE_REDUCTION.get();
    }

    @Override
    public CurseChanceReductionBonus copy() {
        return new CurseChanceReductionBonus(reduction, operation);
    }

    @Override
    public CurseChanceReductionBonus multiply(double multiplier) {
        reduction *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof CurseChanceReductionBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<CurseChanceReductionBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof CurseChanceReductionBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedReduction = otherBonus.reduction + this.reduction;
        return new CurseChanceReductionBonus(mergedReduction, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("curse_chance_reduction_bonus", reduction * 100, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return reduction > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<CurseChanceReductionBonus> consumer) {
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

    private void setReduction(Consumer<CurseChanceReductionBonus> consumer, float value) {
        reduction = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<CurseChanceReductionBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public CurseChanceReductionBonus deserialize(JsonObject json) throws JsonParseException {
            float reduction = json.get("reduction").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new CurseChanceReductionBonus(reduction, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof CurseChanceReductionBonus ccrBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("reduction", ccrBonus.reduction);
            SerializationHelper.serializeOperation(json, ccrBonus.operation);
        }

        @Override
        public CurseChanceReductionBonus deserialize(CompoundTag tag) {
            float reduction = tag.getFloat("reduction");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new CurseChanceReductionBonus(reduction, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof CurseChanceReductionBonus ccrBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("reduction", ccrBonus.reduction);
            SerializationHelper.serializeOperation(tag, ccrBonus.operation);
            return tag;
        }

        @Override
        public CurseChanceReductionBonus deserialize(FriendlyByteBuf buf) {
            float reduction = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new CurseChanceReductionBonus(reduction, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof CurseChanceReductionBonus ccrBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(ccrBonus.reduction);
            buf.writeInt(ccrBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new CurseChanceReductionBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}