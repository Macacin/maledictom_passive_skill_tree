package daripher.skilltree.skill.bonus.player.agility;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class MovementSpeedBonus implements SkillBonus<MovementSpeedBonus> {
    public float amount;
    private AttributeModifier.Operation operation;

    public MovementSpeedBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.MOVEMENT_SPEED.get();
    }

    @Override
    public MovementSpeedBonus copy() {
        return new MovementSpeedBonus(amount, operation);
    }

    @Override
    public MovementSpeedBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof MovementSpeedBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<MovementSpeedBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof MovementSpeedBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        return new MovementSpeedBonus(mergedAmount, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("movement_speed", amount, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<MovementSpeedBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> setAmount(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getSpeedBonus(Player player) {
        if (player == null) return amount;
        return amount;
    }

    private void setAmount(Consumer<MovementSpeedBonus> consumer, float value) {
        amount = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<MovementSpeedBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public MovementSpeedBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = json.get("amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new MovementSpeedBonus(amount, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof MovementSpeedBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", aBonus.amount);
            SerializationHelper.serializeOperation(json, aBonus.operation);
        }

        @Override
        public MovementSpeedBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new MovementSpeedBonus(amount, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof MovementSpeedBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", aBonus.amount);
            SerializationHelper.serializeOperation(tag, aBonus.operation);
            return tag;
        }

        @Override
        public MovementSpeedBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new MovementSpeedBonus(amount, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof MovementSpeedBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.amount);
            buf.writeInt(aBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new MovementSpeedBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
}