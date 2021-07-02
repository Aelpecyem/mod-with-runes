package de.aelpecyem.runes.common.item;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.entity.JeraRuneEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JeraRuneItem extends Item {
    public JeraRuneItem() {
        super(new FabricItemSettings().maxCount(16).group(RunesMod.GROUP));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockHitResult blockHitResult = raycast(world, user);
        ItemUsageContext context = new ItemUsageContext(user, hand, blockHitResult);
        if (!world.isClient){
            BlockPos pos = context.getBlockPos();
            if (!world.isAir(pos)){
                pos = pos.offset(context.getSide());
            }
            JeraRuneEntity entity = new JeraRuneEntity(context.getWorld());
            entity.setPosition(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            context.getWorld().spawnEntity(entity);
            return new TypedActionResult<>(ActionResult.CONSUME, user.getStackInHand(hand));
        }
        return new TypedActionResult<>(ActionResult.PASS, user.getStackInHand(hand));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("tooltip." + RunesMod.MOD_ID +".jera_rune").formatted(Formatting.AQUA));
    }

    private static BlockHitResult raycast(World world, PlayerEntity player){
        float pitch = player.getPitch();
        float yaw = player.getYaw();
        Vec3d vec3d = player.getEyePos();
        float h = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
        float i = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
        float j = -MathHelper.cos(-pitch * 0.017453292F);
        float k = MathHelper.sin(-pitch * 0.017453292F);
        float l = i * j;
        float n = h * j;
        Vec3d vec3d2 = vec3d.add(l * 2, k * 2, n * 2);
        return world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
    }
}
