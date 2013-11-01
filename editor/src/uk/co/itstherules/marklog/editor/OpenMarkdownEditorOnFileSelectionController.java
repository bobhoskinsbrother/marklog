package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.markdown.TabbedMarkdownEditors;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.FileSelectionController;

import java.io.File;

public final class OpenMarkdownEditorOnFileSelectionController implements FileSelectionController {

    private final TabbedMarkdownEditors tabbedMarkdownEditors;

    public OpenMarkdownEditorOnFileSelectionController(TabbedMarkdownEditors tabbedMarkdownEditors) {
        this.tabbedMarkdownEditors = tabbedMarkdownEditors;
    }

    @Override public void onFile(File file) {
        if(file.getName().endsWith(".md")) {
            if(tabbedMarkdownEditors.exists(file)) {
                tabbedMarkdownEditors.focusOn(file);
            } else {
                tabbedMarkdownEditors.addEditorFor(file);
            }
        }
    }
}
