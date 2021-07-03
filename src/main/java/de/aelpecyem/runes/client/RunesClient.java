package de.aelpecyem.runes.client;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.client.packet.FertilizeParticlePacket;
import de.aelpecyem.runes.client.packet.OpenKnowledgeScreenPacket;
import de.aelpecyem.runes.client.packet.SyncKnowledgeScrapPacket;
import de.aelpecyem.runes.client.packet.SyncRuneRecipePacket;
import de.aelpecyem.runes.client.particle.RuneParticle;
import de.aelpecyem.runes.client.renderer.JeraRuneEntityRenderer;
import de.aelpecyem.runes.client.screen.KnowledgeScreen;
import de.aelpecyem.runes.common.item.BifrostAmuletItem;
import de.aelpecyem.runes.common.item.OrbOfKnowledgeItem;
import de.aelpecyem.runes.common.reg.RunesEntities;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.common.reg.RunesParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RunesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(RunesEntities.ROCK, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(RunesEntities.JERA_RUNE, JeraRuneEntityRenderer::new);
        ParticleFactoryRegistry.getInstance().register(RunesParticles.RUNE, RuneParticle.RuneFactory::new);
        FabricModelPredicateProviderRegistry.register(RunesObjects.BIFROST_AMULET, RunesMod.id("variant"), (stack, world, entity, seed) -> BifrostAmuletItem.getColorScheme(stack).getThreshold());
        ClientPlayNetworking.registerGlobalReceiver(SyncRuneRecipePacket.ID, SyncRuneRecipePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncKnowledgeScrapPacket.ID, SyncKnowledgeScrapPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(OpenKnowledgeScreenPacket.ID, new ClientPlayNetworking.PlayChannelHandler() {
            @Override
            public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
                Identifier id = buf.readIdentifier();
                int[] pixels = buf.readIntArray();
                client.execute(() -> {
                    if (client.player != null){
                        client.openScreen(new KnowledgeScreen(pixels, id));
                    }
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(FertilizeParticlePacket.ID, FertilizeParticlePacket::handle);
        ColorProviderRegistryImpl.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                float ratio = OrbOfKnowledgeItem.getStoredXP(stack) / (float) OrbOfKnowledgeItem.getMaxStoredXP(stack);
                int r = (int) MathHelper.lerp(ratio, 95, 75);
                int g = (int) MathHelper.lerp(ratio, 122, 186);
                int b = (int) MathHelper.lerp(ratio, 211, 11);
                return (r << 16) + (g << 8) + (b);
            }
            return 0xFFFFFF;
        }, RunesObjects.ORB_OF_KNOWLEDGE);
    }
}
