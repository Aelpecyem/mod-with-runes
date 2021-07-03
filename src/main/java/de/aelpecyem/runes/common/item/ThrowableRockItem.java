package de.aelpecyem.runes.common.item;

import de.aelpecyem.runes.common.entity.ThrownRockEntity;
import de.aelpecyem.runes.common.reg.RunesSounds;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ThrowableRockItem extends Item {
    public final HitAction onHit;
    private int timer;
    public ThrowableRockItem(int timer, HitAction onHit, Settings settings) {
        super(settings);
        this.onHit = onHit;
        this.timer = timer;
        DispenserBlock.registerBehavior(this, new ProjectileDispenserBehavior() {
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new ThrownRockEntity(world, position.getX(), position.getY(), position.getZ()), (rock) -> {
                    rock.setItem(stack);
                });
            }
        });
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), RunesSounds.ITEM_ROCK_THROWN, SoundCategory.NEUTRAL, 0.5F, MathHelper.nextGaussian(user.getRandom(), 0.2F, 0.1F));
        if (!world.isClient) {
            ThrownRockEntity rock = new ThrownRockEntity(world, user);
            rock.setItem(itemStack);
            rock.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(rock);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        user.getItemCooldownManager().set(this, timer);
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    public interface HitAction {
        void onHit(@Nullable Entity owner, @Nullable Entity target, BlockPos hitPos, ThrownRockEntity entity);
    }
}
