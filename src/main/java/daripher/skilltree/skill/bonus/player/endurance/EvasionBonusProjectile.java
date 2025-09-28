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

public final class EvasionBonusProjectile implements SkillBonus<EvasionBonusProjectile> {
    public float chance;
    private AttributeModifier.Operation operation;

    public EvasionBonusProjectile(float chance, AttributeModifier.Operation operation) {
        this.chance = chance;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.EVASION_PROJECTILE.get();
    }

    @Override
    public EvasionBonusProjectile copy() {
        return new EvasionBonusProjectile(chance, operation);
    }

    @Override
    public EvasionBonusProjectile multiply(double multiplier) {
        chance *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof EvasionBonusProjectile otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<EvasionBonusProjectile> merge(SkillBonus<?> other) {
        if (!(other instanceof EvasionBonusProjectile otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedChance = otherBonus.chance + this.chance;
        return new EvasionBonusProjectile(mergedChance, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("evasion_projectile", chance, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return chance > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<EvasionBonusProjectile> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, chance).setNumericResponder(value -> setChance(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getEvasionChance(Player player) {
        return chance;
    }

    private void setChance(Consumer<EvasionBonusProjectile> consumer, float value) {
        chance = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<EvasionBonusProjectile> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public EvasionBonusProjectile deserialize(JsonObject json) throws JsonParseException {
            float chance = json.get("chance").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new EvasionBonusProjectile(chance, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof EvasionBonusProjectile eBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", eBonus.chance);
            SerializationHelper.serializeOperation(json, eBonus.operation);
        }

        @Override
        public EvasionBonusProjectile deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new EvasionBonusProjectile(chance, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof EvasionBonusProjectile eBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", eBonus.chance);
            SerializationHelper.serializeOperation(tag, eBonus.operation);
            return tag;
        }

        @Override
        public EvasionBonusProjectile deserialize(FriendlyByteBuf buf) {
            float chance = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new EvasionBonusProjectile(chance, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof EvasionBonusProjectile eBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(eBonus.chance);
            buf.writeInt(eBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new EvasionBonusProjectile(0.05f, AttributeModifier.Operation.ADDITION);
        }
    }
}