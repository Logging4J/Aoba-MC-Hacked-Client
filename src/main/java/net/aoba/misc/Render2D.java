package net.aoba.misc;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Render2D {
	private static final float ROUND_QUALITY = 10;

	public static void drawTexturedQuad(Matrix4f matrix4f, Identifier texture, float x1, float y1, float width, float height, Color color) {
		float red = color.getRedFloat();
		float green = color.getGreenFloat();
		float blue = color.getBlueFloat();
		float alpha = color.getAlphaFloat();

		float x2 = x1 + width;
		float y2 = y1 + height;

		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.enableBlend();
		BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(matrix4f, x1, y1, 0).color(red, green, blue, alpha).texture(0, 0);
		bufferBuilder.vertex(matrix4f, x1, y2, 0).color(red, green, blue, alpha).texture(0, 1);
		bufferBuilder.vertex(matrix4f, x2, y2, 0).color(red, green, blue, alpha).texture(1, 1);
		bufferBuilder.vertex(matrix4f, x2, y1, 0).color(red, green, blue, alpha).texture(1, 0);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.disableBlend();
	}

	public static void drawBox(Matrix4f matrix4f, float x, float y, float width, float height, Color color) {

		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();

		RenderSystem.setShader(GameRenderer::getPositionProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, x, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0);
		bufferBuilder.vertex(matrix4f, x, y + height, 0);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawCircle(Matrix4f matrix4f, float x, float y, float radius, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION);
		double roundedInterval = (360.0f / 30.0f);

		for (int i = 0; i < 30; i++) {
			double angle = Math.toRadians(0 + (i * roundedInterval));
			double angle2 = Math.toRadians(0 + ((i + 1) * roundedInterval));
			float radiusX1 = (float) (Math.cos(angle) * radius);
			float radiusY1 = (float) Math.sin(angle) * radius;
			float radiusX2 = (float) Math.cos(angle2) * radius;
			float radiusY2 = (float) Math.sin(angle2) * radius;

			bufferBuilder.vertex(matrix4f, x, y, 0);
			bufferBuilder.vertex(matrix4f, x + radiusX1, y + radiusY1, 0);
			bufferBuilder.vertex(matrix4f, x + radiusX2, y + radiusY2, 0);
		}
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawRoundedBox(Matrix4f matrix4f, float x, float y, float width, float height, float radius,
			Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION);
		buildFilledArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f);
		buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f);
		buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f);
		buildFilledArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f);

// |---
		bufferBuilder.vertex(matrix4f, x + radius, y, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y, 0);
		bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0);

// ---|
		bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0);

// _||
		bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + width, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0);

// |||
		bufferBuilder.vertex(matrix4f, x + width, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0);

/// __|
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0);
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0);

// |__
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0);
		bufferBuilder.vertex(matrix4f, x + radius, y + height, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0);

// |||
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0);
		bufferBuilder.vertex(matrix4f, x, y + height - radius, 0);
		bufferBuilder.vertex(matrix4f, x, y + radius, 0);

/// ||-
		bufferBuilder.vertex(matrix4f, x, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0);

/// |-/
		bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0);

/// /_|
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0);

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawRoundedOutline(Matrix4f matrix4f, float x, float y, float width, float height, float radius,
			Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
// Top Left Arc and Top
		buildArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f);
		bufferBuilder.vertex(matrix4f, x + radius, y, 0);
		bufferBuilder.vertex(matrix4f, x + width - radius, y, 0);

// Top Right Arc and Right
		buildArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f);
		bufferBuilder.vertex(matrix4f, x + width, y + radius, 0);
		bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0);

// Bottom Right
		buildArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0);
		bufferBuilder.vertex(matrix4f, x + radius, y + height, 0);

