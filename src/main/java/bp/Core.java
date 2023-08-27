package main.java.bp;

import main.java.bp.core.AppFramework;
import main.java.bp.core.Gui;
import main.java.bp.gui.SwingGui;

public class Core {

    public static void main(String[] args) {
        Gui gui = new SwingGui();

        AppFramework app = AppFramework.getAppFramework();
        app.init(gui);
        app.run();
    }

}
