package de.aelpecyem.runes.mixin.client;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.util.AcceleratedMinecartAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MinecartEntityRenderer.class)
public class MinecartEntityRendererMixin<T extends AbstractMinecartEntity> {
    @Unique private static final Identifier RUNED_TEXTURE = RunesMod.id("textures/entity/runed_minecart.png");
    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void getTexture(T abstractMinecartEntity, CallbackInfoReturnable<Identifier> cir){
        if (abstractMinecartEntity instanceof AcceleratedMinecartAccessor accessor && accessor.isAccelerated()){
            cir.setReturnValue(RUNED_TEXTURE);
        }
    }
}
