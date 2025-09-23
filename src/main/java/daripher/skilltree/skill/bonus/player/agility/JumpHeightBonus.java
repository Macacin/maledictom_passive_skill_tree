package daripher.skilltree.skill.bonus.player.agility;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class JumpHeightBonus implements SkillBonus<JumpHeightBonus> {
    private float amount; // Amount as fraction added to multiplier (e.g., 0.1 for +10% velocity)
    private AttributeModifier.Operation operation;

    private static final double BASE_VELOCITY = 0.42;
    private static final double GRAVITY = 0.08;
    private static final double FRICTION = 0.98;

    public JumpHeightBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.JUMP_HEIGHT.get();
    }

    @Override
    public JumpHeightBonus copy() {
        return new JumpHeightBonus(amount, operation);
    }

    @Override
    public JumpHeightBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof JumpHeightBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<JumpHeightBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof JumpHeightBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        return new JumpHeightBonus(mergedAmount, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        double approxDeltaBlocks = approximateDeltaBlocks(amount);
        double resistancePercent = approxDeltaBlocks * 10;
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip("jump_height", approxDeltaBlocks, operation);
        tooltip.append(Component.literal(", +").append(String.format("%.1f", resistancePercent)).append("% to fall damage resistance"));
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<JumpHeightBonus> consumer) {
        editor.addLabel(0, 0, "Amount (blocks approx)", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, approximateDeltaBlocks(amount)).setNumericResponder(value -> setAmount(consumer, blocksToAmount(value.floatValue())));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getJumpHeightMultiplier(Player player) {
        // Optional condition: e.g., if (player.isInWater()) return 0;
        return amount;
    }

    public float getFallResistance(Player player) {
        return (float) (approximateDeltaBlocks(amount) * 0.1f); // +0.2 blocks -> 0.02 = 2% fraction
    }

    private void setAmount(Consumer<JumpHeightBonus> consumer, float value) {
        amount = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<JumpHeightBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    // Approximate delta blocks for tooltip (linear approx: delta ≈ 2.4 * amount)
    private double approximateDeltaBlocks(float amount) {
        return Math.round(2.4 * amount * 10) / 10.0; // Round to 1 decimal
    }

    // Reverse: amount from desired blocks (amount ≈ blocks / 2.4)
    private float blocksToAmount(float blocks) {
        return blocks / 2.4f;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public JumpHeightBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = json.get("amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new JumpHeightBonus(amount, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", aBonus.amount);
            SerializationHelper.serializeOperation(json, aBonus.operation);
        }

        @Override
        public JumpHeightBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new JumpHeightBonus(amount, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", aBonus.amount);
            SerializationHelper.serializeOperation(tag, aBonus.operation);
            return tag;
        }

        @Override
        public JumpHeightBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new JumpHeightBonus(amount, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.amount);
            buf.writeInt(aBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new JumpHeightBonus(0.1f, AttributeModifier.Operation.ADDITION); // +10% velocity by default, approx +0.2 blocks +2%
        }
    }
}