package uk.co.itstherules.marklog.editor.markdown;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public final class TabbedMarkdownEditors extends JTabbedPane {

    private final String projectRoot;

    public TabbedMarkdownEditors(File projectRoot) {
        this.projectRoot = projectRoot.getAbsolutePath();
        setPreferredSize(new Dimension(1024, 720));
    }


    public void removeMarkdownTabFor(File file) {
        if(tabExists(file)) {
            final int index = indexOfTab(identifier(file));
            removeTabAt(index);
        }
    }

    public void addMarkdownEditorFor(File file){
        if(tabExists(file)) {
            focusOn(file);
            final int index = indexOfTab(identifier(file));
            setSelectedIndex(index);
        } else {
            final String path = identifier(file);
            addTab(path, new MarkdownAndHtmlPanel(file));

            JPanel panelForTab = new JPanel(new MigLayout("","[center][right]","[center][center]"));

            panelForTab.setOpaque(false);
            JLabel tabTitleLabel = new JLabel(path);
            JButton closeButton = new JButton("x");
            closeButton.setSize(22, 22);
            closeButton.setPreferredSize(new Dimension(22, 22));

            panelForTab.add(tabTitleLabel);
            panelForTab.add(closeButton, "gapleft 10");

            int index = indexOfTab(path);
            setTabComponentAt(index, panelForTab);
            setSelectedIndex(index);

            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    Component selected = getSelectedComponent();
                    if (selected != null) {
                        remove(selected);
                    }
                }
            });
        }
    }

    private String identifier(File file) {
        return file.getAbsolutePath().substring(projectRoot.length()+1);
    }

    private boolean tabExists(File file) {
        return indexOfTab(identifier(file)) > -1;
    }

    public void focusOn(File file) {
        final int index = indexOfTab(identifier(file));
        getTabComponentAt(index).transferFocus();
    }

    public void reloadTabIfOpen(File file) {
        if(tabExists(file) && file.exists()) {
            //TODO implement me
        }
    }
}
