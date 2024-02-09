package bp;

import bp.core.AppFramework;
import bp.core.Gui;
import bp.gui.SwingGui;

public class Core {

    public static void main(String[] args) {
        Gui gui = new SwingGui();

        AppFramework app = AppFramework.getAppFramework();
        app.init(gui);
        app.run();
    }

}
