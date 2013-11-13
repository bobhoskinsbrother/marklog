package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import uk.co.itstherules.marklog.editor.filesystem.Files;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.FileModel;
import uk.co.itstherules.marklog.editor.markdown.Markdown;
import uk.co.itstherules.marklog.string.Append;
import uk.co.itstherules.marklog.string.Chomp;
import uk.co.itstherules.marklog.string.CompositeStringManipulator;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;

import static uk.co.itstherules.marklog.editor.filesystem.Files.rootRelativePath;

public class TreeTransferHandler extends TransferHandler {

    private final File root;

    public TreeTransferHandler(File root) {
        this.root = root;
    }

    public boolean canImport(TransferHandler.TransferSupport info) {
        return false;
    }

    protected Transferable createTransferable(JComponent c) {
        return new StringSelection(exportString(c));
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    public boolean importData(TransferHandler.TransferSupport info) {
        return false;
    }

    protected void exportDone(JComponent c, Transferable data, int action) { }

    protected String exportString(JComponent c) {
        JTree tree = JTree.class.cast(c);
        FileModel fileModel = FileModel.class.cast(tree.getLastSelectedPathComponent());
        final String rootRelativePath = rootRelativePath(root, fileModel.getFile());
        final File file = new File(rootRelativePath);
        if (Files.isImage(file)) {
            return Markdown.image(rootRelativePath);
        }
        if(Files.isMarkdown(file)) {
            return Markdown.link(new CompositeStringManipulator(new Chomp(".md"), new Append(".html")).manipulate(rootRelativePath));
        }
        return Markdown.link(rootRelativePath);
    }
}