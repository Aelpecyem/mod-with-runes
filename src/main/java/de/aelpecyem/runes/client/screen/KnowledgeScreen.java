package de.aelpecyem.runes.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeScreen extends Screen {
    private static final int pixelWidth = 8;
    private final int pixels[];
    private List<Text> text;
    public KnowledgeScreen(int[] pixels, Identifier id) {
        super(new TranslatableText("knowledge_text." + id.toString().replace(':', '.') + ".title"));
        this.pixels = pixels;
        this.text = splitText(new TranslatableText("knowledge_text." + id.toString().replace(':', '.') + ".desc"));
    }

    private List<Text> splitText(TranslatableText text) {
        List<Text> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int length = 0;
        for (String s : text.getString().split(" ")) {
            length += s.length();
            if (length > 30 || s.startsWith("<br>")){
                list.add(new LiteralText(builder.toString()));
                builder = new StringBuilder();
                length = 0;
            }
            builder.append(s.replace("<br>", ""));
            builder.append(" ");
        }
        list.add(new LiteralText(builder.toString()));
        return list;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        matrices.push();
        matrices.translate(width / 2F, height / 2F, 0);
        matrices.translate(-100, -50, 0);
        drawCenteredText(matrices, textRenderer, getTitle(), pixelWidth * 4, -10, 0xFFFFFF);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (pixels[x + y * 8] == 1) {
                    drawColoredQuad(matrices.peek().getPositionMatrix(), x, y, 0xFFFFFF);
                }
            }
            drawHorizontalLine(matrices, 0, 8 * pixelWidth, x * pixelWidth, -1);
            drawVerticalLine(matrices, x * pixelWidth, 0, 8 * pixelWidth, -1);
        }
        drawHorizontalLine(matrices, 0, 8 * pixelWidth, 8 * pixelWidth, -1);
        drawVerticalLine(matrices, 8 * pixelWidth, 0, 8 * pixelWidth, -1);
        matrices.push();
        matrices.scale(0.8F, 0.8F, 1F);
        matrices.translate(0, 4, 0);
        for (int i = 0; i < text.size(); i++) {
            drawTextWithShadow(matrices, textRenderer, text.get(i), pixelWidth * 11, i * pixelWidth, 0xFFFFFF);
        }
        matrices.pop();
        matrices.pop();
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
