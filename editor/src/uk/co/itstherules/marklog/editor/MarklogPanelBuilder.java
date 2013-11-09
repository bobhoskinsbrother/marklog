package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.actionbuilder.MenuItemActionBuilder;
import uk.co.itstherules.marklog.editor.dialogs.NewProjectDialog;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;
import uk.co.itstherules.marklog.editor.viewbuilder.Builder;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.viewbuilder.MenuBarBuilder.menu;
import static uk.co.itstherules.marklog.editor.viewbuilder.MenuBarBuilder.menuBar;
import static uk.co.itstherules.marklog.editor.viewbuilder.MenuBuilder.item;

public final class MarklogPanelBuilder implements Builder<JPanel> {

    private final MarklogApp app;
    private final MarklogController controller;
    private final JPanel panel;

    public MarklogPanelBuilder(MarklogApp app) {
        this.app = app;
        panel = makePanel();
        addMenuTo(panel);
        JPanel blankPanel = new BlankPanelBuilder().build();
        controller = new MarklogController(build(), blankPanel, app);
        controller.addBlankPanel();
    }

    private JPanel makePanel() {
        JPanel panel = new JPanel();
        panel.setName("marklogPanel");
        panel.setLayout(new BorderLayout());
        return panel;
    }

    @Override public JPanel build() {
        return panel;
    }

    private void addMenuTo(JPanel panel) {
        panel.add(createMenu(), BorderLayout.NORTH);
    }

    private JMenuBar createMenu() {
        return menuBar("menuBar").add(
                    menu("File")
                        .add(item("New Project...").withClickAction(openNewProjectDialog()).build())
                        .add(item("Open Project...").withClickAction(openOpenProjectDialog()).build())
                        .add(item("Close Project").withClickAction(closeCurrentProject()).build())
                    .build())
                .build();
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
                    final ProjectConfigurationModel configuration = new ProjectConfigurationModel(file);
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