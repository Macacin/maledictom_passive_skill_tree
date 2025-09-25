package daripher.skilltree.skill.bonus.player.agility;

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

public final class AirborneDamageBonus implements SkillBonus<AirborneDamageBonus> {
    public float amount;
    public AttributeModifier.Operation operation;

    public AirborneDamageBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.AIRBORNE_DAMAGE.get();
    }

    @Override
    public AirborneDamageBonus copy() {
        return new AirborneDamageBonus(amount, operation);
    }

    @Override
    public AirborneDamageBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof AirborneDamageBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<AirborneDamageBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof AirborneDamageBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        return new AirborneDamageBonus(mergedAmount, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("airborne_damage", amount, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<AirborneDamageBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> setAmount(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getDamageBonus(Player player) {
        if (player == null) return amount;
        return !player.onGround() ? amount : 0f;
    }

    private void setAmount(Consumer<AirborneDamageBonus> consumer, float value) {
        amount = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<AirborneDamageBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public AirborneDamageBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = json.get("amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new AirborneDamageBonus(amount, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof AirborneDamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", aBonus.amount);
            SerializationHelper.serializeOperation(json, aBonus.operation);
        }

        @Override
        public AirborneDamageBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new AirborneDamageBonus(amount, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof AirborneDamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", aBonus.amount);
            SerializationHelper.serializeOperation(tag, aBonus.operation);
            return tag;
        }

        @Override
        public AirborneDamageBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new AirborneDamageBonus(amount, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof AirborneDamageBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.amount);
            buf.writeInt(aBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new AirborneDamageBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE); // Default +10%
        }
    }
}