package de.aelpecyem.runes.common.item;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.entity.ThrownRockEntity;
import de.aelpecyem.runes.util.AcceleratedMinecartAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class RaidhoRuneItem extends Item implements UseEntityCallback{
    public RaidhoRuneItem() {
        super(new FabricItemSettings().maxCount(16).group(RunesMod.GROUP));
        UseEntityCallback.EVENT.register(this);
        DispenserBlock.registerBehavior(this, (pointer, stack) -> {
             List<Entity> list = pointer.getWorld().getEntitiesByClass(Entity.class, new Box(pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING))), EntityPredicates.EXCEPT_SPECTATOR);
            for (Entity entity : list) {
                if (entity instanceof AcceleratedMinecartAccessor accessor && !accessor.isAccelerated()) {
                    accessor.setAccelerated(true);
                    stack.decrement(1);
                }
            }
            return stack;
        });
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("tooltip." + RunesMod.MOD_ID +".raidho_rune").formatted(Formatting.AQUA));
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof RaidhoRuneItem && entity instanceof AcceleratedMinecartAccessor accessor) {
            stack.decrement(1);
            accessor.setAccelerated(true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
