package de.aelpecyem.runes.common.misc;

import de.aelpecyem.runes.util.EnhancedEnchantingAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class RuneEnchantingSlot extends Slot {
    private EnhancedEnchantingAccessor enchantingAccessor;
    private boolean client;

    public RuneEnchantingSlot(boolean client, EnhancedEnchantingAccessor enchantingAccessor, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.enchantingAccessor = enchantingAccessor;
        this.client = client;
    }

    public boolean canInsert(ItemStack stack) {
        return true;
    }

    public int getMaxItemCount() {
        return 1;
    }

    @Override
    public ItemStack getStack() {
        return enchantingAccessor.isRuneMode() && client && enchantingAccessor.getRecipe() != null ? enchantingAccessor.getRecipe().output() : super.getStack();
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        super.onTakeItem(player, stack);
        if (enchantingAccessor.isRuneMode() && enchantingAccessor.getRecipe() != null){
            player.experienceLevel -= enchantingAccessor.getRecipe().xpCost();
            player.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, player.world.random.nextFloat() * 0.1F + 0.9F);
            inventory.removeStack(1, 1);
            inventory.removeStack(0, 1);
            enchantingAccessor.clearPixels();
        }
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        super.onCrafted(stack);
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (enchantingAccessor.isRuneMode() && enchantingAccessor.getRecipe() != null){
            return enchantingAccessor.getRecipe().output().copy();
        }
        return super.takeStack(amount);
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return enchantingAccessor.isRuneMode() && enchantingAccessor.getRecipe() != null ?
                (playerEntity.experienceLevel >= enchantingAccessor.getRecipe().xpCost() && inventory.getStack(1).getCount() > 1) :
                super.canTakeItems(playerEntity);
    }
}
