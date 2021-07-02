package de.aelpecyem.runes.common.item;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.reg.RunesObjects;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class OrbOfKnowledgeItem extends TrinketItem {
    public OrbOfKnowledgeItem() {
        super(new FabricItemSettings().group(RunesMod.GROUP).maxCount(1));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof PlayerEntity p){
            p.addExperience(extractXP(stack));
        }
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()){
            user.setCurrentHand(hand);
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("tooltip." + RunesMod.MOD_ID + ".orb_of_knowledge").formatted(Formatting.AQUA));
        int percentage = getStoredXP(stack) * 100 / getMaxStoredXP(stack);
        tooltip.add(new TranslatableText("tooltip." + RunesMod.MOD_ID + ".fullness", percentage + "%").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }

    public static int storeXP(int amount, ItemStack stack){
        int stored = getStoredXP(stack);
        int spaceLeft = getMaxStoredXP(stack) - (stored + amount);
        if (!stack.hasTag()){
            stack.setTag(new NbtCompound());
        }
        if (spaceLeft < 0){
            stack.getTag().putInt("StoredXP", MathHelper.clamp(stored + amount + spaceLeft, 0, getMaxStoredXP(stack)));
            return -spaceLeft;
        }
        stack.getTag().putInt("StoredXP", stored + amount);
        return 0;
    }

    public static int extractXP(ItemStack stack){
        if (!stack.hasTag()){
            return 0;
        }
        int xp = stack.getTag().getInt("StoredXP");
        if (xp > 0){
            stack.getTag().putInt("StoredXP", xp - 1);
            return 1;
        }
        return 0;
    }

    public static int getStoredXP(ItemStack stack){
        return stack.hasTag() ? stack.getTag().getInt("StoredXP") : 0;
    }

    public static int getMaxStoredXP(ItemStack stack){
        return 500;
    }

    public static boolean isFull(ItemStack stack){
        return getStoredXP(stack) >= getMaxStoredXP(stack);
    }

    public static boolean isWearingTrinket(LivingEntity entity){
        return TrinketsApi.getTrinketComponent(entity).map(component -> component.isEquipped(RunesObjects.ORB_OF_KNOWLEDGE)).orElse(false);
    }

    public static Optional<Pair<SlotReference, ItemStack>> getTrinket(LivingEntity entity){
        return TrinketsApi.getTrinketComponent(entity).map(component -> {
            List<Pair<SlotReference, ItemStack>> equipped = component.getEquipped(RunesObjects.ORB_OF_KNOWLEDGE);
            return equipped.stream().filter(pair -> !isFull(pair.getRight())).findFirst();
        }).orElse(Optional.empty());
    }


    public static class ExperienceTooltipComponent implements TooltipComponent {
        @Override
        public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
            textRenderer.draw(matrices, "uwu", x, y, 0xFFFFFF);
        }

        @Override
        public int getHeight() {
            return 12;
        }

        @Override
        public int getWidth(TextRenderer textRenderer) {
            return 100;
        }
    }
}
