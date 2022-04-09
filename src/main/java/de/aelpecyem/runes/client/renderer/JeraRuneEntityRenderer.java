package de.aelpecyem.runes.client.renderer;

import de.aelpecyem.runes.common.entity.JeraRuneEntity;
import de.aelpecyem.runes.common.reg.RunesObjects;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class JeraRuneEntityRenderer extends EntityRenderer<JeraRuneEntity> {
    private static final ItemStack renderStack = new ItemStack(RunesObjects.JERA_RUNE);
    private final ItemRenderer itemRenderer;
    public JeraRuneEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(JeraRuneEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        float angle = 360 * ((entity.age + tickDelta) / 120F);
        matrices.push();
        matrices.translate(0, 0.25F, 0);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle));
        itemRenderer.renderItem(renderStack, ModelTransformation.Mode.FIXED,
                this.getLight(entity, tickDelta), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getId());
        matrices.pop();
    }

    @Override
    public Identifier getTexture(JeraRuneEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
