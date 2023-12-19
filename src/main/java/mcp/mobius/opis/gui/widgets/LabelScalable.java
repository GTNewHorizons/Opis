package mcp.mobius.opis.gui.widgets;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import mcp.mobius.opis.gui.interfaces.IWidget;

public class LabelScalable extends LabelFixedFont {

    private float scale = 1.0f;

    public LabelScalable(IWidget parent, String text) {
        super(parent, text);
    }

    public LabelScalable(IWidget parent, String text, int color) {
        super(parent, text, color);
    }

    public LabelScalable setScale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public void draw(Point pos) {
        this.saveGLState();
        GL11.glScalef(this.scale, this.scale, 1.0f);
        this.mc.fontRenderer
                .drawString(this.text, (int) (pos.getX() / this.scale), (int) (pos.getY() / this.scale), this.color);
        this.loadGLState();
    }
}
