package uk.co.itstherules.marklog.editor.markdown;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public final class TabbedMarkdownEditors extends JTabbedPane {

    private final String projectRoot;

    public TabbedMarkdownEditors(String projectRoot) {
        this.projectRoot = projectRoot;
        setPreferredSize(new Dimension(1024, 720));
    }


    public void addEditorFor(File file){
        final String path = identifier(file);
        addTab(path, new MarkdownEditorPanel(file));
        JPanel panelForTab = new JPanel(new GridBagLayout());
        panelForTab.setOpaque(false);
        JLabel tabTitleLabel = new JLabel(path);
        JButton closeButton = new JButton("x");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;

        panelForTab.add(tabTitleLabel, constraints);

        constraints.gridx++;
        constraints.weightx = 0;
        panelForTab.add(closeButton, constraints);

        int index = indexOfTab(path);
        setTabComponentAt(index, panelForTab);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Component selected = getSelectedComponent();
                if (selected != null) {
                    remove(selected);
                }
            }
        });
    }

    private String identifier(File file) { return file.getAbsolutePath().substring(projectRoot.length()); }

    public boolean exists(File file) {
        return indexOfTab(identifier(file)) > -1;
    }

    public void focusOn(File file) {
        final int index = indexOfTab(identifier(file));
        getTabComponentAt(index).requestFocus();
    }

}
