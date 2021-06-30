package de.aelpecyem.runes.common.reg;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.item.BifrostAmuletItem;
import de.aelpecyem.runes.common.item.KnowledgeScrapItem;
import de.aelpecyem.runes.common.item.ThrowableRockItem;
import de.aelpecyem.runes.util.RegistryUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RunesObjects {
    public static final Item SMOOTH_SLATE = new ThrowableRockItem((owner, target, hitPos, entity) -> {
        if (target != null) {
            target.damage(DamageSource.thrownProjectile(entity, owner), 3);
        }
    }, new FabricItemSettings().maxCount(16).group(RunesMod.GROUP));
    public static final Item BIFROST_RUNE = new Item(new FabricItemSettings().maxCount(16).group(RunesMod.GROUP)){
        @Override
        @Environment(EnvType.CLIENT)
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            super.appendTooltip(stack, world, tooltip, context);
            tooltip.add(new TranslatableText("tooltip." + RunesMod.MOD_ID +".bifrost_rune").formatted(Formatting.GRAY));
        }
    };
    public static final Item BIFROST_AMULET = new BifrostAmuletItem();
    public static final KnowledgeScrapItem KNOWLEDGE_SCRAP = new KnowledgeScrapItem();

    public static void init(){
        RegistryUtil.register(Registry.ITEM,"smooth_slate", SMOOTH_SLATE);
        RegistryUtil.register(Registry.ITEM,"bifrost_rune",  BIFROST_RUNE);
        RegistryUtil.register(Registry.ITEM,"bifrost_amulet",  BIFROST_AMULET);
        RegistryUtil.register(Registry.ITEM,"knowledge_scrap",  KNOWLEDGE_SCRAP);
    }
}
