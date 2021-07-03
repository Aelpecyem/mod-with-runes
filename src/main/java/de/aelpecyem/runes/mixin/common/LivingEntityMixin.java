package de.aelpecyem.runes.mixin.common;

import de.aelpecyem.runes.common.misc.RuneParticleEffect;
import de.aelpecyem.runes.common.reg.RunesSounds;
import de.aelpecyem.runes.util.StasisAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public float prevBodyYaw;
    @Shadow public float bodyYaw;
    @Shadow public float prevHeadYaw;
    @Shadow public float headYaw;
    @Shadow protected float prevLookDirection;
    @Shadow protected float lookDirection;
    @Shadow protected float prevStepBobbingAmount;
    @Shadow protected float stepBobbingAmount;

    @Shadow public int handSwingTicks;
    @Shadow public float handSwingProgress;
    @Shadow public float lastHandSwingProgress;
    @Shadow public float limbDistance;
    @Shadow public float lastLimbDistance;
    @Shadow protected int playerHitTimer;

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci){
        if (this instanceof StasisAccessor accessor) {
            if (accessor.getStasisTicks() > 0) {
                this.prevBodyYaw = bodyYaw;
                this.prevHeadYaw = headYaw;
                this.prevLookDirection = lookDirection;
                this.prevStepBobbingAmount = stepBobbingAmount;
                this.handSwingTicks = -1;
                this.lastHandSwingProgress = handSwingProgress;
                this.limbDistance = lastLimbDistance;
                this.playerHitTimer = 0;
                this.timeUntilRegen = 0;
                this.fallDistance = 0;
                ci.cancel();
                accessor.setStasisTicks(accessor.getStasisTicks() - 1);
                if (accessor.getStasisTicks() == 0 && world.isClient){
                    for (int i = 0; i < 10; i++) {
                        world.addParticle(new RuneParticleEffect(new Vec3f(0.8F, 1F, 0.05F), 1 + random.nextFloat()),
                                getParticleX(1), getRandomBodyY(), getParticleZ(1),
                                random.nextGaussian() / 10F, random.nextGaussian() / 10F, random.nextGaussian() / 10F);
                    }
                }
            } else if (accessor.getStasisVelocity().length() > 0) {
                playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,1F, 1F);
                setVelocity(accessor.getStasisVelocity());
                accessor.setStasisVelocity(Vec3d.ZERO);
                velocityDirty = true;
                velocityModified = true;
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof StasisAccessor accessor && accessor.getStasisTicks() > 0){
            if (source.getAttacker() != null) {
                playSound(RunesSounds.STASIS_HIT,1F, (float) (0.8F + accessor.getStasisVelocity().length() / 2F));
                double x = source.getAttacker().getX() - this.getX();
                double y = source.getAttacker().getY() - this.getY();
                double z = source.getAttacker().getZ() - this.getZ();
                double strength = 0.5D * (1.1D - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                if (!(strength <= 0.0D)) {
                    this.velocityDirty = true;
                    Vec3d vec3d = this.getVelocity();
                    Vec3d vec3d2 = (new Vec3d(x, y, z)).normalize().multiply(strength);
                    this.setVelocity(vec3d.x / 2.0D - vec3d2.x, vec3d.y / 2.0D - vec3d2.y, vec3d.z / 2.0D - vec3d2.z);
                }
            }
            cir.setReturnValue(false);
        }
    }
}
