package daripher.skilltree.skill.bonus.player.wisdom;

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

public final class SpellCooldownReductionBonus implements SkillBonus<SpellCooldownReductionBonus> {
    public float bonus;
    private AttributeModifier.Operation operation;

    public SpellCooldownReductionBonus(float bonus, AttributeModifier.Operation operation) {
        this.bonus = bonus;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.SPELL_COOLDOWN_REDUCTION.get();
    }

    @Override
    public SpellCooldownReductionBonus copy() {
        return new SpellCooldownReductionBonus(bonus, operation);
    }

    @Override
    public SpellCooldownReductionBonus multiply(double multiplier) {
        bonus *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof SpellCooldownReductionBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<SpellCooldownReductionBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof SpellCooldownReductionBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedBonus = otherBonus.bonus + this.bonus;
        return new SpellCooldownReductionBonus(mergedBonus, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("spell_cooldown_reduction", bonus, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return bonus > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<SpellCooldownReductionBonus> consumer) {
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

    private void setBonus(Consumer<SpellCooldownReductionBonus> consumer, float value) {
        bonus = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<SpellCooldownReductionBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public SpellCooldownReductionBonus deserialize(JsonObject json) throws JsonParseException {
            float bonus = json.get("bonus").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new SpellCooldownReductionBonus(bonus, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellCooldownReductionBonus scBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("bonus", scBonus.bonus);
            SerializationHelper.serializeOperation(json, scBonus.operation);
        }

        @Override
        public SpellCooldownReductionBonus deserialize(CompoundTag tag) {
            float bonus = tag.getFloat("bonus");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new SpellCooldownReductionBonus(bonus, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellCooldownReductionBonus scBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("bonus", scBonus.bonus);
            SerializationHelper.serializeOperation(tag, scBonus.operation);
            return tag;
        }

        @Override
        public SpellCooldownReductionBonus deserialize(FriendlyByteBuf buf) {
            float bonus = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new SpellCooldownReductionBonus(bonus, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof SpellCooldownReductionBonus scBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(scBonus.bonus);
            buf.writeInt(scBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new SpellCooldownReductionBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}