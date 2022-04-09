package de.aelpecyem.runes.common.entity;

import de.aelpecyem.runes.client.packet.FertilizeParticlePacket;
import de.aelpecyem.runes.common.misc.RuneParticleEffect;
import de.aelpecyem.runes.common.reg.RunesEntities;
import de.aelpecyem.runes.common.reg.RunesObjects;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JeraRuneEntity extends Entity {
    private int damageProgress;
    private final List<BlockPos> fertilizableCache = new ArrayList<>();
    public JeraRuneEntity(World world) {
        this(RunesEntities.JERA_RUNE, world);
    }

    public JeraRuneEntity(EntityType<? extends JeraRuneEntity> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (age % 200 == 0){
            if (!world.isClient){
                if (age % 200 == 0 || fertilizableCache.isEmpty()){
                    findFertilizables();
                }
                if (!fertilizableCache.isEmpty()) {
                    BlockPos fertPos = fertilizableCache.get(random.nextInt(fertilizableCache.size()));
                    BlockState state = world.getBlockState(fertPos);
                    if (state.getBlock() instanceof Fertilizable f) {
                        if (f.isFertilizable(world, fertPos, state, world.isClient)) {
                            f.grow((ServerWorld) world, random, fertPos, state);
                            FertilizeParticlePacket.send((ServerWorld) world, fertPos);
                        }
                    } else {
                        fertilizableCache.remove(fertPos);
                    }
                }
            }else{
                world.addParticle(new RuneParticleEffect(new Vec3f(0, 1, 0), MathHelper.nextGaussian(random, 1, 0.2F)),
                        getParticleX(1), getRandomBodyY(), getParticleZ(1), 0, 0, 0);
            }
        }
        if (damageProgress > 0){
            damageProgress--;
        }
    }

    private void findFertilizables() {
        fertilizableCache.clear();
        BlockPos.iterateOutwards(getBlockPos(), 4, 4, 4).forEach(possiblePos -> {
            if (world.getBlockState(possiblePos).getBlock() instanceof Fertilizable){
                fertilizableCache.add(possiblePos.toImmutable());
            }
        });
    }

    @Override
    protected Box calculateBoundingBox() {
        return super.calculateBoundingBox();
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if (!this.world.isClient && !this.isRemoved()) {
            damageProgress += 10;
            this.emitGameEvent(GameEvent.ENTITY_DAMAGED, attacker);
            world.sendEntityStatus(this, (byte) 8);
            boolean bl = attacker instanceof PlayerEntity p && p.getAbilities().creativeMode;
            if (bl || damageProgress > 30) {
                if (!bl && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    this.dropItem(RunesObjects.JERA_RUNE);
                }
                this.discard();
            }
        }
        return false;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 8){
            for (int i = 0; i < 5; i++) {
                world.addParticle(new RuneParticleEffect(new Vec3f(0, 1, 0), MathHelper.nextGaussian(random, 1, 0.2F)),
                        getParticleX(1), getRandomBodyY(), getParticleZ(1), 0, 0, 0);
            }
        }else {
            super.handleStatus(status);
        }
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(RunesObjects.JERA_RUNE);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        //no-op
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        //no-op
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
