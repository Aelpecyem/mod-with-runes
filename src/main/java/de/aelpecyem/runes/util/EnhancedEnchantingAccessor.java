package de.aelpecyem.runes.util;

import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;

public interface EnhancedEnchantingAccessor {
    boolean isRuneMode();

    int[] getRunePixels();

    default int getPixel(int x, int y){
        return getRunePixels()[x + y * 8];
    }

    RuneEnchantingRecipe getRecipe();
}
