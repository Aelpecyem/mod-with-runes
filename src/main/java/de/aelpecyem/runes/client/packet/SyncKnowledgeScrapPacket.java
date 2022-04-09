package de.aelpecyem.runes.client.packet;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.recipe.RuneEnchantingRecipe;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.util.EnhancedEnchantingAccessor;
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
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncKnowledgeScrapPacket {
    public static final Identifier ID = RunesMod.id("sync_scrap");

    public static void send(ServerPlayerEntity player, RuneEnchantingRecipe recipe) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(player.getId());
        if (recipe != null) {
            recipe.toPacket(buf);
        }
        PlayerLookup.tracking(player).forEach(tracked -> ServerPlayNetworking.send(tracked, ID, buf));
        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int entityId = buf.readInt();
        RuneEnchantingRecipe recipe =RuneEnchantingRecipe.fromPacket(buf);
        client.execute(() -> {
            if (client.player != null){
                Entity entity = client.world.getEntityById(entityId);
                if (entity instanceof PlayerEntity p){
                    RunesObjects.KNOWLEDGE_FRAGMENT.setKnowledge(p.getMainHandStack(), recipe);
                }
            }
        });
    }
}
