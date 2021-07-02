package de.aelpecyem.runes.client;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.client.packet.FertilizeParticlePacket;
import de.aelpecyem.runes.client.packet.OpenKnowledgeScreenPacket;
import de.aelpecyem.runes.client.packet.SyncKnowledgeScrapPacket;
import de.aelpecyem.runes.client.packet.SyncRuneRecipePacket;
import de.aelpecyem.runes.client.particle.RuneParticle;
import de.aelpecyem.runes.client.renderer.JeraRuneEntityRenderer;
import de.aelpecyem.runes.common.entity.JeraRuneEntity;
import de.aelpecyem.runes.common.item.BifrostAmuletItem;
import de.aelpecyem.runes.common.reg.RunesEntities;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.common.reg.RunesParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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
        ClientPlayNetworking.registerGlobalReceiver(OpenKnowledgeScreenPacket.ID, OpenKnowledgeScreenPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(FertilizeParticlePacket.ID, FertilizeParticlePacket::handle);
    }
}
