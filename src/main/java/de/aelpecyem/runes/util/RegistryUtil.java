package de.aelpecyem.runes.util;

import de.aelpecyem.runes.RunesMod;
import net.minecraft.item.Item;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryUtil {
    public static <T> T register(Registry<T> registry, String name,  T entry) {
        return Registry.register(registry, RunesMod.id(name), entry);
    }
}
