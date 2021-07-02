package de.aelpecyem.runes.mixin.common;

import de.aelpecyem.runes.common.item.OrbOfKnowledgeItem;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.util.RuneKnowledgeAccessor;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements RuneKnowledgeAccessor {
    private Set<RuneEnchantingRecipe> unlockedRecipes = new HashSet<>(); //todo server-side only for now

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci){
        NbtList runeKnowledge = new NbtList();
        for (RuneEnchantingRecipe unlockedRecipe : unlockedRecipes) {
            runeKnowledge.add(NbtString.of(unlockedRecipe.id().toString()));
        }
        nbt.put("Runes_RuneKnowledge", runeKnowledge);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci){
        NbtList runeKnowledge = nbt.getList("Runes_RuneKnowledge", NbtElement.STRING_TYPE);
        unlockedRecipes.clear();
        for (NbtElement nbtElement : runeKnowledge) {
            if (nbtElement instanceof NbtString s){
                unlockedRecipes.add(RuneEnchantingRecipe.recipes.get(new Identifier(s.asString())));
            }
        }
    }

    @Inject(method = "addExperience", at = @At("HEAD"), cancellable = true)
    private void addExperience(int experience, CallbackInfo ci){
        if (OrbOfKnowledgeItem.isWearingTrinket(this)){
            OrbOfKnowledgeItem.getTrinket(this).ifPresent(pair -> {
                int experienceLeft = OrbOfKnowledgeItem.storeXP(experience, pair.getRight());
                if (experienceLeft > 0) {
                    addExperience(experienceLeft, ci);
                }
                ci.cancel();
            });
        }
    }
    @Override
    public boolean hasKnowledge(RuneEnchantingRecipe recipe) {
        return unlockedRecipes.contains(recipe);
    }

    @Override
    public void addKnowledge(RuneEnchantingRecipe recipe) {
        unlockedRecipes.add(recipe);
    }
}
