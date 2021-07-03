package de.aelpecyem.runes.common.reg;

import de.aelpecyem.runes.RunesMod;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RunesLootTables {
    public static final Map<Identifier, Identifier> LOOT_TABLE_INJECTS = new HashMap<>();
    public static final Identifier KNOWLEDGE_FRAGMENT = RunesMod.id("knowledge_fragment");
    public static void init(){
        LOOT_TABLE_INJECTS.put(LootTables.SIMPLE_DUNGEON_CHEST, KNOWLEDGE_FRAGMENT);
        LOOT_TABLE_INJECTS.put(LootTables.ABANDONED_MINESHAFT_CHEST, KNOWLEDGE_FRAGMENT);
        LOOT_TABLE_INJECTS.put(LootTables.BASTION_TREASURE_CHEST, KNOWLEDGE_FRAGMENT);
        LOOT_TABLE_INJECTS.put(LootTables.STRONGHOLD_LIBRARY_CHEST, KNOWLEDGE_FRAGMENT);
        LOOT_TABLE_INJECTS.put(LootTables.IGLOO_CHEST_CHEST, KNOWLEDGE_FRAGMENT);
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, identifier, builder, lootTableSetter) -> {
            if (LOOT_TABLE_INJECTS.containsKey(identifier)) {
                FabricLootSupplier lootSupplier = (FabricLootSupplier) FabricLootSupplierBuilder.of(lootManager.getTable(LOOT_TABLE_INJECTS.get(identifier))).build();
                lootTableSetter.set(builder.withPools(lootSupplier.getPools()).build());
            }
        });
    }
}
