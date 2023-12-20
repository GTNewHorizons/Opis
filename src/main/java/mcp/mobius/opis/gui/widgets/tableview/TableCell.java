package mcp.mobius.opis.gui.widgets.tableview;

import org.lwjgl.util.Point;

import mcp.mobius.opis.gui.helpers.UIException;
import mcp.mobius.opis.gui.interfaces.CType;
import mcp.mobius.opis.gui.interfaces.IWidget;
import mcp.mobius.opis.gui.interfaces.WAlign;
import mcp.mobius.opis.gui.widgets.LabelTTF;
import mcp.mobius.opis.gui.widgets.WidgetBase;
import mcp.mobius.opis.gui.widgets.WidgetGeometry;
import mcp.mobius.opis.proxy.ProxyClient;

class TableCell extends WidgetBase {

    float fontSize = 1.0f;

    public TableCell(IWidget parent, String text, WAlign align, float fontSize) {
        super(parent);

        this.fontSize = fontSize;

        // this.addWidget("Crop", new LayoutCropping(null)).setGeometry(new WidgetGeometry(50.0, 50.0, 90.0, 100.0,
        // CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));

        /*
         * switch(align){ case CENTER: //this.getWidget("Crop").addWidget("Text", new LabelFixedFont(null, text))
         * this.addWidget("Text", new LabelScalable(null, text)) .setGeometry(new WidgetGeometry(50.0, 50.0, 100.0,
         * 100.0, CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));
         * ((LabelScalable)this.getWidget("Text")).setScale(this.fontSize); break; case LEFT:
         * //this.getWidget("Crop").addWidget("Text", new LabelFixedFont(null, text)) this.addWidget("Text", new
         * LabelScalable(null, text)) .setGeometry(new WidgetGeometry(5.0, 50.0, 95.0, 100.0, CType.RELXY, CType.RELXY,
         * WAlign.LEFT, WAlign.CENTER)); ((LabelScalable)this.getWidget("Text")).setScale(this.fontSize); break; case
         * RIGHT: //this.getWidget("Crop").addWidget("Text", new LabelFixedFont(null, text)) this.addWidget("Text", new
         * LabelScalable(null, text)) .setGeometry(new WidgetGeometry(5.0, 50.0, 95.0, 100.0, CType.RELXY, CType.RELXY,
         * WAlign.RIGHT, WAlign.CENTER)); ((LabelScalable)this.getWidget("Text")).setScale(this.fontSize); break;
         * default: throw new UIException(String.format("Unexpected align value : %s", align)); }
         */

        // LabelTTF label = (LabelTTF)this.addWidget("Text", new LabelTTF(null, text, ProxyClient.fontMC16));
        LabelTTF label = (LabelTTF) this.addWidget("Text", new LabelTTF(null, text, ProxyClient.fontMC8));
        switch (align) {
            case CENTER:
                label.setGeometry(
                        new WidgetGeometry(
                                50.0,
                                50.0,
                                100.0,
                                100.0,
                                CType.RELXY,
                                CType.RELXY,
                                WAlign.CENTER,
                                WAlign.CENTER));
                label.setScale(this.fontSize);
                break;
            case LEFT:
                label.setGeometry(
                        new WidgetGeometry(
                                5.0,
                                50.0,
                                95.0,
                                100.0,
                                CType.RELXY,
                                CType.RELXY,
                                WAlign.LEFT,
                                WAlign.CENTER));
                label.setScale(this.fontSize);
                break;
            case RIGHT:
                label.setGeometry(
                        new WidgetGeometry(
                                5.0,
                                50.0,
                                95.0,
                                100.0,
                                CType.RELXY,
                                CType.RELXY,
                                WAlign.RIGHT,
                                WAlign.CENTER));
                label.setScale(this.fontSize);
                break;
            default:
                throw new UIException(String.format("Unexpected align value : %s", align));
        }
    }

    @Override
    public void draw(Point pos) {}

    public void setFontSize(float size) {
        this.fontSize = size;
    }
}
