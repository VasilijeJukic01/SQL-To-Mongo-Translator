package bp.gui.view;

import bp.core.AppFramework;
import bp.gui.controller.RunController;
import bp.gui.controller.TextPaneController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static bp.constants.Constants.*;

@SuppressWarnings("FieldCanBeLocal")
public class MainFrame extends JFrame {

    private static MainFrame instance;

    private JSplitPane splitPane;
    private JTextPane textPane;
    private JTable dataTable;
    private JButton btnRun;

    private JScrollPane textAreaScrollPane;
    private JScrollPane tableScrollPane;

    private TextPaneController textPaneController;

    private ErrorWindow errorWindow;

    private MainFrame() {}

    private void init() {
        initWindow();
        initComponents();
        initDataTable();
        packComponents();
    }

    private void initWindow() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setSize(SCREEN_WID, SCREEN_HEI);
        Image icon = tk.getImage(getClass().getResource("/images/icon.png"));
        this.setIconImage(icon);
        this.setTitle("SQL to Mongo Translator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.textPaneController = new TextPaneController();
    }

    private void initComponents() {
        this.btnRun = new JButton("Run");
        Dimension buttonSize = new Dimension(80, 30);
        this.btnRun.setPreferredSize(buttonSize);
        this.btnRun.setAction(new RunController());

        this.textPane = new JTextPane(textPaneController.generateStyle());
        textPaneController.setTextPane(textPane);
        textPane.setBackground(TEXT_PANE_COLOR);
        textPane.setForeground(Color.WHITE);
        Font font = new Font("Arial", Font.PLAIN, FONT_SIZE);
        textPane.setFont(font);
        textPane.setCaretColor(Color.WHITE);
        this.textAreaScrollPane = new JScrollPane(textPane);
        textAreaScrollPane.setPreferredSize(new Dimension(SCREEN_WID, SCREEN_HEI/2));
    }

    private void initDataTable() {
        this.dataTable = new JTable();
        this.dataTable.setModel(AppFramework.getAppFramework().getTableModel());

        dataTable.setBackground(DATA_TABLE_BG);
        dataTable.setForeground(DATA_TABLE_FG);
        dataTable.getTableHeader().setForeground(DATA_TABLE_H);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(HEADER_BG);
        headerRenderer.setForeground(HEADER_FG);
        dataTable.getTableHeader().setDefaultRenderer(headerRenderer);

        this.tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setPreferredSize(new Dimension(400, SCREEN_HEI/2));
    }

    private void packComponents() {
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BUTTON_PANEL_COLOR);
        buttonPanel.add(btnRun);
        upperPanel.add(textAreaScrollPane, BorderLayout.CENTER);
        upperPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(upperPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, mainPanel);
        this.getContentPane().setBackground(BUTTON_PANEL_COLOR);
        this.add(splitPane);
        this.errorWindow = new ErrorWindow(this, true, AppFramework.getAppFramework().getValidator());
        pack();
    }

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
            instance.init();
        }
        return instance;
    }

    public JTextPane getTextPane() {
        return textPane;
    }
}
