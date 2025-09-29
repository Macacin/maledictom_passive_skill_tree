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

public final class MiningSpeedBonus implements SkillBonus<MiningSpeedBonus> {
    public float bonus;
    private AttributeModifier.Operation operation;

    public MiningSpeedBonus(float bonus, AttributeModifier.Operation operation) {
        this.bonus = bonus;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.MINING_SPEED.get();
    }

    @Override
    public MiningSpeedBonus copy() {
        return new MiningSpeedBonus(bonus, operation);
    }

    @Override
    public MiningSpeedBonus multiply(double multiplier) {
        bonus *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof MiningSpeedBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<MiningSpeedBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof MiningSpeedBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedBonus = otherBonus.bonus + this.bonus;
        return new MiningSpeedBonus(mergedBonus, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("mining_speed", bonus, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return bonus > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<MiningSpeedBonus> consumer) {
        editor.addLabel(0, 0, "Bonus", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, bonus).setNumericResponder(value -> setBonus(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getSpeedBonus(Player player) {
        return bonus;  // Можно добавить условия, если нужно (например, проверка на инструмент)
    }

    private void setBonus(Consumer<MiningSpeedBonus> consumer, float value) {
        bonus = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<MiningSpeedBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public MiningSpeedBonus deserialize(JsonObject json) throws JsonParseException {
            float bonus = json.get("bonus").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new MiningSpeedBonus(bonus, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof MiningSpeedBonus mBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("bonus", mBonus.bonus);
            SerializationHelper.serializeOperation(json, mBonus.operation);
        }

        @Override
        public MiningSpeedBonus deserialize(CompoundTag tag) {
            float bonus = tag.getFloat("bonus");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new MiningSpeedBonus(bonus, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof MiningSpeedBonus mBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("bonus", mBonus.bonus);
            SerializationHelper.serializeOperation(tag, mBonus.operation);
            return tag;
        }

        @Override
        public MiningSpeedBonus deserialize(FriendlyByteBuf buf) {
            float bonus = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new MiningSpeedBonus(bonus, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof MiningSpeedBonus mBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(mBonus.bonus);
            buf.writeInt(mBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new MiningSpeedBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
}