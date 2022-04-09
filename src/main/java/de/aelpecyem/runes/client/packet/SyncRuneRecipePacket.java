package de.aelpecyem.runes.client.packet;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.util.EnhancedEnchantingAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncRuneRecipePacket {
    public static final Identifier ID = RunesMod.id("sync_recipe");

    public static void send(ServerPlayerEntity player, RuneEnchantingRecipe recipe, int syncId) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(recipe != null);
        buf.writeInt(syncId);
        if (recipe != null) {
            recipe.toPacket(buf);
        }
        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean notNull = buf.readBoolean();
        int syncId = buf.readInt();
        RuneEnchantingRecipe recipe = notNull ? RuneEnchantingRecipe.fromPacket(buf) : null;
        client.execute(() -> {
            if (client.player != null){
                ScreenHandler screenHandler = client.player.currentScreenHandler;
                if (screenHandler instanceof EnhancedEnchantingAccessor a && screenHandler.syncId == syncId){
                    a.setRecipe(recipe);
                }
            }
        });
    }
}
