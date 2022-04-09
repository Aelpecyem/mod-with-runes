package de.aelpecyem.runes.mixin.common;

import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe.RuneEnchantingManager;
import java.util.List;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataPackContents.class)
public class ServerResourceManagerMixin {
    @Unique private final RuneEnchantingManager runeEnchantingManager = new RuneEnchantingRecipe.RuneEnchantingManager();

    @Inject(method = "getContents", at = @At("RETURN"), cancellable = true)
    private void getContents(CallbackInfoReturnable<List<ResourceReloader>> cir){
        List<ResourceReloader> list = cir.getReturnValue();
        list.add(runeEnchantingManager);
        cir.setReturnValue(list);
    }
}
