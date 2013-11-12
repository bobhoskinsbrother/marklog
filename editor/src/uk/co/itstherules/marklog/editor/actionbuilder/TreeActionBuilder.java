package uk.co.itstherules.marklog.editor.actionbuilder;

import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.FileModel;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TreeActionBuilder {

    private final JTree tree;

    public TreeActionBuilder(JTree tree) {
        this.tree = tree;
    }

    public TreeActionBuilder hasKeyBeenPressed(String key, final ApplyChanged applyChanged) {
        InputMap inputMap = tree.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        removeAllFromInputMap(inputMap, keyStroke);
        inputMap = tree.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(keyStroke, "RenameFile");
        ActionMap actionMap = tree.getActionMap();
        actionMap.put("RenameFile", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                Object lastSelected = tree.getLastSelectedPathComponent();
                if(lastSelected!=null && FileModel.class.isInstance(lastSelected)) {
                    final FileModel fileModel = FileModel.class.cast(lastSelected);
                    applyChanged.apply(fileModel);
                }
            }
        });
        return this;
    }

    private void removeAllFromInputMap(InputMap inputMap, KeyStroke keyStroke) {
        while (inputMap!= null) {
            inputMap.remove(keyStroke);
            inputMap = inputMap.getParent();
        }
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
