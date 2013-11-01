package uk.co.itstherules.marklog.editor.actionbuilder;

import uk.co.itstherules.marklog.editor.filesystem.tree.file.FileModel;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TreeActionBuilder {

    private final JTree tree;

    public TreeActionBuilder(JTree tree) {
        this.tree = tree;
    }

    public TreeActionBuilder hasBeenDoubleClicked(final ApplyChanged applyChanged) {
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    FileModel node = FileModel.class.cast(path.getLastPathComponent());
                    if (e.getClickCount() == 2) {
                        applyChanged.apply(node);
                    }
                }
            }
        });
        return this;
    }

    public TreeActionBuilder hasBeenRightClicked(final ApplyChanged applyChanged) {
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        FileModel node = FileModel.class.cast(path.getLastPathComponent());
                        Rectangle pathBounds = tree.getUI().getPathBounds(tree, path);
                        if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
                            applyChanged.apply(node);
                        }
                    }
                }
            }
        });
        return this;
    }

    public interface ApplyChanged {
        void apply(FileModel node);
    }
}
