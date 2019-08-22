package net.github.iunius118.cc3dprojector.client.renderer;

import net.github.iunius118.cc3dprojector.peripheral.ModelProgramProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.animation.Animation;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

public class ThreeDModelRenderer {

    public static void doRender(RenderWorldLastEvent event, Vec3d pos, float yaw, List<Map<Integer, Object>> model, boolean isTurtle) {
        Object obj;
        Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        Oscillator oscillator = new Oscillator(0, 0.0f, 1.0f);
        boolean isOscillating = false;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GL11.glLineWidth(1.0F);
        GL11.glPointSize(1.0F);

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.translate(pos.x, pos.y + 1.0D, pos.z);
        GlStateManager.rotate(yaw, 0.0f, -1.0f, 0.0f);

        if (isTurtle) {
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
        }

        for (Map<Integer, Object> statement : model) {
            obj = statement.get(1);

            if (obj instanceof String) {
                String command = (String) obj;

                // Render each command
                if (command.equals(ModelProgramProcessor.NAME_COLOR) && statement.size() == ModelProgramProcessor.SIZE_COLOR) {
                    int c = (Integer) statement.get(2);

                    switch(c) {
                        case 0: 	// White
                            color.setColor(0.941f, 0.941f, 0.941f);
                            break;
                        case 1: 	// Orange
                            color.setColor(0.949f, 0.698f, 0.2f);
                            break;
                        case 2: 	// Magenta
                            color.setColor(0.898f, 0.498f, 0.847f);
                            break;
                        case 3: 	// LightBlue
                            color.setColor(0.6f, 0.698f, 0.949f);
                            break;
                        case 4: 	// Yellow
                            color.setColor(0.871f, 0.871f, 0.424f);
                            break;
                        case 5: 	// Lime
                            color.setColor(0.498f, 0.8f, 0.098f);
                            break;
                        case 6: 	// Pink
                            color.setColor(0.949f, 0.698f, 0.8f);
                            break;
                        case 7: 	// Gray
                            color.setColor(0.298f, 0.298f, 0.298f);
                            break;
                        case 8: 	// LightGray
                            color.setColor(0.6f, 0.6f, 0.6f);
                            break;
                        case 9: 	// Cyan
                            color.setColor(0.298f, 0.6f, 0.698f);
                            break;
                        case 10:	// Purple
                            color.setColor(0.698f, 0.4f, 0.898f);
                            break;
                        case 11:	// Blue
                            color.setColor(0.2f, 0.4f, 0.8f);
                            break;
                        case 12:	// Brown
                            color.setColor(0.498f, 0.4f, 0.298f);
                            break;
                        case 13:	// Green
                            color.setColor(0.341f, 0.651f, 0.306f);
                            break;
                        case 14:	// Red
                            color.setColor(0.8f, 0.298f, 0.298f);
                            break;
                        case 15:	// Black
                            color.setColor(0.098f, 0.098f, 0.098f);
                            break;
                    }

                    GlStateManager.color(color.r, color.g, color.b, color.a);

                } else if (command.equals(ModelProgramProcessor.NAME_OPACITY) && statement.size() == ModelProgramProcessor.SIZE_OPACITY) {
                    float f = (Float) statement.get(2);

                    if (isOscillating) {
                        f *= oscillator.oscillate();
                        f += color.a;

                        if (f < 0) {
                            f = 0;
                        }

                        if (f > 1) {
                            f = 1;
                        }

                        isOscillating = false;
                    } else {
                        color.a = f;
                    }

                    GlStateManager.color(color.r, color.g, color.b, f);

                } else if (command.equals(ModelProgramProcessor.NAME_OSCILLATE)) {
                    if (statement.size() == ModelProgramProcessor.SIZE_OSCILLATE) {
                        int type = (Integer) statement.get(2);
                        float phase = (Float) statement.get(3);
                        float period = (Float) statement.get(4);
                        oscillator.setState(type, phase, period);
                    }

                    isOscillating = true;

                } else if (command.equals(ModelProgramProcessor.NAME_POINTS) && statement.size() >= ModelProgramProcessor.SIZE_POINTS) {
                    worldRenderer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION);

                    for (int i = 1; i < statement.size(); i++) {
                        Map p = (Map) statement.get(i + 1);
                        worldRenderer.pos((double)(Float) p.get(1), (double)(Float) p.get(2), (double)(Float) p.get(3)).endVertex();
                    }

                    tessellator.draw();

                } else if (command.equals(ModelProgramProcessor.NAME_LINE_STRIP) && statement.size() >= ModelProgramProcessor.SIZE_LINE_STRIP) {
                    worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

                    for (int i = 1; i < statement.size(); i++) {
                        Map p = (Map) statement.get(i + 1);
                        worldRenderer.pos((double)(Float) p.get(1), (double)(Float) p.get(2), (double)(Float) p.get(3)).endVertex();
                    }

                    tessellator.draw();

                } else if (command.equals(ModelProgramProcessor.NAME_LINE_LOOP) && statement.size() >= ModelProgramProcessor.SIZE_LINE_LOOP) {
                    worldRenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);

                    for (int i = 1; i < statement.size(); i++) {
                        Map p = (Map) statement.get(i + 1);
                        worldRenderer.pos((double)(Float) p.get(1), (double)(Float) p.get(2), (double)(Float) p.get(3)).endVertex();
                    }

                    tessellator.draw();

                } else if (command.equals(ModelProgramProcessor.NAME_FACE) && statement.size() == ModelProgramProcessor.SIZE_FACE) {
                    Map<Integer, Float> p1 = (Map<Integer, Float>) statement.get(2);
                    Map p2 = (Map) statement.get(3);
                    Map p3 = (Map) statement.get(4);
                    Map p4 = (Map) statement.get(5);

                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
                    worldRenderer.pos((double)(Float) p1.get(1), (double)(Float) p1.get(2), (double)(Float) p1.get(3)).endVertex();
                    worldRenderer.pos((double)(Float) p2.get(1), (double)(Float) p2.get(2), (double)(Float) p2.get(3)).endVertex();
                    worldRenderer.pos((double)(Float) p3.get(1), (double)(Float) p3.get(2), (double)(Float) p3.get(3)).endVertex();
                    worldRenderer.pos((double)(Float) p4.get(1), (double)(Float) p4.get(2), (double)(Float) p4.get(3)).endVertex();
                    tessellator.draw();

                } else if (command.equals(ModelProgramProcessor.NAME_TRANSLATE) && statement.size() == ModelProgramProcessor.SIZE_TRANSLATE) {
                    Map p = (Map) statement.get(2);
                    double x = (double)(Float) p.get(1);
                    double y = (double)(Float) p.get(2);
                    double z = (double)(Float) p.get(3);

                    if (isOscillating) {
                        double d = (double) oscillator.oscillate();
                        x *= d;
                        y *= d;
                        z *= d;
                        isOscillating = false;
                    }

                    GlStateManager.translate(x, y, z);

                } else if (command.equals(ModelProgramProcessor.NAME_ROTATE_X) && statement.size() == ModelProgramProcessor.SIZE_ROTATE_X) {
                    float f = (Float) statement.get(2);

                    if (isOscillating) {
                        f *= oscillator.oscillate();
                        isOscillating = false;
                    }

                    GlStateManager.rotate(f, 1.0f, 0.0f, 0.0f);

                } else if (command.equals(ModelProgramProcessor.NAME_ROTATE_Y) && statement.size() == ModelProgramProcessor.SIZE_ROTATE_Y) {
                    float f = (Float) statement.get(2);

                    if (isOscillating) {
                        f *= oscillator.oscillate();
                        isOscillating = false;
                    }

                    GlStateManager.rotate(f, 0.0f, 1.0f, 0.0f);

                } else if (command.equals(ModelProgramProcessor.NAME_ROTATE_Z) && statement.size() == ModelProgramProcessor.SIZE_ROTATE_Z) {
                    float f = (Float) statement.get(2);

                    if (isOscillating) {
                        f *= oscillator.oscillate();
                        isOscillating = false;
                    }

                    GlStateManager.rotate(f, 0.0f, 0.0f, 1.0f);

                } else if (command.equals(ModelProgramProcessor.NAME_SCALE) && statement.size() == ModelProgramProcessor.SIZE_SCALE) {
                    Map p = (Map) statement.get(2);
                    double x = (double) p.get(1);
                    double y = (double) p.get(2);
                    double z = (double) p.get(3);

                    if (isOscillating) {
                        double d = (double) oscillator.oscillate();
                        x *= d;
                        y *= d;
                        z *= d;
                        isOscillating = false;
                    }

                    GlStateManager.scale(x, y, z);
                }
            }
        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
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

