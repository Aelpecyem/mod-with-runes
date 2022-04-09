package de.aelpecyem.runes.util;

import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;

public interface RuneKnowledgeAccessor {
    boolean hasKnowledge(RuneEnchantingRecipe recipe);

    void addKnowledge(RuneEnchantingRecipe recipe);
}
