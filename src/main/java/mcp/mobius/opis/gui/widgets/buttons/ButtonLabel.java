package mcp.mobius.opis.gui.widgets.buttons;

import mcp.mobius.opis.gui.events.MouseEvent;
import mcp.mobius.opis.gui.interfaces.CType;
import mcp.mobius.opis.gui.interfaces.IWidget;
import mcp.mobius.opis.gui.interfaces.WAlign;
import mcp.mobius.opis.gui.widgets.LabelFixedFont;
import mcp.mobius.opis.gui.widgets.WidgetGeometry;

public class ButtonLabel extends ButtonBase {

    public ButtonLabel(IWidget parent, String text) {
        super(parent);

        this.addWidget("Label", new LabelFixedFont(this, text));
        this.getWidget("Label")
                .setGeometry(new WidgetGeometry(
                        50.0D, 50.0D, 100.0D, 20.0D, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        super.onMouseClick(event);
    }
}
