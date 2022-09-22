package mcp.mobius.opis.gui.widgets.tableview;

import mcp.mobius.opis.gui.helpers.UIException;
import mcp.mobius.opis.gui.interfaces.CType;
import mcp.mobius.opis.gui.interfaces.IWidget;
import mcp.mobius.opis.gui.interfaces.WAlign;
import mcp.mobius.opis.gui.widgets.LayoutBase;
import mcp.mobius.opis.gui.widgets.WidgetBase;
import mcp.mobius.opis.gui.widgets.WidgetGeometry;
import org.lwjgl.util.Point;

public class TableRow extends WidgetBase {
    int ncolumns = -1;
    double[] widths;
    String[] texts;
    WAlign[] aligns;
    float fontSize = 1.0f;
    boolean init = false;
    int bgcolor1 = 0xff505050;
    int bgcolor2 = 0xff505050;
    Object obj;

    public TableRow(IWidget parent, float fontSize) {
        super(parent);

        this.fontSize = fontSize;

        this.addWidget("Background", new LayoutBase(null))
                .setGeometry(new WidgetGeometry(
                        50.0, 50.0, 100.0, 100.0, CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));
        ((LayoutBase) this.getWidget("Background")).setBackgroundColors(this.bgcolor1, this.bgcolor2);
    }

    public TableRow attachObject(Object obj) {
        this.obj = obj;
        return this;
    }

    public Object getObject() {
        return this.obj;
    }

    public void setFontSize(float size) {
        this.fontSize = size;
    }

    public TableRow setColumnsWidth(double... widths) {
        if (this.ncolumns == -1) this.ncolumns = widths.length;
        else if (this.ncolumns != widths.length) {
            throw new UIException(
                    String.format("Number of columns mismatch. Expecting %d, got %d", this.ncolumns, widths.length));
        }

        this.widths = widths;

        return this;
    }

    public TableRow setColumnsText(String... strings) {
        if (this.ncolumns == -1) this.ncolumns = strings.length;
        else if (this.ncolumns != strings.length) {
            throw new UIException(
                    String.format("Number of columns mismatch. Expecting %d, got %d", this.ncolumns, strings.length));
        }

        this.texts = strings;

        return this;
    }

    public TableRow setColumnsAlign(WAlign... aligns) {
        if (this.ncolumns == -1) this.ncolumns = aligns.length;
        else if (this.ncolumns != aligns.length) {
            throw new UIException(
                    String.format("Number of columns mismatch. Expecting %d, got %d", this.ncolumns, aligns.length));
        }

        this.aligns = aligns;

        return this;
    }

    public TableRow setColors(int bg1, int bg2) {
        this.bgcolor1 = bg1;
        this.bgcolor2 = bg2;
        ((LayoutBase) this.getWidget("Background")).setBackgroundColors(this.bgcolor1, this.bgcolor2);
        return this;
    }

    @Override
    public void draw() {
        if (!init) {
            double currentOffset = 0.0;
            for (int i = 0; i < this.ncolumns; i++) {
                if (!this.widgets.containsKey(String.format("Cell_%02d", i))) {
                    TableCell cell = (TableCell) (this.addWidget(
                            String.format("Cell_%02d", i),
                            new TableCell(null, this.texts[i], this.aligns[i], this.fontSize)));
                    cell.setGeometry(new WidgetGeometry(
                            currentOffset,
                            50.0,
                            this.widths[i],
                            100.0,
                            CType.RELXY,
                            CType.RELXY,
                            WAlign.LEFT,
                            WAlign.CENTER));
                    currentOffset += this.widths[i];
                }
            }
            init = true;
        }
        super.draw();
    }

    @Override
    public void draw(Point pos) {}
}
