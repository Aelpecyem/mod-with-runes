package de.aelpecyem.runes.mixin.common;

import de.aelpecyem.runes.common.misc.RuneEnchantingSlot;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.util.EnhancedEnchantingAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin extends ScreenHandler implements EnhancedEnchantingAccessor {
    @Shadow private Inventory inventory;
    private final int[] runePixels = new int[64];
    private boolean runeMode = false;
    private RuneEnchantingRecipe currentRecipe;
    private boolean recipeChanged = false;
    private EnchantmentScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    private void init(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci){
        for (int i = 0; i < runePixels.length; i++) {
            this.addProperty(Property.create(runePixels, i));
        }
        this.slots.set(0, new RuneEnchantingSlot(this, inventory, 0, 15, 47));
    }

    @Inject(method = "onContentChanged", at = @At("HEAD"), cancellable = true)
    private void onContentChanged(Inventory inventory, CallbackInfo ci){
        runeMode = inventory.getStack(0).getItem() == RunesObjects.SMOOTH_SLATE;
    }

    @Inject(method = "transferSlot", at = @At("HEAD"), cancellable = true)
    private void transferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir){
        if (index == 0 && runeMode){
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "onButtonClick", at = @At("HEAD"), cancellable = true)
    private void onButtonClick(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir){
        if (isRuneMode()){
            cir.setReturnValue(false);
            if (id < 64 + 2 && id > 2){
                id -= 3;
                runePixels[id] = runePixels[id] == 1 ? 0 : 1;
                cir.setReturnValue(true);
            }
        }
        currentRecipe = RuneEnchantingRecipe.getRecipe(runePixels).orElse(null);
        if (currentRecipe != null && !recipeChanged){
            //"de-sync" client handler item
            recipeChanged = true;
        }else if (recipeChanged && currentRecipe == null){
            //"re-sync" client handler item
            syncState();
        }
        //todo check recipes when one of these is changed, change item (if craftable)
    }

    @Override
    public boolean isRuneMode() {
        return runeMode;
    }

    @Override
    public int[] getRunePixels() {
        return runePixels;
    }

    @Override
    public RuneEnchantingRecipe getRecipe() {
        return currentRecipe;
    }
}
