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

public final class MaxHealthBonus implements SkillBonus<MaxHealthBonus> {
    public float amount;
    private AttributeModifier.Operation operation;

    public MaxHealthBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.MAX_HEALTH.get();
    }

    @Override
    public MaxHealthBonus copy() {
        return new MaxHealthBonus(amount, operation);
    }

    @Override
    public MaxHealthBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof MaxHealthBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<MaxHealthBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof MaxHealthBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        return new MaxHealthBonus(mergedAmount, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("max_health", amount, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<MaxHealthBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> setAmount(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getHealthBonus(Player player) {
        if (player == null) return amount;
        return amount; // Можно добавить условия, если нужно (e.g., based on player state)
    }

    private void setAmount(Consumer<MaxHealthBonus> consumer, float value) {
        amount = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<MaxHealthBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public MaxHealthBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = json.get("amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new MaxHealthBonus(amount, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof MaxHealthBonus mBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", mBonus.amount);
            SerializationHelper.serializeOperation(json, mBonus.operation);
        }

        @Override
        public MaxHealthBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new MaxHealthBonus(amount, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof MaxHealthBonus mBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", mBonus.amount);
            SerializationHelper.serializeOperation(tag, mBonus.operation);
            return tag;
        }

        @Override
        public MaxHealthBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new MaxHealthBonus(amount, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof MaxHealthBonus mBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(mBonus.amount);
            buf.writeInt(mBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new MaxHealthBonus(2f, AttributeModifier.Operation.ADDITION); // Default +2 HP
        }
    }
}