package daripher.skilltree.skill.bonus.player.constitution;

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

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class DamageReflectionBonus implements SkillBonus<DamageReflectionBonus> {
    public float chance;
    private AttributeModifier.Operation operation;

    public DamageReflectionBonus(float chance, AttributeModifier.Operation operation) {
        this.chance = chance;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.DAMAGE_REFLECTION.get();
    }

    @Override
    public DamageReflectionBonus copy() {
        return new DamageReflectionBonus(chance, operation);
    }

    @Override
    public DamageReflectionBonus multiply(double multiplier) {
        chance *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof DamageReflectionBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<DamageReflectionBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof DamageReflectionBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedChance = otherBonus.chance + this.chance;
        return new DamageReflectionBonus(mergedChance, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("damage_reflection", chance * 100, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return chance > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<DamageReflectionBonus> consumer) {
        editor.addLabel(0, 0, "Chance (%)", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, chance * 100).setNumericResponder(value -> setChance(consumer, value.floatValue() / 100));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getChance() {
        return chance;
    }

    private void setChance(Consumer<DamageReflectionBonus> consumer, float value) {
        chance = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<DamageReflectionBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public DamageReflectionBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = json.get("chance").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new DamageReflectionBonus(chance, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageReflectionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", aBonus.chance);
            SerializationHelper.serializeOperation(json, aBonus.operation);
        }

        @Override
        public DamageReflectionBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new DamageReflectionBonus(chance, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageReflectionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", aBonus.chance);
            SerializationHelper.serializeOperation(tag, aBonus.operation);
            return tag;
        }

        @Override
        public DamageReflectionBonus deserialize(FriendlyByteBuf buf) {
            float chance = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new DamageReflectionBonus(chance, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageReflectionBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.chance);
            buf.writeInt(aBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new DamageReflectionBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}