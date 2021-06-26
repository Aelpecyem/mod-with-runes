package de.aelpecyem.runes.client;

import de.aelpecyem.runes.client.particle.RuneParticle;
import de.aelpecyem.runes.common.reg.RunesEntities;
import de.aelpecyem.runes.common.reg.RunesParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.particle.EnchantGlyphParticle;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class RunesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(RunesEntities.ROCK, FlyingItemEntityRenderer::new);
        ParticleFactoryRegistry.getInstance().register(RunesParticles.RUNE, RuneParticle.RuneFactory::new);
    }
}
