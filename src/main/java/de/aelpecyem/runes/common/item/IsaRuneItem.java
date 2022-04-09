package de.aelpecyem.runes.common.item;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.util.StasisAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IsaRuneItem extends ThrowableRockItem{
    public IsaRuneItem() {
        super(20,  (owner, target, hitPos, entity) -> {
            if (target != null && owner instanceof PlayerEntity p){
                p.getItemCooldownManager().set(RunesObjects.ISA_RUNE, 120);
            }
            if (target instanceof StasisAccessor accessor && !target.world.isClient) {
                Vec3d velocity = target.getVelocity();
                target.setVelocity(Vec3d.ZERO);
                target.velocityDirty = true;
                accessor.setStasisTicks(100);
                accessor.setStasisVelocity(velocity);
            }
        }, new FabricItemSettings().maxCount(1).group(RunesMod.GROUP));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("tooltip." + RunesMod.MOD_ID +".isa_rune").formatted(Formatting.AQUA));
    }

}
