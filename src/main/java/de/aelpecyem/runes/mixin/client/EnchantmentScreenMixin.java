package de.aelpecyem.runes.mixin.client;

import de.aelpecyem.runes.client.screen.RuneScreen;
import de.aelpecyem.runes.util.EnhancedEnchantingAccessor;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends HandledScreen<EnchantmentScreenHandler> {
    private RuneScreen runeScreen;
    private EnchantmentScreenMixin(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci){
        this.runeScreen = new RuneScreen((EnchantmentScreen) (Object) this, handler, (EnhancedEnchantingAccessor) this.handler);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;drawMouseoverTooltip(Lnet/minecraft/client/util/math/MatrixStack;II)V", shift = At.Shift.AFTER), cancellable = true)
    private void renderCancel(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
        if (this.handler instanceof EnhancedEnchantingAccessor accessor && accessor.isRuneMode()){
            ci.cancel();
        }
    }


    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;renderBackground(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    private void renderHead(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
        if (this.handler instanceof EnhancedEnchantingAccessor accessor && accessor.isRuneMode()){
            runeScreen.render(client, matrices, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "drawBackground", at = @At("HEAD"), cancellable = true)
    private void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci){
        if (this.handler instanceof EnhancedEnchantingAccessor accessor && accessor.isRuneMode()){
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.handler instanceof EnhancedEnchantingAccessor accessor && accessor.isRuneMode()){
            if (runeScreen.mouseClicked(client, mouseX, mouseY, button)){
                cir.setReturnValue(true);
            }
        }
    }
}
