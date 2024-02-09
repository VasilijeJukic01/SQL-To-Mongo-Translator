package bp.gui;

import bp.core.Gui;
import bp.gui.view.MainFrame;

public class SwingGui implements Gui {

    @Override
    public void start() {
        MainFrame.getInstance().setVisible(true);
    }
}
