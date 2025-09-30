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

public final class ArmorIgnoreBonus implements SkillBonus<ArmorIgnoreBonus> {
    public float ignore;
    private AttributeModifier.Operation operation;

    public ArmorIgnoreBonus(float ignore, AttributeModifier.Operation operation) {
        this.ignore = ignore;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.ARMOR_IGNORE.get();
    }

    @Override
    public ArmorIgnoreBonus copy() {
        return new ArmorIgnoreBonus(ignore, operation);
    }

    @Override
    public ArmorIgnoreBonus multiply(double multiplier) {
        ignore *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ArmorIgnoreBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<ArmorIgnoreBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ArmorIgnoreBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedIgnore = otherBonus.ignore + this.ignore;
        return new ArmorIgnoreBonus(mergedIgnore, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("armor_ignore", ignore, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return ignore > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ArmorIgnoreBonus> consumer) {
        editor.addLabel(0, 0, "Ignore", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, ignore).setNumericResponder(value -> setIgnore(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getIgnore(Player player) {
        return ignore;
    }

    private void setIgnore(Consumer<ArmorIgnoreBonus> consumer, float value) {
        ignore = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<ArmorIgnoreBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ArmorIgnoreBonus deserialize(JsonObject json) throws JsonParseException {
            float ignore = json.get("ignore").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new ArmorIgnoreBonus(ignore, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ArmorIgnoreBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("ignore", aBonus.ignore);
            SerializationHelper.serializeOperation(json, aBonus.operation);
        }

        @Override
        public ArmorIgnoreBonus deserialize(CompoundTag tag) {
            float ignore = tag.getFloat("ignore");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new ArmorIgnoreBonus(ignore, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ArmorIgnoreBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("ignore", aBonus.ignore);
            SerializationHelper.serializeOperation(tag, aBonus.operation);
            return tag;
        }

        @Override
        public ArmorIgnoreBonus deserialize(FriendlyByteBuf buf) {
            float ignore = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new ArmorIgnoreBonus(ignore, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ArmorIgnoreBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.ignore);
            buf.writeInt(aBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ArmorIgnoreBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}