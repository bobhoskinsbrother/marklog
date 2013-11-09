package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import uk.co.itstherules.marklog.editor.IconLoader;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.TreeActionBuilder;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.DefaultFileModel;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.FileModel;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.FileSystemModel;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.FileWorker;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public class FileSystemTree extends JPanel {

    private final MarklogApp app;
    private final MarklogController controller;
    private final FileSystemModel fileSystemModel;
    private final File root;
    private JTree tree;

    public FileSystemTree(MarklogApp app, MarklogController controller, final File baseDirectory) {
        root = baseDirectory;
        setName("fileSystemTree");
        this.app = app;
        this.controller = controller;
        fileSystemModel = new FileSystemModel(baseDirectory);
        tree = makeTree(fileSystemModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(200, 400));
        setPreferredSize(new Dimension(200, 400));
        JPanel panel = makeScrollPaneWithTree(tree);
        add(BorderLayout.CENTER, panel);
        setVisible(true);
        setupWorker(baseDirectory);
    }

    private void setupWorker(File baseDirectory) {
        final FileWorker worker = new FileWorker(baseDirectory);
        worker.addPropertyChangeListener(new RefreshTreeOnPropertyChange());
        worker.execute();
    }

    private JTree makeTree(FileSystemModel model) {
        JTree tree = new JTree(model);
        tree.setCellRenderer(new FileSystemTreeCellRenderer());
        when(tree).hasBeenDoubleClicked(openFile()).hasBeenRightClicked(showPopupMenu());
        return tree;
    }

    private JPanel makeScrollPaneWithTree(JTree tree) {
        JPanel reply = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        final JButton syncButton = new JButton(IconLoader.fromResource("/sync.png"));
        syncButton.setToolTipText("Sync with Server");
        buttonPanel.add(syncButton);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tree);
        reply.add(BorderLayout.CENTER, scrollPane);
        reply.add(BorderLayout.SOUTH, buttonPanel);
        return reply;
    }

    private TreeActionBuilder.ApplyChanged openFile() {
        return new TreeActionBuilder.ApplyChanged() {
            @Override public void apply(FileModel node) {
                if (node == null)
                    return;
                if (node.isLeaf()) {
                    controller.openFile(node.getFile());
                }
            }
        };
    }

    private void reloadTabIfOpen(File file) {
        controller.reloadTabIfOpen(file);
    }

    private void addNode(File file) {
        FileModel parent = fileSystemModel.findFileModelFor(file.getParentFile());
        FileModel child = new DefaultFileModel(file);
        fileSystemModel.insertNodeInto(parent, child, parent.getChildCount());
    }

    private void removeNode(File file) {
        FileModel child = fileSystemModel.findFileModelFor(file);
        if (child != null) {
            fileSystemModel.removeNodeFromParent(child);
            controller.removeMarkdownTabFor(child.getFile());
        }
    }

    private TreeActionBuilder.ApplyChanged showPopupMenu() {
        return new TreeActionBuilder.ApplyChanged() {
            @Override public void apply(FileModel node) {
                final Point location = MouseInfo.getPointerInfo().getLocation();
                final Point locationOnScreen = tree.getLocationOnScreen();
                final int x = location.x - locationOnScreen.x;
                final int y = location.y - locationOnScreen.y;
                new RightClickTreeMenu(app, controller, tree, node, x, y);
            }
        };
    }

    private class RefreshTreeOnPropertyChange implements PropertyChangeListener {

        @Override public void propertyChange(PropertyChangeEvent event) {
            final Object newValue = event.getNewValue();
            if (File.class.isInstance(newValue)) {
                File file = File.class.cast(newValue);
                final String propertyName = event.getPropertyName();
                if ("ENTRY_DELETE".equals(propertyName)) {
                    removeNode(file);
                }
                if ("ENTRY_CREATE".equals(propertyName)) {
                    addNode(file);
                }
                if ("ENTRY_MODIFY".equals(propertyName)) {
                    reloadTabIfOpen(file);
                }
            }
        }
    }
}