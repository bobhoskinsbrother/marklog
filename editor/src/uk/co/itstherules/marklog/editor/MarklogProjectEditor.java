package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.filesystem.tree.file.FileSystemTree;
import uk.co.itstherules.marklog.editor.markdown.TabbedMarkdownEditors;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;

import javax.swing.*;
import java.io.File;

public final class MarklogProjectEditor extends JSplitPane {

    private final TabbedMarkdownEditors markdownEditors;

    public MarklogProjectEditor(MarklogApp app, ProjectConfiguration configuration, MarklogController controller) {
        setName("MarklogProjectEditor");
        File directory = configuration.getDirectory();
        markdownEditors = new TabbedMarkdownEditors(directory);
        FileSystemTree fileSystemTree = new FileSystemTree(app, controller, configuration);
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