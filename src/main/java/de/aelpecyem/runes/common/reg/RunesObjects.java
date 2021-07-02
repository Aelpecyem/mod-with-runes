package de.aelpecyem.runes.common.reg;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.item.BifrostAmuletItem;
import de.aelpecyem.runes.common.item.JeraRuneItem;
import de.aelpecyem.runes.common.item.KnowledgeFragmentItem;
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
import java.util.stream.Collectors;

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
    public static final Item JERA_RUNE = new JeraRuneItem();
    public static final Item BIFROST_AMULET = new BifrostAmuletItem();
    public static final KnowledgeFragmentItem KNOWLEDGE_FRAGMENT = new KnowledgeFragmentItem();

    public static void init(){
        RegistryUtil.register(Registry.ITEM,"smooth_slate", SMOOTH_SLATE);
        RegistryUtil.register(Registry.ITEM,"bifrost_rune",  BIFROST_RUNE);
        RegistryUtil.register(Registry.ITEM,"jera_rune",  JERA_RUNE);
        RegistryUtil.register(Registry.ITEM,"bifrost_amulet",  BIFROST_AMULET);
        RegistryUtil.register(Registry.ITEM,"knowledge_fragment",  KNOWLEDGE_FRAGMENT);
    }

    public static List<ItemStack> appendItemsForGroup(){
        return Registry.ITEM.getIds().stream().map(Registry.ITEM::get)
                .filter(item -> item.getGroup() == RunesMod.GROUP)
                .map(Item::getDefaultStack).collect(Collectors.toList());
    }

    public static void appendItemsForGroup(List<ItemStack> itemStacks) {
        itemStacks.addAll(Registry.ITEM.getIds().stream().filter(id -> id.getNamespace().equals(RunesMod.MOD_ID))
                .map(id -> Registry.ITEM.get(id).getDefaultStack()).collect(Collectors.toList()));
    }
}
