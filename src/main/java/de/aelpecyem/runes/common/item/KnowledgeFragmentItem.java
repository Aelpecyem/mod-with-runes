package de.aelpecyem.runes.common.item;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.client.packet.OpenKnowledgeScreenPacket;
import de.aelpecyem.runes.client.packet.SyncKnowledgeScrapPacket;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.util.RuneKnowledgeAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class KnowledgeFragmentItem extends Item {
    public KnowledgeFragmentItem() {
        super(new FabricItemSettings().maxCount(1).group(RunesMod.GROUP));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity serverPlayer && user instanceof RuneKnowledgeAccessor knowledge){
            RuneEnchantingRecipe presentRecipe = getKnowledge(itemStack);
            RuneEnchantingRecipe recipe = presentRecipe != null ? presentRecipe : addKnowledge(knowledge, user, itemStack);
            if (recipe != null){
                SyncKnowledgeScrapPacket.send(serverPlayer, recipe);
                OpenKnowledgeScreenPacket.send(serverPlayer, recipe);
            }else {
                user.sendMessage(new TranslatableText("text." + RunesMod.MOD_ID + ".unlocked_all"), true);
                return TypedActionResult.fail(itemStack);
            }
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        ItemStack outputStack = getRecipeStack(stack);
        if (!outputStack.isEmpty()){
            tooltip.add(new TranslatableText(outputStack.getTranslationKey()).formatted(Formatting.GRAY));
        }
    }

    public void setKnowledge(ItemStack stack, RuneEnchantingRecipe recipe){
        if (!stack.hasTag()){
            stack.setTag(new NbtCompound());
        }
        stack.getTag().putString("RunesRecipe", recipe.id().toString());
        stack.getTag().put("RunesRecipeOutput", recipe.output().writeNbt(new NbtCompound()));
    }

    public RuneEnchantingRecipe getKnowledge(ItemStack stack){
        if (!stack.hasTag() || !stack.getTag().contains("RunesRecipe")){
            return null;
        }
        Identifier id = new Identifier(stack.getTag().getString("RunesRecipe"));
        return RuneEnchantingRecipe.recipes.get(id);
    }

    public ItemStack getRecipeStack(ItemStack stack){
        if (!stack.hasTag() || !stack.getTag().contains("RunesRecipeOutput")){
            return ItemStack.EMPTY;
        }
        return ItemStack.fromNbt((NbtCompound) stack.getTag().get("RunesRecipeOutput"));
    }

    private RuneEnchantingRecipe addKnowledge(RuneKnowledgeAccessor knowledge, PlayerEntity user, ItemStack stack) {
        RuneEnchantingRecipe bifrostRecipe= RuneEnchantingRecipe.recipes.values().stream()
                .filter(recipe -> recipe.output().getItem() == RunesObjects.BIFROST_RUNE).findFirst().orElse(null);
        List<RuneEnchantingRecipe> recipes = RuneEnchantingRecipe.recipes.values().stream()
                .filter(recipe -> !knowledge.hasKnowledge(recipe) && recipe != bifrostRecipe)
                .collect(Collectors.toList());
        if (!recipes.isEmpty()){
            RuneEnchantingRecipe addedKnowledge = recipes.get(user.getRandom().nextInt(recipes.size()));
            knowledge.addKnowledge(addedKnowledge);
            setKnowledge(stack, addedKnowledge);
            return addedKnowledge;
        } else if (!knowledge.hasKnowledge(bifrostRecipe)){
            knowledge.addKnowledge(bifrostRecipe);
            setKnowledge(stack, bifrostRecipe);
            return bifrostRecipe;
        }
        RuneEnchantingRecipe recipe = RuneEnchantingRecipe.recipes.values().stream().toList().get(user.getRandom().nextInt(RuneEnchantingRecipe.recipes.size()));
        setKnowledge(stack, recipe);
        return recipe;
    }
}
