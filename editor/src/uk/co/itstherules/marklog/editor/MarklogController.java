package uk.co.itstherules.marklog.editor;

import org.apache.commons.io.FileUtils;
import uk.co.itstherules.marklog.editor.dialogs.EditProjectDialog;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.editor.model.PostService;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static uk.co.itstherules.marklog.editor.dialogs.SyncDialog.syncDialog;

public class MarklogController {

    private final JPanel blankPanel;
    private final MarklogApp app;
    private final JPanel panel;
    private MarklogProjectEditor projectEditor;

    public MarklogController(JPanel panel, JPanel blankPanel, MarklogApp app) {
        this.panel = panel;
        this.blankPanel = blankPanel;
        this.app = app;
    }

    void removeMarklogProject() {
        if (projectEditor != null) {
            panel.remove(projectEditor);
            addBlankPanel();
            app.pack();
        }
    }

    public void newMarklogProject(ProjectConfiguration configuration) {
        removeMarklogProject();
        removeBlankPanel();
        projectEditor = new MarklogProjectEditor(app, configuration, this);
        panel.add(projectEditor, BorderLayout.CENTER);
        app.pack();
    }

    public void newMarkdownTabFor(File file) {
        projectEditor.addMarkdownEditorFor(file);
        app.pack();
    }

    void addBlankPanel() { panel.add(blankPanel, BorderLayout.CENTER); }

    void removeBlankPanel() { panel.remove(blankPanel); }

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
        if (moveChildrenUp) {
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
        Post post = new Post(file, postName);
        post.save();
        newMarkdownTabFor(post.getFile());
    }


    public boolean renameFile(File oldFile, String newFileName) {
        final File newFile = new File(oldFile.getParent(), newFileName);
        return oldFile.renameTo(newFile);
    }

    public void openFile(File file) {
        if (file.getName().endsWith(".md")) {
            newMarkdownTabFor(file);
        }
    }

    public void reloadTabIfOpen(File file) {
        projectEditor.reloadTabIfOpen(file);
    }

    public void openSyncDialog(ProjectConfiguration configuration, PostService service) {
        syncDialog(app, configuration, service).ok();
    }

    public void editProject(File file) {
        final ProjectConfiguration configuration = new ProjectConfiguration();
        configuration.load(file);
        new EditProjectDialog(app, this, configuration);
    }
}
