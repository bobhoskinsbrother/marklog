package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.filesystem.tree.file.FileSystemTree;
import uk.co.itstherules.marklog.editor.markdown.TabbedMarkdownEditors;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;

import javax.swing.*;
import java.io.File;

public final class MarklogProjectEditor extends JSplitPane {

    private final TabbedMarkdownEditors markdownEditors;
    private final FileSystemTree fileSystemTree;

    public MarklogProjectEditor(MarklogApp app, ProjectConfigurationModel configuration, MarklogController controller) {
        setName("MarklogProjectEditor");
        final File directory = configuration.getDirectory();
        markdownEditors = new TabbedMarkdownEditors(directory);
        fileSystemTree = new FileSystemTree(app, controller, directory);
        setLeftComponent(fileSystemTree);
        setRightComponent(markdownEditors);
        setResizeWeight(0.25);
    }

    public void addMarkdownEditorFor(File file) {
        markdownEditors.addMarkdownEditorFor(file);
    }

    public void removeMarkdownTabFor(File file) {
        markdownEditors.removeMarkdownTabFor(file);
    }

    public void reloadTabIfOpen(File file) {
        markdownEditors.reloadTabIfOpen(file);
    }
}