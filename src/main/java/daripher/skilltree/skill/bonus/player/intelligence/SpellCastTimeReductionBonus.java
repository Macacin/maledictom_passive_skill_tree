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
import net.minecraftforge.fml.ModList;

import java.util.function.Consumer;

public final class SpellCastTimeReductionBonus implements SkillBonus<SpellCastTimeReductionBonus> {
    public float bonus;
    private AttributeModifier.Operation operation;

    public SpellCastTimeReductionBonus(float bonus, AttributeModifier.Operation operation) {
        this.bonus = bonus;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.SPELL_CAST_TIME_REDUCTION.get();
    }

    @Override
    public SpellCastTimeReductionBonus copy() {
        return new SpellCastTimeReductionBonus(bonus, operation);
    }

    @Override
    public SpellCastTimeReductionBonus multiply(double multiplier) {
        bonus *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof SpellCastTimeReductionBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<SpellCastTimeReductionBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof SpellCastTimeReductionBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedBonus = otherBonus.bonus + this.bonus;
        return new SpellCastTimeReductionBonus(mergedBonus, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("spell_cast_time_reduction", bonus, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return bonus > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<SpellCastTimeReductionBonus> consumer) {
        editor.addLabel(0, 0, "Bonus", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, bonus).setNumericResponder(value -> setBonus(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getReductionBonus(Player player) {
        if (!ModList.get().isLoaded("irons_spellbooks")) return 0f;
        return bonus;
    }

    private void setBonus(Consumer<SpellCastTimeReductionBonus> consumer, float value) {
        bonus = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<SpellCastTimeReductionBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public SpellCastTimeReductionBonus deserialize(JsonObject json) throws JsonParseException {
            float bonus = json.get("bonus").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new SpellCastTimeReductionBonus(bonus, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellCastTimeReductionBonus sctrBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("bonus", sctrBonus.bonus);
            SerializationHelper.serializeOperation(json, sctrBonus.operation);
        }

        @Override
        public SpellCastTimeReductionBonus deserialize(CompoundTag tag) {
            float bonus = tag.getFloat("bonus");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new SpellCastTimeReductionBonus(bonus, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellCastTimeReductionBonus sctrBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("bonus", sctrBonus.bonus);
            SerializationHelper.serializeOperation(tag, sctrBonus.operation);
            return tag;
        }

        @Override
        public SpellCastTimeReductionBonus deserialize(FriendlyByteBuf buf) {
            float bonus = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new SpellCastTimeReductionBonus(bonus, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellCastTimeReductionBonus sctrBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(sctrBonus.bonus);
            buf.writeInt(sctrBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new SpellCastTimeReductionBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}