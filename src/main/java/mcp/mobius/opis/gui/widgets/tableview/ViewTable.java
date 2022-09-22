package mcp.mobius.opis.gui.widgets.tableview;

import mcp.mobius.opis.gui.helpers.UIException;
import mcp.mobius.opis.gui.interfaces.CType;
import mcp.mobius.opis.gui.interfaces.IWidget;
import mcp.mobius.opis.gui.interfaces.WAlign;
import mcp.mobius.opis.gui.widgets.LayoutBase;
import mcp.mobius.opis.gui.widgets.ViewportScrollable;
import mcp.mobius.opis.gui.widgets.WidgetBase;
import mcp.mobius.opis.gui.widgets.WidgetGeometry;
import org.lwjgl.util.Point;

public class ViewTable extends WidgetBase {

    int ncolumns = -1;
    int nrows = 0;
    double[] widths;
    String[] texts;
    WAlign[] aligns;
    float fontSize = 1.0f;
    boolean init = false;
    int rowColorOdd = 0x50505050;
    int rowColorEven = 0x50808080;

    public ViewTable(IWidget parent) {
        super(parent);
        this.addWidget("Titles", new TableRow(null, this.fontSize))
                .setGeometry(
                        new WidgetGeometry(0.0, 0.0, 100.0, 16.0, CType.REL_X, CType.REL_X, WAlign.LEFT, WAlign.TOP));
        ((TableRow) this.getWidget("Titles")).setColors(0x00000000, 0x00000000);
        this.addWidget("Viewport", new ViewportScrollable(null))
                .setGeometry(
                        new WidgetGeometry(0.0, 16.0, 100.0, 90.0, CType.REL_X, CType.RELXY, WAlign.LEFT, WAlign.TOP));
        ((ViewportScrollable) (this.getWidget("Viewport")))
                .attachWidget(new LayoutBase(null))
                .setGeometry(
                        new WidgetGeometry(0.0, 0.0, 100.0, 0.0, CType.RELXY, CType.REL_X, WAlign.LEFT, WAlign.TOP));
    }

    @Override
    public void draw(Point pos) {}

    public ViewTable setColumnsWidth(double... widths) {
        if (this.ncolumns == -1) this.ncolumns = widths.length;
        else if (this.ncolumns != widths.length) {
            throw new UIException(
                    String.format("Number of columns mismatch. Expecting %d, got %d", this.ncolumns, widths.length));
        }

        this.widths = widths;
        ((TableRow) this.getWidget("Titles")).setColumnsWidth(widths);
        return this;
    }

    public ViewTable setRowColors(int even, int odd) {
        this.rowColorEven = even;
        this.rowColorOdd = odd;
        return this;
    }

    public ViewTable setColumnsTitle(String... strings) {
        if (this.ncolumns == -1) this.ncolumns = strings.length;
        else if (this.ncolumns != strings.length) {
            throw new UIException(
                    String.format("Number of columns mismatch. Expecting %d, got %d", this.ncolumns, strings.length));
        }

        this.texts = strings;
        ((TableRow) this.getWidget("Titles")).setColumnsText(strings);

        return this;
    }

    public ViewTable setColumnsAlign(WAlign... aligns) {
        if (this.ncolumns == -1) this.ncolumns = aligns.length;
        else if (this.ncolumns != aligns.length) {
            throw new UIException(
                    String.format("Number of columns mismatch. Expecting %d, got %d", this.ncolumns, aligns.length));
        }

        this.aligns = aligns;
        ((TableRow) this.getWidget("Titles")).setColumnsAlign(aligns);

        return this;
    }

    public ViewTable addRow(String... strings) {
        this.addRow(null, strings);
        return this;
    }

    public ViewTable addRow(Object obj, String... strings) {
        IWidget tableLayout = ((ViewportScrollable) (this.getWidget("Viewport"))).getAttachedWidget();
        tableLayout.setSize(100.0, (this.nrows + 1) * 16);

        TableRow newRow = (TableRow) new TableRow(null, this.fontSize);
        newRow.setColumnsWidth(this.widths);
        newRow.setColumnsText(strings);
        newRow.setColumnsAlign(this.aligns);
        if (this.nrows % 2 == 1) newRow.setColors(this.rowColorOdd, this.rowColorOdd);
        else newRow.setColors(this.rowColorEven, this.rowColorEven);
        newRow.setGeometry(
                new WidgetGeometry(0.0, 16 * this.nrows, 100.0, 16, CType.REL_X, CType.REL_X, WAlign.LEFT, WAlign.TOP));
        newRow.attachObject(obj);

        tableLayout.addWidget(String.format("Row_%03d", this.nrows), newRow);

        this.nrows += 1;

        return this;
    }

    public TableRow getRow(double x, double y) {
        ViewportScrollable viewport = (ViewportScrollable) this.getWidget("Viewport");
        IWidget layout = viewport.getAttachedWidget();
        TableRow row = (TableRow) layout.getWidgetAtLayer(x, y - viewport.getOffset(), 1);

        return row;
    }

    public void setFontSize(float size) {
        this.fontSize = size;
    }
}
