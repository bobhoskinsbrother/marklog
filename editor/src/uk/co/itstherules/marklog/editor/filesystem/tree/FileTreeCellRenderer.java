package uk.co.itstherules.marklog.editor.filesystem.tree;

import uk.co.itstherules.marklog.editor.IconLoader;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    private Map<String, Icon> iconCache = new HashMap<String, Icon>();
    private Map<File, String> rootNameCache = new HashMap<File, String>();
    private Icon defaultDirectory;
    private Icon defaultFile;

    FileTreeCellRenderer() {
        defaultDirectory = IconLoader.fromResource("/folder.png");
        defaultFile = IconLoader.fromResource("/file.png");

        iconCache.put("", defaultDirectory);
        iconCache.put("md", IconLoader.fromResource("/md_file.png"));
        iconCache.put("marklog", IconLoader.fromResource("/marklog_file.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        FileTreeNode fileTreeNode = FileTreeNode.class.cast(value);
        File file = fileTreeNode.getFile();
        String filename = "";
        if (file != null) {
            if (fileTreeNode.isRoot()) {
                filename = getRootFilename(file);
            } else {
                filename = file.getName();
            }
        }
        JLabel label = JLabel.class.cast(super.getTreeCellRendererComponent(tree, filename, sel, expanded, leaf, row, hasFocus));
        if (file != null) {
            Icon icon = getIconFor(file);
            label.setIcon(icon);
        }
        return label;
    }

    private Icon getIconFor(File file) {
        final String extension = getExtensionFor(file);
        final Icon icon = this.iconCache.get(extension);
        if(icon == null) { return defaultFile; }
        return icon;
    }

    private String getExtensionFor(File file) {
        final String fileName = file.getName();
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        int slashIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (dotIndex > slashIndex) {
            extension = fileName.substring(dotIndex+1);
        }
        return extension;
    }

    private String getRootFilename(File file) {
        String filename;
        filename = this.rootNameCache.get(file);
        if (filename == null) {
            filename = TreeBrowser.fileSystemView.getSystemDisplayName(file);
            this.rootNameCache.put(file, filename);
        }
        return filename;
    }
}
