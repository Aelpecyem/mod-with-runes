package de.aelpecyem.runes.mixin.common;

import de.aelpecyem.runes.common.misc.RuneParticleEffect;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.util.AcceleratedMinecartAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements AcceleratedMinecartAccessor {
    @Unique private static final TrackedData<Boolean> ACCELERATED = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private AbstractMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "moveOnRail", at = @At("TAIL"))
    private void moveOnRail(BlockPos pos, BlockState state, CallbackInfo ci){
        if (isAccelerated()){
            setVelocity(getVelocity().multiply(1.5F));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci){
        if (isAccelerated() && world.isClient && age % 4 == 0 && getVelocity().length() > 1 ){
            world.addParticle(new RuneParticleEffect(new Vec3f(1, 0.5F, 0F), 1.5F),
                    getParticleX(1), getRandomBodyY(), getParticleZ(1),
                    0, 0, 0);
        }
    }

    @Inject(method = "dropItems", at = @At("TAIL"))
    private void dropItems(DamageSource damageSource, CallbackInfo ci){
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && isAccelerated()) {
            this.dropStack(new ItemStack(RunesObjects.RAIDHO_RUNE));
        }
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo ci){
        this.dataTracker.startTracking(ACCELERATED, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci){
        nbt.putBoolean("RunesAccelerated", isAccelerated());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci){
        setAccelerated(nbt.getBoolean("RunesAccelerated"));
    }

    @Override
    public boolean isAccelerated() {
        return dataTracker.get(ACCELERATED);
    }

    @Override
    public void setAccelerated(boolean accelerated) {
        this.dataTracker.set(ACCELERATED, accelerated);
    }
}
