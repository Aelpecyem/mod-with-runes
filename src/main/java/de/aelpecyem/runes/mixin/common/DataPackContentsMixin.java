package de.aelpecyem.runes.mixin.common;

import com.google.common.collect.ImmutableList;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe.RuneEnchantingManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin {
    @Unique private final RuneEnchantingManager runeEnchantingManager = new RuneEnchantingRecipe.RuneEnchantingManager();

    @Inject(method = "getContents", at = @At("RETURN"), cancellable = true)
    private void getContents(CallbackInfoReturnable<List<ResourceReloader>> cir){
        List<ResourceReloader> updatedList = new ArrayList<>(cir.getReturnValue());
        updatedList.add(runeEnchantingManager);
        cir.setReturnValue(ImmutableList.copyOf(updatedList));
    }
}
