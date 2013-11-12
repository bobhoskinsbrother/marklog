package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.actionbuilder.MenuItemActionBuilder;
import uk.co.itstherules.marklog.editor.dialogs.NewProjectDialog;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.editor.viewbuilder.Builder;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.BlankPanelBuilder.blankPanel;
import static uk.co.itstherules.marklog.editor.viewbuilder.MenuBuilder.menu;
import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;
import static uk.co.itstherules.marklog.editor.viewbuilder.MenuItemBuilder.item;
import static uk.co.itstherules.marklog.editor.viewbuilder.MenuBarBuilder.menuBar;

public final class MarklogPanelBuilder implements Builder<JPanel> {

    private final MarklogApp app;
    private final MarklogController controller;
    private final JPanel panel;

    public MarklogPanelBuilder(MarklogApp app) {
        this.app = app;
        panel = panel("marklogPanel").borderLayout().ok();
        addMenuTo(panel);
        controller = new MarklogController(panel, blankPanel().ok(), app);
        controller.addBlankPanel();
    }

    @Override public JPanel ok() {
        return panel;
    }

    private void addMenuTo(JPanel panel) {
        panel.add(createMenu(), BorderLayout.NORTH);
    }

    private JMenuBar createMenu() {
        return menuBar("menuBar").add(
                    menu("File")
                        .add(item("New Project...").withClickAction(openNewProjectDialog()).ok())
                        .add(item("Open Project...").withClickAction(openOpenProjectDialog()).ok())
                        .add(item("Close Project").withClickAction(closeCurrentProject()).ok())
                    .ok())
                .ok();
    }

    private MenuItemActionBuilder.ApplyChanged closeCurrentProject() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override
            public void apply() {
                controller.removeMarklogProject();
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openOpenProjectDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override
            public void apply() {
                final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                fileChooser.setSize(500,400);
                fileChooser.setPreferredSize(new Dimension(500,400));
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getName().endsWith(".marklog");
                    }

                    @Override
                    public String getDescription() {
                        return "Marklog Project";
                    }
                });
                int returnVal = fileChooser.showOpenDialog(app);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    final ProjectConfiguration configuration = new ProjectConfiguration(file);
                    controller.newMarklogProject(configuration);
                }
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openNewProjectDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override
            public void apply() {
                new NewProjectDialog(app, controller);
            }
        };
    }

}