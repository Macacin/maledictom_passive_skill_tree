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

public final class VoidResistanceBonus implements SkillBonus<VoidResistanceBonus> {
    public float resistance;
    private AttributeModifier.Operation operation;

    public VoidResistanceBonus(float resistance, AttributeModifier.Operation operation) {
        this.resistance = resistance;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.VOID_RESISTANCE.get();
    }

    @Override
    public VoidResistanceBonus copy() {
        return new VoidResistanceBonus(resistance, operation);
    }

    @Override
    public VoidResistanceBonus multiply(double multiplier) {
        resistance *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof VoidResistanceBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<VoidResistanceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof VoidResistanceBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedResistance = otherBonus.resistance + this.resistance;
        return new VoidResistanceBonus(mergedResistance, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("void_resistance_bonus", resistance * 100, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return resistance > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<VoidResistanceBonus> consumer) {
        editor.addLabel(0, 0, "Resistance", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, resistance).setNumericResponder(value -> setResistance(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getResistanceBonus(Player player) {
        return resistance;
    }

    private void setResistance(Consumer<VoidResistanceBonus> consumer, float value) {
        resistance = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<VoidResistanceBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public VoidResistanceBonus deserialize(JsonObject json) throws JsonParseException {
            float resistance = json.get("resistance").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new VoidResistanceBonus(resistance, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof VoidResistanceBonus vrBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("resistance", vrBonus.resistance);
            SerializationHelper.serializeOperation(json, vrBonus.operation);
        }

        @Override
        public VoidResistanceBonus deserialize(CompoundTag tag) {
            float resistance = tag.getFloat("resistance");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new VoidResistanceBonus(resistance, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof VoidResistanceBonus vrBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("resistance", vrBonus.resistance);
            SerializationHelper.serializeOperation(tag, vrBonus.operation);
            return tag;
        }

        @Override
        public VoidResistanceBonus deserialize(FriendlyByteBuf buf) {
            float resistance = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new VoidResistanceBonus(resistance, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof VoidResistanceBonus vrBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(vrBonus.resistance);
            buf.writeInt(vrBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new VoidResistanceBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}