    public static class Oscillator {

        private int _type;
        private float _phase;
        private float _period;

        public Oscillator(int type, float phase, float period) {
            setState(type, phase, period);
        }

        public void setState(int type, float phase, float period) {
            _type = type;
            _phase = phase;
            _period = period;

            if (_phase >= 1.0f || _phase < 0.0f) {
                _phase = 0.0f;
            }

            if (_period <= 0.0f) {
                _period = 1.0f;
            }
        }

        public float oscillate() {
            float time = Animation.getWorldTime(Minecraft.getMinecraft().world, Animation.getPartialTickTime()) + _phase * _period;
            float ret = 0.0f;

            switch (_type) {
                case 0:	// linear or sawtooth
                    ret = (time % _period) * 2.0f / _period - 1.0f;
                    break;

                case 1:	// sin
                    ret = (float)Math.sin((time % _period) * 2.0f * (float)Math.PI / _period);
                    break;

                case 2:	// square
                    ret = (time % _period) * 2.0f / _period - 1.0f;
                    if (ret < 0.0f) {
                        ret = -1.0f;
                    } else {
                        ret = 1.0f;
                    }
                    break;

                case 3:	// triangle
                    ret = (time % _period) * 4.0f / _period - 1.0f;
                    if (ret > 1.0f) {
                        ret = 2.0f - ret;
                    }
                    break;
            }

            return ret;
        }

    }

}
