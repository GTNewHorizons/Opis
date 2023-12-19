package mcp.mobius.opis.gui.widgets;

import org.lwjgl.util.Point;

import mcp.mobius.opis.gui.font.FontHelper;
import mcp.mobius.opis.gui.font.TrueTypeFont;
import mcp.mobius.opis.gui.interfaces.CType;
import mcp.mobius.opis.gui.interfaces.IWidget;

public class LabelTTF extends WidgetBase {

    protected TrueTypeFont font;
    protected String text = "";
    protected int color;
    protected float scale = 1.0f;

    public LabelTTF(IWidget parent, String text, TrueTypeFont font) {
        super(parent);
        this.font = font;
        this.color = 0xFFFFFF;
        this.setText(text);
    }

    public LabelTTF(IWidget parent, String text, int color, TrueTypeFont font) {
        super(parent);
        this.font = font;
        this.color = color;
        this.setText(text);
    }

    public void setText(String text) {
        this.text = text;
        this.updateGeometry();
    }

    @Override
    public IWidget setGeometry(WidgetGeometry geom) {
        this.geom = geom;
        this.updateGeometry();
        return this;
    }

    public LabelTTF setScale(float scale) {
        this.scale = scale;
        return this;
    }

    protected void updateGeometry() {
        if (this.geom == null) this.geom = new WidgetGeometry(0, 0, 50, 50, CType.ABSXY, CType.ABSXY);

        if (this.font == null || this.text == null) return;

        this.geom = new WidgetGeometry(
                this.geom.x,
                this.geom.y,
                this.font.getWidth(this.text),
                this.font.getHeight(this.text) / this.rez.getScaleFactor(),
                this.geom.posType,
                CType.ABSXY,
                this.geom.alignX,
                this.geom.alignY);
    }

    @Override
    public void draw(Point pos) {
        this.saveGLState();
        FontHelper.drawString(this.text, pos.getX(), pos.getY(), this.font, this.scale, this.scale, this.color);
        this.loadGLState();
    }
}
