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

public final class MagicCritChanceBonus implements SkillBonus<MagicCritChanceBonus> {
    public float bonus;
    private AttributeModifier.Operation operation;

    public MagicCritChanceBonus(float bonus, AttributeModifier.Operation operation) {
        this.bonus = bonus;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.MAGIC_CRIT_CHANCE.get();
    }

    @Override
    public MagicCritChanceBonus copy() {
        return new MagicCritChanceBonus(bonus, operation);
    }

    @Override
    public MagicCritChanceBonus multiply(double multiplier) {
        bonus *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof MagicCritChanceBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<MagicCritChanceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof MagicCritChanceBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedBonus = otherBonus.bonus + this.bonus;
        return new MagicCritChanceBonus(mergedBonus, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("magic_crit_chance", bonus, operation)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return bonus > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<MagicCritChanceBonus> consumer) {
        editor.addLabel(0, 0, "Bonus", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, bonus).setNumericResponder(value -> setBonus(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getChance(Player player) {
        if (!ModList.get().isLoaded("irons_spellbooks")) return 0f;
        return bonus;
    }

    private void setBonus(Consumer<MagicCritChanceBonus> consumer, float value) {
        bonus = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<MagicCritChanceBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public MagicCritChanceBonus deserialize(JsonObject json) throws JsonParseException {
            float bonus = json.get("bonus").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new MagicCritChanceBonus(bonus, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof MagicCritChanceBonus mcBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("bonus", mcBonus.bonus);
            SerializationHelper.serializeOperation(json, mcBonus.operation);
        }

        @Override
        public MagicCritChanceBonus deserialize(CompoundTag tag) {
            float bonus = tag.getFloat("bonus");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new MagicCritChanceBonus(bonus, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof MagicCritChanceBonus mcBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("bonus", mcBonus.bonus);
            SerializationHelper.serializeOperation(tag, mcBonus.operation);
            return tag;
        }

        @Override
        public MagicCritChanceBonus deserialize(FriendlyByteBuf buf) {
            float bonus = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new MagicCritChanceBonus(bonus, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof MagicCritChanceBonus mcBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(mcBonus.bonus);
            buf.writeInt(mcBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new MagicCritChanceBonus(0.1f, AttributeModifier.Operation.ADDITION);
        }
    }
}