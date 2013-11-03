package uk.co.itstherules.marklog.editor;

import org.apache.commons.io.FileUtils;
import uk.co.itstherules.marklog.editor.actionbuilder.MenuItemActionBuilder;
import uk.co.itstherules.marklog.editor.dialogs.NewProjectDialog;
import uk.co.itstherules.marklog.editor.model.PostModel;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class MarklogPanel extends JPanel {

    private final MarklogApp app;
    private MarklogProjectEditor projectEditor;
    private MarklogController controller;

    public MarklogPanel(MarklogApp app) {
        setName("marklogPanel");
        this.app = app;
        this.controller = new MarklogController();
        setLayout(new BorderLayout());
        addMenu();
    }

    public MarklogPanel(MarklogApp app, ProjectConfigurationModel configuration) {
        this(app);
        controller.newMarklogProject(configuration);
    }

    private void addMenu() {
        add(menu(), BorderLayout.NORTH);
    }

    private JMenuBar menu() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setName("menuBar");
        final JMenu menu = new JMenu("File");
        final JMenuItem newProjectMenuItem = new JMenuItem("New Project...");
        final JMenuItem openProjectMenuItem = new JMenuItem("Open Project...");
        final JMenuItem closeProjectMenuItem = new JMenuItem("Close Project");
        when(newProjectMenuItem).hasBeenClicked(openNewProjectDialog());
        when(openProjectMenuItem).hasBeenClicked(openOpenProjectDialog());
        when(closeProjectMenuItem).hasBeenClicked(closeCurrentProject());
        menu.add(newProjectMenuItem);
        menu.add(openProjectMenuItem);
        menu.add(closeProjectMenuItem);
        menuBar.add(menu);
        return menuBar;
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

    public MarklogController getController() {
        return controller;
    }

    public class MarklogController {

        private void removeMarklogProject() {
            if (projectEditor != null) {
                remove(projectEditor);
                app.pack();
            }
        }

        public void newMarklogProject(ProjectConfigurationModel configuration) {
            removeMarklogProject();
            projectEditor = new MarklogProjectEditor(app, configuration, controller);
            add(projectEditor, BorderLayout.CENTER);
            app.pack();
        }

        public void newMarkdownTabFor(File file) {
            projectEditor.addMarkdownEditorFor(file);
            app.pack();
        }

        public boolean deleteFile(File file) {
            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }
            return file.delete();
        }

        public boolean deleteDirectory(File directory, boolean moveChildrenUp) {
            if(moveChildrenUp) {
                if (directory.isDirectory()) {
                    File parentFile = directory.getParentFile();
                    final File[] files = directory.listFiles();
                    for (File file : files) {
                        try {
                            FileUtils.moveToDirectory(file, parentFile, false);
                        } catch (IOException e) {
                            return false;
                        }
                    }
                }
                return directory.delete();
            } else {
                return deleteFile(directory);
            }
        }

        public void removeMarkdownTabFor(File file) {
            projectEditor.removeMarkdownTabFor(file);
            app.pack();
        }

        public void addNewPost(File file, String postName) {
            PostModel post = new PostModel(file, postName);
            post.save();
            newMarkdownTabFor(post.getFile());
        }

        public void openFile(File file) {
            if (file.getName().endsWith(".md")) {
                newMarkdownTabFor(file);
            }

        }
    }
}