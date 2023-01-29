package mcp.mobius.opis.api;

import mcp.mobius.opis.swing.SelectedTab;

public interface ITabPanel {

    SelectedTab getSelectedTab();

    boolean refreshOnString(); // Should we refresh on a cache update ?

    boolean refresh(); // Do the actual refresh
}
