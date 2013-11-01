package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.filesystem.tree.file.FileSystemTree;
import uk.co.itstherules.marklog.editor.markdown.TabbedMarkdownEditors;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;

import javax.swing.*;
import java.io.File;

public final class MarklogProjectEditor extends JSplitPane {

    private final TabbedMarkdownEditors markdownEditors;
    private final FileSystemTree fileSystemTree;

    public MarklogProjectEditor(MarklogApp app, ProjectConfigurationModel projectConfiguration, MarklogPanel.MarklogController marklogController) {
        setName("MarklogProjectEditor");
        final File directory = projectConfiguration.getDirectory();
        markdownEditors = new TabbedMarkdownEditors(directory);
        final OpenMarkdownEditorOnFileSelectionController fileSelectionController = new OpenMarkdownEditorOnFileSelectionController(markdownEditors);
        fileSystemTree = new FileSystemTree(app, fileSelectionController, marklogController, directory);
        setLeftComponent(fileSystemTree);
        setRightComponent(markdownEditors);
        setResizeWeight(0.25);
    }

    public void addEditorFor(File file) {
        markdownEditors.addEditorFor(file);
    }

}