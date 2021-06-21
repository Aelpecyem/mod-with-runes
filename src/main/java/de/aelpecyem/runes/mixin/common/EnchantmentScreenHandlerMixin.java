package de.aelpecyem.runes.mixin.common;

import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.util.EnhancedEnchantingAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin extends ScreenHandler implements EnhancedEnchantingAccessor {
    private int[] runePixels = new int[64];
    private boolean runeMode = false;
    private EnchantmentScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    private void init(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci){
        for (int i = 0; i < runePixels.length; i++) {
            this.addProperty(Property.create(runePixels, i));
        }
    }

    @Inject(method = "onContentChanged", at = @At("HEAD"), cancellable = true)
    private void onContentChanged(Inventory inventory, CallbackInfo ci){
        runeMode = inventory.getStack(0).getItem() == RunesObjects.SMOOTH_ROCK;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        //consume xp and lapis
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
}
