package iunius118.mods.cc3dprojector.client.renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import iunius118.mods.cc3dprojector.peripheral.ModelProgramProcessor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class Renderer3DModel {

	public static void doRender(RenderWorldLastEvent event, Vec3 pos, float yaw, List<Map<Integer, Object>> model, boolean isTurtle) {
		Object obj;
		Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GL11.glLineWidth(1.0F);
		GL11.glPointSize(1.0F);

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.translate(pos.xCoord, pos.yCoord + 1.0D, pos.zCoord);
		GlStateManager.rotate(yaw, 0.0f, -1.0f, 0.0f);

		if (isTurtle) {
			GlStateManager.translate(-0.5D, -0.5D, -0.5D);
		}

		for (Map<Integer, Object> statement : model) {
			obj = statement.get(Integer.valueOf(1));

			if (obj instanceof String) {
				String command = (String)obj;

				if (command.equals(ModelProgramProcessor.NAME_COLOR) && statement.size() == ModelProgramProcessor.SIZE_COLOR) {
					int c = (Integer)statement.get(Integer.valueOf(2));

					switch(c) {
					case 0:
						color.setColor(0.941f, 0.941f, 0.941f);
						break;
					case 1:
						color.setColor(0.949f, 0.698f, 0.2f);
						break;
					case 2:
						color.setColor(0.898f, 0.498f, 0.847f);
						break;
					case 3:
						color.setColor(0.6f, 0.698f, 0.949f);
						break;
					case 4:
						color.setColor(0.871f, 0.871f, 0.424f);
						break;
					case 5:
						color.setColor(0.498f, 0.8f, 0.098f);
						break;
					case 6:
						color.setColor(0.949f, 0.698f, 0.8f);
						break;
					case 7:
						color.setColor(0.298f, 0.298f, 0.298f);
						break;
					case 8:
						color.setColor(0.6f, 0.6f, 0.6f);
						break;
					case 9:
						color.setColor(0.298f, 0.6f, 0.698f);
						break;
					case 10:
						color.setColor(0.698f, 0.4f, 0.898f);
						break;
					case 11:
						color.setColor(0.2f, 0.4f, 0.8f);
						break;
					case 12:
						color.setColor(0.498f, 0.4f, 0.298f);
						break;
					case 13:
						color.setColor(0.341f, 0.651f, 0.306f);
						break;
					case 14:
						color.setColor(0.8f, 0.298f, 0.298f);
						break;
					case 15:
						color.setColor(0.098f, 0.098f, 0.098f);
						break;
					}

					GlStateManager.color(color.r, color.g, color.b, color.a);

				} else if (command.equals(ModelProgramProcessor.NAME_TRANSPARENCY) && statement.size() == ModelProgramProcessor.SIZE_TRANSPARENCY) {
					float f = (Float)statement.get(Integer.valueOf(2));
					color.a = f;
					GlStateManager.color(color.r, color.g, color.b, color.a);

				} else if (command.equals(ModelProgramProcessor.NAME_POINTS) && statement.size() >= ModelProgramProcessor.SIZE_POINTS) {
					worldrenderer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION);

					for (int i = 1; i < statement.size(); i++) {
						Map<Integer, Float> p = (Map<Integer, Float>)statement.get(Integer.valueOf(i + 1));
						worldrenderer.pos((double)p.get(Integer.valueOf(1)), (double)p.get(Integer.valueOf(2)), (double)p.get(Integer.valueOf(3))).endVertex();
					}

					tessellator.draw();

				} else if (command.equals(ModelProgramProcessor.NAME_LINE_STRIP) && statement.size() >= ModelProgramProcessor.SIZE_LINE_STRIP) {
					worldrenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

					for (int i = 1; i < statement.size(); i++) {
						Map<Integer, Float> p = (Map<Integer, Float>)statement.get(Integer.valueOf(i + 1));
						worldrenderer.pos((double)p.get(Integer.valueOf(1)), (double)p.get(Integer.valueOf(2)), (double)p.get(Integer.valueOf(3))).endVertex();
					}

					tessellator.draw();

				} else if (command.equals(ModelProgramProcessor.NAME_LINE_LOOP) && statement.size() >= ModelProgramProcessor.SIZE_LINE_LOOP) {
					worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);

					for (int i = 1; i < statement.size(); i++) {
						Map<Integer, Float> p = (Map<Integer, Float>)statement.get(Integer.valueOf(i + 1));
						worldrenderer.pos((double)p.get(Integer.valueOf(1)), (double)p.get(Integer.valueOf(2)), (double)p.get(Integer.valueOf(3))).endVertex();
					}

					tessellator.draw();

				} else if (command.equals(ModelProgramProcessor.NAME_FACE) && statement.size() == ModelProgramProcessor.SIZE_FACE) {
					Map<Integer, Float> p1 = (Map<Integer, Float>)statement.get(Integer.valueOf(2));
					Map<Integer, Float> p2 = (Map<Integer, Float>)statement.get(Integer.valueOf(3));
					Map<Integer, Float> p3 = (Map<Integer, Float>)statement.get(Integer.valueOf(4));
					Map<Integer, Float> p4 = (Map<Integer, Float>)statement.get(Integer.valueOf(5));

					worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
					worldrenderer.pos((double)p1.get(Integer.valueOf(1)), (double)p1.get(Integer.valueOf(2)), (double)p1.get(Integer.valueOf(3))).endVertex();
					worldrenderer.pos((double)p2.get(Integer.valueOf(1)), (double)p2.get(Integer.valueOf(2)), (double)p2.get(Integer.valueOf(3))).endVertex();
					worldrenderer.pos((double)p3.get(Integer.valueOf(1)), (double)p3.get(Integer.valueOf(2)), (double)p3.get(Integer.valueOf(3))).endVertex();
					worldrenderer.pos((double)p4.get(Integer.valueOf(1)), (double)p4.get(Integer.valueOf(2)), (double)p4.get(Integer.valueOf(3))).endVertex();
					tessellator.draw();

				} else if (command.equals(ModelProgramProcessor.NAME_TRANSLATE) && statement.size() == ModelProgramProcessor.SIZE_TRANSLATE) {
					Map<Integer, Float> p = (Map<Integer, Float>)statement.get(Integer.valueOf(2));
					GlStateManager.translate((double)p.get(Integer.valueOf(1)), (double)p.get(Integer.valueOf(2)), (double)p.get(Integer.valueOf(3)));

				} else if (command.equals(ModelProgramProcessor.NAME_ROTATE_X) && statement.size() == ModelProgramProcessor.SIZE_ROTATE_X) {
					float f = (Float)statement.get(Integer.valueOf(2));
					GlStateManager.rotate(f, 1.0f, 0.0f, 0.0f);

				} else if (command.equals(ModelProgramProcessor.NAME_ROTATE_Y) && statement.size() == ModelProgramProcessor.SIZE_ROTATE_Y) {
					float f = (Float)statement.get(Integer.valueOf(2));
					GlStateManager.rotate(f, 0.0f, 1.0f, 0.0f);

				} else if (command.equals(ModelProgramProcessor.NAME_ROTATE_Z) && statement.size() == ModelProgramProcessor.SIZE_ROTATE_Z) {
					float f = (Float)statement.get(Integer.valueOf(2));
					GlStateManager.rotate(f, 0.0f, 0.0f, 1.0f);

				} else if (command.equals(ModelProgramProcessor.NAME_SCALE) && statement.size() == ModelProgramProcessor.SIZE_SCALE) {
					Map<Integer, Float> p = (Map<Integer, Float>)statement.get(Integer.valueOf(2));
					GlStateManager.scale((double)p.get(Integer.valueOf(1)), (double)p.get(Integer.valueOf(2)), (double)p.get(Integer.valueOf(3)));
				}
			}
		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

	public static class Color {
		public float r;
		public float g;
		public float b;
		public float a;

		public Color(float red, float green, float blue, float alpha) {
			r = red;
			g = green;
			b = blue;
			a = alpha;
		}

		public void setColor(float red, float green, float blue, float alpha) {
			r = red;
			g = green;
			b = blue;
			a = alpha;
		}

		public void setColor(float red, float green, float blue) {
			r = red;
			g = green;
			b = blue;
		}
	}

}
