package daripher.skilltree.skill.bonus.player.agility;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class SwimSpeedBonus implements SkillBonus<SwimSpeedBonus> {
    private float amount;
    public AttributeModifier.Operation operation;

    public SwimSpeedBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.SWIM_SPEED.get();
    }

    @Override
    public SwimSpeedBonus copy() {
        return new SwimSpeedBonus(amount, operation);
    }

    @Override
    public SwimSpeedBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof SwimSpeedBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<SwimSpeedBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof SwimSpeedBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        return new SwimSpeedBonus(mergedAmount, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("swim_speed", amount, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<SwimSpeedBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> setAmount(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getSpeedBonus(Player player) {
        if (player == null) return amount;
        if (player.isInWater()) {
            return amount;
        }
        return 0f;
    }

    private void setAmount(Consumer<SwimSpeedBonus> consumer, float value) {
        amount = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<SwimSpeedBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public SwimSpeedBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = json.get("amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new SwimSpeedBonus(amount, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof SwimSpeedBonus sBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", sBonus.amount);
            SerializationHelper.serializeOperation(json, sBonus.operation);
        }

        @Override
        public SwimSpeedBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new SwimSpeedBonus(amount, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof SwimSpeedBonus sBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", sBonus.amount);
            SerializationHelper.serializeOperation(tag, sBonus.operation);
            return tag;
        }

        @Override
        public SwimSpeedBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new SwimSpeedBonus(amount, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof SwimSpeedBonus sBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(sBonus.amount);
            buf.writeInt(sBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new SwimSpeedBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE); // Default +20%
        }
    }
}