package de.aelpecyem.runes.client.packet;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.misc.RuneParticleEffect;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.util.EnhancedEnchantingAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.Random;

public class FertilizeParticlePacket {
    public static final Identifier ID = RunesMod.id("fertilize_particle");

    public static void send(ServerWorld world, BlockPos pos) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        PlayerLookup.tracking(world, pos).forEach(player -> ServerPlayNetworking.send(player, ID, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        client.execute(() -> {
            if (client.world != null){
                ClientWorld world = client.world;
                Random random = world.random;
                for (int i = 0; i < 5; i++) {
                    world.addParticle(new RuneParticleEffect(new Vec3f(0, 1, 0), MathHelper.nextGaussian(world.random, 1, 0.2F)),
                            pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0, 0, 0);
                    world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + random.nextFloat(), pos.getY() + 0.5F + random.nextFloat(), pos.getZ() + random.nextFloat(), 0, 0, 0);
                }
            }
        });
    }
}
