package de.aelpecyem.runes.mixin.common;

import de.aelpecyem.runes.common.misc.RuneParticleEffect;
import de.aelpecyem.runes.util.StasisAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin implements StasisAccessor{
    @Shadow @Final protected DataTracker dataTracker;
    @Shadow public World world;

    @Shadow public abstract double getParticleX(double widthScale);

    @Shadow public abstract double getRandomBodyY();

    @Shadow public abstract double getParticleZ(double widthScale);

    @Shadow @Final protected Random random;
    @Shadow public boolean velocityDirty;

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Unique
    private static final TrackedData<Integer> STASIS_TICKS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique private Vec3d stasisVelocity = Vec3d.ZERO;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci){
        if (getStasisTicks() > 0) {
            ci.cancel();
            setStasisTicks(getStasisTicks() - 1);
            if (getStasisTicks() == 0 && world.isClient){
                for (int i = 0; i < 10; i++) {
                    world.addParticle(new RuneParticleEffect(new Vec3f(0.8F, 1F, 0.05F), 1 + random.nextFloat()),
                            getParticleX(1), getRandomBodyY(), getParticleZ(1),
                            random.nextGaussian() / 10F, random.nextGaussian() / 10F, random.nextGaussian() / 10F);
                }
            }
        } else if (getStasisVelocity().length() > 0) {
            setVelocity(getStasisVelocity());
            setStasisVelocity(Vec3d.ZERO);
            velocityDirty = true;
        }
    }

    @Inject(method = "addVelocity", at = @At("HEAD"), cancellable = true)
    private void addVelocity(double deltaX, double deltaY, double deltaZ, CallbackInfo ci){
        if (getStasisTicks() > 0){
            setStasisVelocity(getStasisVelocity().add(deltaX, deltaY, deltaZ));
            ci.cancel();
        }
    }

    @Inject(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
    private void setVelocity(Vec3d velocity, CallbackInfo ci){
        if (getStasisTicks() > 0){
            setStasisVelocity(getStasisVelocity().add(velocity));
            ci.cancel();
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo ci){
        this.dataTracker.startTracking(STASIS_TICKS, 0);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir){
        nbt.putInt("RunesStasisTicks", getStasisTicks());
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci){
        setStasisTicks(nbt.getInt("RunesStasisTicks"));
    }
    @Override
    public int getStasisTicks() {
        return dataTracker.get(STASIS_TICKS);
    }

    @Override
    public void setStasisTicks(int ticks) {
        this.dataTracker.set(STASIS_TICKS, ticks);
    }

    @Override
    public Vec3d getStasisVelocity() {
        return stasisVelocity;
    }

    @Override
    public void setStasisVelocity(Vec3d velocity) {
        this.stasisVelocity = velocity;
    }
}