// Bottom Left
		buildArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f);
		bufferBuilder.vertex(matrix4f, x, y + height - radius, 0);
		bufferBuilder.vertex(matrix4f, x, y + radius, 0);

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void drawTranslucentBlurredRoundedBox(Matrix4f matrix4f, float x, float y, float width, float height,
			float radius, Color color) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		for (int i = 0; i < 5; i++) {
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			float alpha = color.getAlphaFloat() * (1.0f / (i + 1)); // Adjust alpha for each blur layer

			RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);

			drawRoundedBox(matrix4f, x - i, y - i, width + 2 * i, height + 2 * i, radius + i, color);
		}

		// Draw the main rounded box
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());
		drawRoundedBox(matrix4f, x, y, width, height, radius, color);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawOutlinedBox(Matrix4f matrix4f, float x, float y, float width, float height,
			Color outlineColor, Color backgroundColor) {
		RenderSystem.setShaderColor(backgroundColor.getRedFloat(), backgroundColor.getGreenFloat(),
				backgroundColor.getBlueFloat(), backgroundColor.getAlphaFloat());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, x, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0);
		bufferBuilder.vertex(matrix4f, x, y + height, 0);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

		RenderSystem.setShaderColor(outlineColor.getRedFloat(), outlineColor.getGreenFloat(),
				outlineColor.getBlueFloat(), outlineColor.getAlphaFloat());
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, x, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0);
		bufferBuilder.vertex(matrix4f, x, y + height, 0);
		bufferBuilder.vertex(matrix4f, x, y, 0);

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.setShaderColor(1, 1, 1, 1);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawOutlinedBox(Matrix4f matrix4f, float x, float y, float width, float height, Color color) {
		drawOutlinedBox(matrix4f, x, y, width, height, Colors.Black, color);
	}

	public static void drawLine(Matrix4f matrix4f, float x1, float y1, float x2, float y2, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, x1, y1, 0);
		bufferBuilder.vertex(matrix4f, x2, y2, 0);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

		RenderSystem.setShaderColor(1, 1, 1, 1);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawHorizontalGradient(Matrix4f matrix4f, float x, float y, float width, float height,
			Color startColor, Color endColor) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(startColor.getColorAsInt());
		bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(endColor.getColorAsInt());
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(endColor.getColorAsInt());
		bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(startColor.getColorAsInt());

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawVerticalGradient(Matrix4f matrix4f, float x, float y, float width, float height,
			Color startColor, Color endColor) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(startColor.getColorAsInt());
		bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(startColor.getColorAsInt());
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(endColor.getColorAsInt());
		bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(endColor.getColorAsInt());

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawOutline(Matrix4f matrix4f, float x, float y, float width, float height) {
		RenderSystem.setShaderColor(0, 0, 0, 1);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, x, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0);
		bufferBuilder.vertex(matrix4f, x, y + height, 0);
		bufferBuilder.vertex(matrix4f, x, y, 0);

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.setShaderColor(1, 1, 1, 1);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawOutline(Matrix4f matrix4f, float x, float y, float width, float height, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, x, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y, 0);
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0);
		bufferBuilder.vertex(matrix4f, x, y + height, 0);
		bufferBuilder.vertex(matrix4f, x, y, 0);

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.setShaderColor(1, 1, 1, 1);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawItem(DrawContext drawContext, ItemStack stack, float x, float y) {
		MinecraftClient MC = MinecraftClient.getInstance();
		BakedModel bakedModel = MC.getItemRenderer().getModel(stack, null, null, 0);

		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.translate(x + 8, y + 8, 150);
		matrixStack.scale(16.0f, -16.0f, 16.0f);
		DiffuseLighting.disableGuiDepthLighting();
		MC.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, matrixStack,
				drawContext.getVertexConsumers(), 0xFFFFFF, OverlayTexture.DEFAULT_UV, bakedModel);
		DiffuseLighting.enableGuiDepthLighting();
		matrixStack.pop();
	}
	
	public static void drawString(DrawContext drawContext, String text, float x, float y, Color color) {
        AobaClient aoba = Aoba.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 1.0f);
        matrixStack.translate(-x / 2, -y / 2, 0.0f);
        drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
        matrixStack.pop();
    }

    public static void drawString(DrawContext drawContext, String text, float x, float y, int color) {
        AobaClient aoba = Aoba.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 1.0f);
        matrixStack.translate(-x / 2, -y / 2, 0.0f);
        drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
        matrixStack.pop();
    }

    public static void drawStringWithScale(DrawContext drawContext, String text, float x, float y, Color color,
                                           float scale) {
        AobaClient aoba = Aoba.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        matrixStack.scale(scale, scale, 1.0f);
        if (scale > 1.0f) {
            matrixStack.translate(-x / scale, -y / scale, 0.0f);
        } else {
            matrixStack.translate((x / scale) - x, (y * scale) - y, 0.0f);
        }
        drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
        matrixStack.pop();
    }

    public static void drawStringWithScale(DrawContext drawContext, String text, float x, float y, int color,
                                           float scale) {
        AobaClient aoba = Aoba.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        matrixStack.scale(scale, scale, 1.0f);
        if (scale > 1.0f) {
            matrixStack.translate(-x / scale, -y / scale, 0.0f);
        } else {
            matrixStack.translate(x / scale, y * scale, 0.0f);
        }
        drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
        matrixStack.pop();
    }

    private static void buildFilledArc(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float radius,
                                       float startAngle, float sweepAngle) {
        double roundedInterval = (sweepAngle / ROUND_QUALITY);

        for (int i = 0; i < ROUND_QUALITY; i++) {
            double angle = Math.toRadians(startAngle + (i * roundedInterval));
            double angle2 = Math.toRadians(startAngle + ((i + 1) * roundedInterval));
            float radiusX1 = (float) (Math.cos(angle) * radius);
            float radiusY1 = (float) Math.sin(angle) * radius;
            float radiusX2 = (float) Math.cos(angle2) * radius;
            float radiusY2 = (float) Math.sin(angle2) * radius;

            bufferBuilder.vertex(matrix, x, y, 0);
            bufferBuilder.vertex(matrix, x + radiusX1, y + radiusY1, 0);
            bufferBuilder.vertex(matrix, x + radiusX2, y + radiusY2, 0);
        }
    }

    private static void buildArc(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float radius,
                                 float startAngle, float sweepAngle) {
        double roundedInterval = (sweepAngle / ROUND_QUALITY);

        for (int i = 0; i < ROUND_QUALITY; i++) {
            double angle = Math.toRadians(startAngle + (i * roundedInterval));
            float radiusX1 = (float) (Math.cos(angle) * radius);
            float radiusY1 = (float) Math.sin(angle) * radius;

            bufferBuilder.vertex(matrix, x + radiusX1, y + radiusY1, 0);
        }
    }
}