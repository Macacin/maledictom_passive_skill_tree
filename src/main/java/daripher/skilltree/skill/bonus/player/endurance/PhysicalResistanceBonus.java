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

public final class PhysicalResistanceBonus implements SkillBonus<PhysicalResistanceBonus> {
    public float bonus;
    private AttributeModifier.Operation operation;

    public PhysicalResistanceBonus(float bonus, AttributeModifier.Operation operation) {
        this.bonus = bonus;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.PHYSICAL_RESISTANCE.get();
    }

    @Override
    public PhysicalResistanceBonus copy() {
        return new PhysicalResistanceBonus(bonus, operation);
    }

    @Override
    public PhysicalResistanceBonus multiply(double multiplier) {
        bonus *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof PhysicalResistanceBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<PhysicalResistanceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof PhysicalResistanceBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedBonus = otherBonus.bonus + this.bonus;
        return new PhysicalResistanceBonus(mergedBonus, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("physical_resistance", bonus, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return bonus > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<PhysicalResistanceBonus> consumer) {
        editor.addLabel(0, 0, "Bonus", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, bonus).setNumericResponder(value -> setBonus(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getResistanceBonus(Player player) {
        return bonus;
    }

    private void setBonus(Consumer<PhysicalResistanceBonus> consumer, float value) {
        bonus = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<PhysicalResistanceBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public PhysicalResistanceBonus deserialize(JsonObject json) throws JsonParseException {
            float bonus = json.get("bonus").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new PhysicalResistanceBonus(bonus, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof PhysicalResistanceBonus pBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("bonus", pBonus.bonus);
            SerializationHelper.serializeOperation(json, pBonus.operation);
        }

        @Override
        public PhysicalResistanceBonus deserialize(CompoundTag tag) {
            float bonus = tag.getFloat("bonus");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new PhysicalResistanceBonus(bonus, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof PhysicalResistanceBonus pBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("bonus", pBonus.bonus);
            SerializationHelper.serializeOperation(tag, pBonus.operation);
            return tag;
        }

        @Override
        public PhysicalResistanceBonus deserialize(FriendlyByteBuf buf) {
            float bonus = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new PhysicalResistanceBonus(bonus, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof PhysicalResistanceBonus pBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(pBonus.bonus);
            buf.writeInt(pBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new PhysicalResistanceBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
}