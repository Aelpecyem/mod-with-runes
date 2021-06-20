package de.aelpecyem.runes.util;

public interface EnhancedEnchantingAccessor {
    boolean isRuneMode();

    int[] getRunePixels();

    default int getPixel(int x, int y){
        return getRunePixels()[x + y * 8];
    }
}
