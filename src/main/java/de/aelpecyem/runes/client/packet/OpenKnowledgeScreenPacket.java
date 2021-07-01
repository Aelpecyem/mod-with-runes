package de.aelpecyem.runes.client.packet;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.client.screen.KnowledgeScreen;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.common.reg.RunesObjects;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OpenKnowledgeScreenPacket {
    public static final Identifier ID = RunesMod.id("knowledge_screen");

    public static void send(ServerPlayerEntity player, RuneEnchantingRecipe recipe) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(recipe.id());
        buf.writeIntArray(recipe.pixels());
        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Identifier id = buf.readIdentifier();
        int[] pixels = buf.readIntArray();
        client.execute(() -> {
            if (client.player != null){
                client.openScreen(new KnowledgeScreen(pixels, id));
            }
        });
    }
}
