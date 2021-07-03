package de.aelpecyem.runes.client.model.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import de.aelpecyem.runes.util.StasisAccessor;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class StasisFeatureRenderer extends FeatureRenderer<LivingEntity, EntityModel<LivingEntity>> {
    private EntityModel<LivingEntity> model;
    public StasisFeatureRenderer(FeatureRendererContext<LivingEntity, EntityModel<LivingEntity>> context) {
        super(context);
        this.model = context.getModel();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof StasisAccessor accessor && accessor.getStasisTicks() > 0) {
            float f = ((float)entity.age + tickDelta) * 0.01F % 1;
            matrices.push();
            model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(new Identifier("textures/entity/creeper/creeper_armor.png"), f, f));
            model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            float ratio = MathHelper.clamp(accessor.getStasisTicks() / 20F, 0F, 1F);
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1 * ratio, 1 * ratio, 0F, 1.0F);
            matrices.pop();
        }
    }
}
