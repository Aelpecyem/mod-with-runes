package de.aelpecyem.runes.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.reg.RunesObjects;
import de.aelpecyem.runes.mixin.client.HandledScreenAccessor;
import de.aelpecyem.runes.util.EnhancedEnchantingAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class RuneScreen extends DrawableHelper {
    private static final Identifier TEXTURE = RunesMod.id("textures/gui/container/runes_table.png");
    private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
    private final BookModel BOOK_MODEL = new BookModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.BOOK));
    private static final int pixelWidth = 8;
    private final EnchantmentScreen screen;
    private final EnchantmentScreenHandler handler;
    private final EnhancedEnchantingAccessor accessor;

    public RuneScreen(EnchantmentScreen screen, EnchantmentScreenHandler handler, EnhancedEnchantingAccessor accessor) {
        this.screen = screen;
        this.handler = handler;
        this.accessor = accessor;
    }

    public void render(MinecraftClient client, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(getOffsetX(), getOffsetY(), 0);
        renderItemBackground(client, matrices);
        int selectionX = (mouseX - getOffsetX()) / pixelWidth;
        int selectionY = (mouseY - getOffsetY()) / pixelWidth;
        matrices.translate(0, 0, 2);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                boolean selected = x >= selectionX && x < selectionX + 1 && y >= selectionY && y < selectionY + 1;
                boolean active = accessor.getPixel(x, y) > 0;
                if (selected || active) {
                    drawColoredQuad(matrices.peek().getModel(), x, y, active ? selected ? 0x444444 : 0x000000 : 0xAAAAAA);
                }
            }
        }
        matrices.pop();
        drawBackground(client, matrices, delta, mouseX, mouseY);

        if (accessor.getRecipe() == null){
            matrices.push();
            matrices.translate(getOffsetX() - 39, getOffsetY() + 44, 275 + getZOffset());
            RenderSystem.enableDepthTest();
            matrices.scale(1/8F, 1/8F, 1);
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    boolean active = accessor.getPixel(x, y) > 0;
                    if (active) {
                        drawColoredQuad(matrices.peek().getModel(), x, y, 0x000000);
                    }
                }
            }

            RenderSystem.disableDepthTest();
            matrices.pop();
        }else{
            matrices.push();
            matrices.translate(getOffsetX() + 80, getOffsetY(), 0);
            client.textRenderer.drawWithShadow(matrices, new TranslatableText("text." + RunesMod.MOD_ID + ".xp_cost", accessor.getRecipe().xpCost()), -14, 0, client.player.experienceLevel < accessor.getRecipe().xpCost() ? 16733525 : 16777088);
            if (handler.getStacks().get(1).isEmpty()){
                client.textRenderer.drawTrimmed(new TranslatableText("text." + RunesMod.MOD_ID + ".missing_lapis", accessor.getRecipe().xpCost()), getOffsetX() + 67, getOffsetY() + 13, 50, 0);
                client.textRenderer.drawTrimmed(new TranslatableText("text." + RunesMod.MOD_ID + ".missing_lapis", accessor.getRecipe().xpCost()), getOffsetX() + 66, getOffsetY() + 12, 50, 16733525);
            }
            matrices.pop();
        }
    }

    private void renderItemBackground(MinecraftClient client, MatrixStack matrices) {
        int scissorX = unscale(getOffsetX(), client.getWindow().getScaledWidth(), client.getWindow().getWidth());
        int scissorY = unscale(client.getWindow().getScaledHeight() - getOffsetY() - 64, client.getWindow().getScaledHeight(), client.getWindow().getHeight());
        int scissorWidth = unscale(64, client.getWindow().getScaledWidth(), client.getWindow().getWidth());
        int scissorHeight = unscale(64, client.getWindow().getScaledHeight(), client.getWindow().getHeight());
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        DiffuseLighting.disableGuiDepthLighting();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.push();
        matrices.translate(32, 24, 0);
        matrices.scale(16 * 8, -16 * 8, 16 * 8);
        client.getItemRenderer().renderItem(RunesObjects.SMOOTH_SLATE.getDefaultStack(), ModelTransformation.Mode.GUI, LightmapTextureManager.pack(15, 15), OverlayTexture.DEFAULT_UV, matrices, immediate, handler.getSeed());
        immediate.draw();
        matrices.pop();
        RenderSystem.disableScissor();
    }

    private static int unscale(int scaled, float windowScaled, int windowReal) {
        return (int) ((scaled / windowScaled) * windowReal);
    }

    protected void drawBackground(MinecraftClient client, MatrixStack matrices, float delta, int mouseX, int mouseY) {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (screen.width - ((HandledScreenAccessor) screen).getBackgroundWidth()) / 2;
        int j = (screen.height - ((HandledScreenAccessor) screen).getBackgroundHeight()) / 2;
        this.drawTexture(matrices, i, j, 0, 0, ((HandledScreenAccessor) screen).getBackgroundWidth(), ((HandledScreenAccessor) screen).getBackgroundHeight());
        int k = (int) client.getWindow().getScaleFactor();
        RenderSystem.viewport((screen.width - 320) / 2 * k, (screen.height - 240) / 2 * k, 320 * k, 240 * k);
        Matrix4f matrix4f = Matrix4f.translate(-0.34F, 0.23F, 0.0F);
        matrix4f.multiply(Matrix4f.viewboxMatrix(90.0D, 1.3333334F, 9.0F, 80.0F));
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(matrix4f);
        matrices.push();
        MatrixStack.Entry entry = matrices.peek();
        entry.getModel().loadIdentity();
        entry.getNormal().loadIdentity();
        matrices.translate(0.0D, 3.299999952316284D, 1984.0D);
        matrices.scale(5.0F, 5.0F, 5.0F);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(20.0F));
        float g = MathHelper.lerp(delta, screen.pageTurningSpeed, screen.nextPageTurningSpeed);
        matrices.translate((1.0F - g) * 0.2F, (1.0F - g) * 0.1F, (1.0F - g) * 0.25F);
        float h = -(1.0F - g) * 90.0F - 90.0F;
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(h));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
        float l = MathHelper.lerp(delta, screen.pageAngle, screen.nextPageAngle) + 0.25F;
        float m = MathHelper.lerp(delta, screen.pageAngle, screen.nextPageAngle) + 0.75F;
        l = (l - (float) MathHelper.fastFloor(l)) * 1.6F - 0.3F;
        m = (m - (float) MathHelper.fastFloor(m)) * 1.6F - 0.3F;
        if (l < 0.0F) {
            l = 0.0F;
        }

        if (m < 0.0F) {
            m = 0.0F;
        }

        if (l > 1.0F) {
            l = 1.0F;
        }

        if (m > 1.0F) {
            m = 1.0F;
        }

        this.BOOK_MODEL.setPageAngles(0.0F, l, m, g);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        VertexConsumer vertexConsumer = immediate.getBuffer(this.BOOK_MODEL.getLayer(BOOK_TEXTURE));
        this.BOOK_MODEL.render(matrices, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        immediate.draw();
        matrices.pop();
        RenderSystem.viewport(0, 0, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
        RenderSystem.restoreProjectionMatrix();
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantingPhrases.getInstance().setSeed(this.handler.getSeed());
    }

    public boolean mouseClicked(MinecraftClient client, double mouseX, double mouseY, int button) {
        int selectionX = (int) ((mouseX - getOffsetX()) / pixelWidth);
        int selectionY = (int) ((mouseY - getOffsetY()) / pixelWidth);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (x >= selectionX && x < selectionX + 1 && y >= selectionY && y < selectionY + 1) {
                    client.interactionManager.clickButton(this.handler.syncId, x + y * 8 + 3);
                    return true;
                }
            }
        }
        return false;
    }

    private int getOffsetX() {
        return screen.width / 2 - 30;
    }

    private int getOffsetY() {
        return screen.height / 2 - 75;
    }

    private static void drawColoredQuad(Matrix4f matrices, int x, int y, int color) {
        int xCoord = x * pixelWidth;
        int yCoord = y * pixelWidth;
        int xEnd = xCoord + pixelWidth;
        int yEnd = yCoord + pixelWidth;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrices, xCoord, yEnd, 0).color(r, g, b, 255).next();
        bufferBuilder.vertex(matrices, xEnd, yEnd, 0).color(r, g, b, 255).next();
        bufferBuilder.vertex(matrices, xEnd, yCoord, 0).color(r, g, b, 255).next();
        bufferBuilder.vertex(matrices, xCoord, yCoord, 0).color(r, g, b, 255).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
}