package daripher.skilltree.skill.bonus.player.strength;

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

public final class ExplosionResistanceBonus implements SkillBonus<ExplosionResistanceBonus> {
    public float resistance;
    private AttributeModifier.Operation operation;

    public ExplosionResistanceBonus(float resistance, AttributeModifier.Operation operation) {
        this.resistance = resistance;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.EXPLOSION_RESISTANCE.get();
    }

    @Override
    public ExplosionResistanceBonus copy() {
        return new ExplosionResistanceBonus(resistance, operation);
    }

    @Override
    public ExplosionResistanceBonus multiply(double multiplier) {
        resistance *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ExplosionResistanceBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<ExplosionResistanceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ExplosionResistanceBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedResistance = otherBonus.resistance + this.resistance;
        return new ExplosionResistanceBonus(mergedResistance, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("explosion_resistance", resistance, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return resistance > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ExplosionResistanceBonus> consumer) {
        editor.addLabel(0, 0, "Resistance", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, resistance).setNumericResponder(value -> setResistance(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getResistance(Player player) {
        return resistance;
    }

    private void setResistance(Consumer<ExplosionResistanceBonus> consumer, float value) {
        resistance = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<ExplosionResistanceBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ExplosionResistanceBonus deserialize(JsonObject json) throws JsonParseException {
            float resistance = json.get("resistance").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new ExplosionResistanceBonus(resistance, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ExplosionResistanceBonus eBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("resistance", eBonus.resistance);
            SerializationHelper.serializeOperation(json, eBonus.operation);
        }

        @Override
        public ExplosionResistanceBonus deserialize(CompoundTag tag) {
            float resistance = tag.getFloat("resistance");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new ExplosionResistanceBonus(resistance, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ExplosionResistanceBonus eBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("resistance", eBonus.resistance);
            SerializationHelper.serializeOperation(tag, eBonus.operation);
            return tag;
        }

        @Override
        public ExplosionResistanceBonus deserialize(FriendlyByteBuf buf) {
            float resistance = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new ExplosionResistanceBonus(resistance, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ExplosionResistanceBonus eBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(eBonus.resistance);
            buf.writeInt(eBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ExplosionResistanceBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}