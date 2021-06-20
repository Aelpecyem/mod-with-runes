package de.aelpecyem.runes.client;

import de.aelpecyem.runes.common.reg.RunesEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class RunesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(RunesEntities.ROCK, FlyingItemEntityRenderer::new);
    }
}
