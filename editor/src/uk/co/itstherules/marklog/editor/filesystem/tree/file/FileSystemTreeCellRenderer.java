package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.FileModel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static uk.co.itstherules.marklog.editor.IconLoader.icon;

class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer {

    private Map<String, Icon> iconCache = new HashMap<String, Icon>();
    private Icon defaultFile;

    FileSystemTreeCellRenderer() {
        defaultFile = icon("/file.png");
        iconCache.put("", icon("/folder.png"));
        iconCache.put("md", icon("/md_file.png"));
        iconCache.put("marklog", icon("/marklog_file.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        FileModel fileModel = FileModel.class.cast(value);
        File file = fileModel.getFile();
        String filename = file.getName();
        JLabel label = JLabel.class.cast(super.getTreeCellRendererComponent(tree, filename, sel, expanded, leaf, row, hasFocus));
        if(fileModel.isRoot()) {
            label.setIcon(rootIcon());
        } else {
            Icon icon = iconFor(file);
            label.setIcon(icon);
        }
        return label;
    }



    private Icon rootIcon() {
        return icon("/root_folder.png");
    }

    private Icon iconFor(File file) {
        final String extension = getExtensionFor(file);
        final Icon icon = this.iconCache.get(extension);
        if (icon == null) {
            return defaultFile;
        }
        return icon;
    }

    private String getExtensionFor(File file) {
        final String fileName = file.getName();
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        int slashIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (dotIndex > slashIndex) {
            extension = fileName.substring(dotIndex + 1);
        }
        return extension;
    }
}
