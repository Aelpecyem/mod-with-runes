package de.aelpecyem.runes.common.entity;

import de.aelpecyem.runes.common.item.ThrowableRockItem;
import de.aelpecyem.runes.common.reg.RunesEntities;
import de.aelpecyem.runes.common.reg.RunesObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ThrownRockEntity extends ThrownItemEntity {
    public ThrownRockEntity(World world, LivingEntity owner) {
        super(RunesEntities.ROCK, owner, world);
    }

    public ThrownRockEntity(EntityType<? extends ThrownItemEntity> type, World world) {
        super(type, world);
    }

    public ThrownRockEntity(World world, double x, double y, double z) {
        super(RunesEntities.ROCK, x, y, z, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (getStack().getItem() instanceof ThrowableRockItem rock && entityHitResult.getEntity() instanceof LivingEntity livingEntity){
            rock.onHit.onHit(getOwner(), livingEntity, entityHitResult.getEntity().getBlockPos(), this);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (getStack().getItem() instanceof ThrowableRockItem rock){
            rock.onHit.onHit(getOwner(), null, blockHitResult.getBlockPos(), this);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            playSound(SoundEvents.BLOCK_STONE_BREAK, 0.1F, MathHelper.nextGaussian(random, 1, 0.2F));
            this.world.sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = new ItemStackParticleEffect(ParticleTypes.ITEM, getStack());

            for(int i = 0; i < 10; ++i) {
                this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(),
                        MathHelper.nextGaussian(random, 0, 0.1F),
                        MathHelper.nextGaussian(random, 0, 0.1F),
                        MathHelper.nextGaussian(random, 0, 0.1F));
            }
        }
    }

    @Override
    protected float getGravity() {
        return 0.06F;
    }

    @Override
    protected Item getDefaultItem() {
        return RunesObjects.SMOOTH_SLATE;
    }
}
