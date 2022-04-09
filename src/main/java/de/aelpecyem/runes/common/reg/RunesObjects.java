package de.aelpecyem.runes.common.reg;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.item.*;
import de.aelpecyem.runes.util.RegistryUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class RunesObjects {
    public static final Item SMOOTH_SLATE = new ThrowableRockItem(10, (owner, target, hitPos, entity) -> {
        if (target instanceof LivingEntity) {
            target.damage(DamageSource.thrownProjectile(entity, owner), 3);
        }
    }, new FabricItemSettings().maxCount(16).group(RunesMod.GROUP));
    public static final Item ANSUZ_RUNE = new RuneIngredientItem();
    public static final Item BIFROST_RUNE = new RuneIngredientItem();
    public static final Item RAIDHO_RUNE = new RaidhoRuneItem();
    public static final Item ISA_RUNE = new IsaRuneItem();
    public static final Item JERA_RUNE = new JeraRuneItem();
    public static final Item BIFROST_AMULET = new BifrostAmuletItem();
    public static final Item ORB_OF_KNOWLEDGE = new OrbOfKnowledgeItem();
    public static final KnowledgeFragmentItem KNOWLEDGE_FRAGMENT = new KnowledgeFragmentItem();

    public static void init() {
        RegistryUtil.register(Registry.ITEM, "smooth_slate", SMOOTH_SLATE);
        RegistryUtil.register(Registry.ITEM, "ansuz_rune", ANSUZ_RUNE);
        RegistryUtil.register(Registry.ITEM, "bifrost_rune", BIFROST_RUNE);
        RegistryUtil.register(Registry.ITEM, "raidho_rune", RAIDHO_RUNE);
        RegistryUtil.register(Registry.ITEM, "isa_rune", ISA_RUNE);
        RegistryUtil.register(Registry.ITEM, "jera_rune", JERA_RUNE);
        RegistryUtil.register(Registry.ITEM, "bifrost_amulet", BIFROST_AMULET);
        RegistryUtil.register(Registry.ITEM, "orb_of_knowledge", ORB_OF_KNOWLEDGE);
        RegistryUtil.register(Registry.ITEM, "knowledge_fragment", KNOWLEDGE_FRAGMENT);
    }
}
