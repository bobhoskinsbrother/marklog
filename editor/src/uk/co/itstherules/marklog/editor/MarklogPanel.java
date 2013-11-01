package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.actionbuilder.MenuItemActionBuilder;
import uk.co.itstherules.marklog.editor.model.PostModel;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class MarklogPanel extends JPanel {

    private final MarklogApp app;
    private MarklogProjectEditor projectEditor;
    private MarklogController marklogController;

    public MarklogPanel(MarklogApp app) {
        setName("marklogPanel");
        this.app = app;
        this.marklogController = new MarklogController();
        setLayout(new BorderLayout());
        addMenu();
    }

    public MarklogPanel(MarklogApp app, ProjectConfigurationModel configuration) {
        this(app);
        marklogController.newMarklogEditor(configuration);
    }

    private void addEditorFor(File file) {
        projectEditor.addEditorFor(file);
        app.pack();
    }

    private void newEditorFor(ProjectConfigurationModel projectConfiguration) {
        removeProjectEditor();
        projectEditor = new MarklogProjectEditor(app, projectConfiguration, marklogController);
        add(projectEditor, BorderLayout.CENTER);
        app.pack();
    }

    private void removeProjectEditor() {
        if (projectEditor != null) {
            remove(projectEditor);
            app.pack();
        }
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
                removeProjectEditor();
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openOpenProjectDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override
            public void apply() {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getName().endsWith(".marklog");
                    }

                    @Override
                    public String getDescription() {
                        return "Marklog Blog Project";
                    }
                });
                int returnVal = fileChooser.showOpenDialog(app);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    final ProjectConfigurationModel configuration = new ProjectConfigurationModel(file);
                    newEditorFor(configuration);
                }
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openNewProjectDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override
            public void apply() {
                new NewProjectDialog(app, marklogController);
            }
        };
    }

    public MarklogController getController() {
        return marklogController;
    }

    public class MarklogController {

        public void newMarklogEditor(ProjectConfigurationModel configuration) {
            newEditorFor(configuration);
        }

        public void newMarkdownTabFor(PostModel post) {
            addEditorFor(post.getFile());
        }

        public boolean delete(File file) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = delete(new File(file, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return file.delete();
        }

        public void addNewPost(File file, String postName) {
            PostModel post = new PostModel(file, postName);
            post.save();
            newMarkdownTabFor(post);
        }
    }
}