package de.aelpecyem.runes.common.reg;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.item.ThrowableRockItem;
import de.aelpecyem.runes.util.RegistryUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class RunesObjects {
    public static final Item SMOOTH_ROCK = new ThrowableRockItem((owner, target, hitPos, entity) -> {
        if (target != null) {
            target.damage(DamageSource.thrownProjectile(entity, owner), 3);
        }
    }, new FabricItemSettings().maxCount(16).group(RunesMod.GROUP).maxDamage(32)); //throwable

    public static void init(){
        RegistryUtil.register(Registry.ITEM,"smooth_rock",  SMOOTH_ROCK);
    }
}
