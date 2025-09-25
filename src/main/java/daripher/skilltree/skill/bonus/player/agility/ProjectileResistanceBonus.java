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

public final class ProjectileResistanceBonus implements SkillBonus<ProjectileResistanceBonus> {
    private float amount;
    public AttributeModifier.Operation operation;

    public ProjectileResistanceBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.PROJECTILE_RESISTANCE.get();
    }

    @Override
    public ProjectileResistanceBonus copy() {
        return new ProjectileResistanceBonus(amount, operation);
    }

    @Override
    public ProjectileResistanceBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ProjectileResistanceBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<ProjectileResistanceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ProjectileResistanceBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        return new ProjectileResistanceBonus(mergedAmount, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("projectile_resistance", amount, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ProjectileResistanceBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> setAmount(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getResistanceBonus(Player player) {
        if (player == null) return amount;
        return amount;
    }

    private void setAmount(Consumer<ProjectileResistanceBonus> consumer, float value) {
        amount = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<ProjectileResistanceBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ProjectileResistanceBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = json.get("amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new ProjectileResistanceBonus(amount, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileResistanceBonus pBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", pBonus.amount);
            SerializationHelper.serializeOperation(json, pBonus.operation);
        }

        @Override
        public ProjectileResistanceBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new ProjectileResistanceBonus(amount, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileResistanceBonus pBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", pBonus.amount);
            SerializationHelper.serializeOperation(tag, pBonus.operation);
            return tag;
        }

        @Override
        public ProjectileResistanceBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new ProjectileResistanceBonus(amount, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileResistanceBonus pBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(pBonus.amount);
            buf.writeInt(pBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ProjectileResistanceBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE); // Default +10%
        }
    }
}