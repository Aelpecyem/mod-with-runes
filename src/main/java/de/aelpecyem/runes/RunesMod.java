package de.aelpecyem.runes;

import de.aelpecyem.runes.common.reg.RunesEntities;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.common.reg.RunesParticles;
import de.aelpecyem.runes.common.reg.RunesSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.LootManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.IdentityHashMap;

public class RunesMod implements ModInitializer {
    public static final String MOD_ID = "runes";
    public static final ItemGroup GROUP = ItemGroup.BREWING; //todo
    @Override
    public void onInitialize() {
        RunesObjects.init();
        RunesEntities.init();
        RunesSounds.init();
        RunesParticles.init();

    }

    public static Identifier id(String path){
        return new Identifier(MOD_ID, path);
    }
}