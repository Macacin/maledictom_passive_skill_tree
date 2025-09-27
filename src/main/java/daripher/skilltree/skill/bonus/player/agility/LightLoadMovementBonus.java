package daripher.skilltree.skill.bonus.player.agility;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import com.jabroni.weightmod.capability.WeightCapabilities;
import com.jabroni.weightmod.config.WeightConfig;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class LightLoadMovementBonus implements SkillBonus<LightLoadMovementBonus> {
    public float amount;
    private AttributeModifier.Operation operation;

    public LightLoadMovementBonus(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.LIGHT_LOAD_MOVEMENT.get();
    }

    @Override
    public LightLoadMovementBonus copy() {
        return new LightLoadMovementBonus(amount, operation);
    }

    @Override
    public LightLoadMovementBonus multiply(double multiplier) {
        amount *= (float) multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof LightLoadMovementBonus otherBonus)) return false;
        return otherBonus.operation == this.operation;
    }

    @Override
    public SkillBonus<LightLoadMovementBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof LightLoadMovementBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        float mergedAmount = otherBonus.amount + this.amount;
        return new LightLoadMovementBonus(mergedAmount, this.operation);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip("light_load_movement", amount, operation).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<LightLoadMovementBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> setAmount(consumer, value.floatValue()));
        editor.addOperationSelection(55, 0, 145, operation).setResponder(operation -> setOperation(consumer, operation));
        editor.increaseHeight(19);
    }

    public float getSpeedBonus(Player player) {
        if (player == null) return amount;
        if (!ModList.get().isLoaded("weightmod")) return 0f; // Безопасность, если WeightMod не установлен

        return player.getCapability(WeightCapabilities.CAPABILITY).map(cap -> {
            int totalWeight = calculateArmorWeight(player); // Используем метод из WeightEventHandler (сделай public, если нужно)
            double percentage = cap.getCapacity() > 0 ? (double) totalWeight / cap.getCapacity() : 0;
            int level = getOverloadLevel(percentage); // Метод из WeightEventHandler
            return level <= 1 ? amount : 0f;
        }).orElse(0f);
    }

    private int calculateArmorWeight(Player player) {
        int totalWeight = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof ArmorItem armor) {
                int defense = armor.getDefense();
                double coef = getArmorCoef(stack);
                double x = getArmorX(stack);
                double weight = (Math.pow(defense + x, 1.25)) * coef;
                totalWeight += (int) Math.round(weight);
            }
        }
        return totalWeight;
    }

    private double getArmorCoef(ItemStack stack) {
        // Копия твоего метода, адаптируй под конфиг
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return 1.0;
        String itemId = id.toString();

        if (WeightConfig.LIGHT_POOL.get().contains(itemId)) {
            return WeightConfig.LIGHT_COEF.get();
        } else if (WeightConfig.MEDIUM_POOL.get().contains(itemId)) {
            return WeightConfig.MEDIUM_COEF.get();
        } else if (WeightConfig.HEAVY_POOL.get().contains(itemId)) {
            return WeightConfig.HEAVY_COEF.get();
        } else if (WeightConfig.VERY_HEAVY_POOL.get().contains(itemId)) {
            return WeightConfig.VERY_HEAVY_COEF.get();
        } else if (WeightConfig.EXTRA_LARGE_POOL.get().contains(itemId)) {
            return WeightConfig.VERY_HEAVY_COEF.get();
        }
        return 1.0;
    }

    private double getArmorX(ItemStack stack) {
        // Аналогично копия
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return 1.0;
        String itemId = id.toString();

        if (WeightConfig.LIGHT_POOL.get().contains(itemId)) {
            return WeightConfig.LIGHT_X.get();
        } else if (WeightConfig.MEDIUM_POOL.get().contains(itemId)) {
            return WeightConfig.MEDIUM_X.get();
        } else if (WeightConfig.HEAVY_POOL.get().contains(itemId)) {
            return WeightConfig.HEAVY_X.get();
        } else if (WeightConfig.VERY_HEAVY_POOL.get().contains(itemId)) {
            return WeightConfig.VERY_HEAVY_X.get();
        } else if (WeightConfig.EXTRA_LARGE_POOL.get().contains(itemId)) {
            return WeightConfig.EXTRA_LARGE_X.get();
        }
        return 1.0;
    }

    private int getOverloadLevel(double percentage) {
        if (percentage <= WeightConfig.LEVEL1_THRESHOLD.get()) return 1;
        if (percentage <= WeightConfig.LEVEL2_THRESHOLD.get()) return 2;
        if (percentage <= WeightConfig.LEVEL3_THRESHOLD.get()) return 3;
        return 4;
    }

    private void setAmount(Consumer<LightLoadMovementBonus> consumer, float value) {
        amount = value;
        consumer.accept(this.copy());
    }

    private void setOperation(Consumer<LightLoadMovementBonus> consumer, AttributeModifier.Operation operation) {
        this.operation = operation;
        consumer.accept(this.copy());
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public LightLoadMovementBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = json.get("amount").getAsFloat();
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
            return new LightLoadMovementBonus(amount, operation);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof LightLoadMovementBonus lBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("amount", lBonus.amount);
            SerializationHelper.serializeOperation(json, lBonus.operation);
        }

        @Override
        public LightLoadMovementBonus deserialize(CompoundTag tag) {
            float amount = tag.getFloat("amount");
            AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
            return new LightLoadMovementBonus(amount, operation);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof LightLoadMovementBonus lBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("amount", lBonus.amount);
            SerializationHelper.serializeOperation(tag, lBonus.operation);
            return tag;
        }

        @Override
        public LightLoadMovementBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(buf.readInt());
            return new LightLoadMovementBonus(amount, operation);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof LightLoadMovementBonus lBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(lBonus.amount);
            buf.writeInt(lBonus.operation.toValue());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new LightLoadMovementBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE); // Default +10%
        }
    }
}