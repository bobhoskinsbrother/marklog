package uk.co.itstherules.marklog.editor;

import javax.swing.*;
import java.awt.*;

public final class MarklogApp extends JFrame {

    private MarklogPanel marklogPanel;

    public MarklogApp() {
        super("Marklog Editor");
        setLookAndFeel();
        setLayout(new GridBagLayout());
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = ((int) screenSize.getWidth());
        int height = ((int) screenSize.getHeight());
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setLocationRelativeTo(null);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        marklogPanel = new MarklogPanel(this);
        add(marklogPanel, fillTheSpaceConstraints());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon image = IconLoader.fromResource("/marklog_logo.png");
        setIconImage(image.getImage());
        pack();
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
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
    }

}
