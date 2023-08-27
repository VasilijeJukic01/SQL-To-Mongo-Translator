package main.java.bp.gui.view;

import main.java.bp.observer.Subscriber;
import main.java.bp.parser.Query;
import main.java.bp.parser.clauses.Clause;
import main.java.bp.validator.Validator;

import javax.swing.*;
import java.awt.*;

public class ErrorWindow extends JDialog implements Subscriber {

    private final JLabel label = new JLabel("");
    private final JButton btnClose = new JButton("Close");

    public ErrorWindow(Frame owner, boolean modal, Validator<Query<Clause>> validator) {
        super(owner, modal);
        validator.addSubscriber(this);
        initWindow();
        initComponents();
        initButtons();
    }

    private void initWindow() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        this.setSize((int) screenSize.getWidth() / 4, (int) screenSize.getHeight() / 4);
        this.setTitle("Error Dialog");
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);

        JPanel spacing = new JPanel();
        this.add(spacing, BorderLayout.NORTH);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.add(labelPanel, BorderLayout.CENTER);
        labelPanel.add(label);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(btnClose);
    }

    private void initButtons() {
        btnClose.setPreferredSize(new Dimension(80, 30));
        btnClose.addActionListener(e -> super.dispose());
    }

    @Override
    public <T> void update(T t) {
        label.setText((String)t);
        this.setVisible(true);
    }
}
