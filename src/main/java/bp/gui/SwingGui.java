package main.java.bp.gui;

import main.java.bp.core.Gui;
import main.java.bp.gui.view.MainFrame;

public class SwingGui implements Gui {

    @Override
    public void start() {
        MainFrame.getInstance().setVisible(true);
    }
}
