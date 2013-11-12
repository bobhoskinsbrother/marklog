package uk.co.itstherules.marklog.editor;

import javax.swing.*;
import java.awt.*;

public final class MarklogApp extends JFrame {

    public MarklogApp() {
        super("Marklog");
        setLookAndFeel();
        setLayout(new GridBagLayout());
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setPreferredSize(screenSize);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        JPanel marklogPanel = new MarklogPanelBuilder(this).ok();
        add(marklogPanel, fillTheSpaceConstraints());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon image = IconLoader.icon("/marklog_logo.png");
        setIconImage(image.getImage());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static GridBagConstraints fillTheSpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        return constraints;
    }

    public static void main(final String... args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MarklogApp();
            }
        });
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
    }

}
