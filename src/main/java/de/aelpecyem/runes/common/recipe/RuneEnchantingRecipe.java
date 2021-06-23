package de.aelpecyem.runes.common.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import de.aelpecyem.runes.RunesMod;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record RuneEnchantingRecipe(Identifier id, ItemStack output, int xpCost, int[] pixels) {
    private static Map<Identifier, RuneEnchantingRecipe> recipes = ImmutableMap.of();

    public boolean matches(int[] pixels) {
        return Arrays.equals(this.pixels, pixels);
    }

    public static Optional<RuneEnchantingRecipe> getRecipe(int[] pixels){
        return recipes.values().stream().filter(runeEnchantingRecipe -> runeEnchantingRecipe.matches(pixels)).findFirst();
    }

    public static class RuneEnchantingManager extends JsonDataLoader {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        public RuneEnchantingManager() {
            super(GSON, "runes");
        }

        @Override
        protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
            HashMap<Identifier, RuneEnchantingRecipe> map = new HashMap<>();
            for (Identifier identifier : prepared.keySet()) {
                map.put(identifier, deserialize(identifier, JsonHelper.asObject(prepared.get(identifier), "top element")));
            }
            recipes = ImmutableMap.copyOf(map);
        }

        private RuneEnchantingRecipe deserialize(Identifier identifier, JsonObject json){
            return new RuneEnchantingRecipe(identifier, ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result")),
                    JsonHelper.getInt(json, "xp_cost"), getPixels(JsonHelper.getArray(json, "pixels")));
        }

        private int[] getPixels(JsonArray pixels) {
            int[] pixelInts = new int[64];
            int index = 0;
            for (JsonElement pixel : pixels) {
                for (char c : pixel.getAsString().toCharArray()) {
                    pixelInts[index] = c == '#' ? 1 : 0;
                    index++;
                }
            }
            return pixelInts;
        }
    }
}
