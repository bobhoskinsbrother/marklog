package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.TreeActionBuilder;
import uk.co.itstherules.marklog.editor.dialogs.RenameFileDialog;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.*;
import uk.co.itstherules.marklog.editor.model.PostService;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static uk.co.itstherules.marklog.editor.IconLoader.icon;
import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;
import static uk.co.itstherules.marklog.editor.viewbuilder.ButtonBuilder.button;

public class FileSystemTree extends JPanel {

    private final MarklogApp app;
    private final MarklogController controller;
    private final FileSystemModel fileSystemModel;
    private final File root;
    private final ProjectConfiguration configuration;
    private JTree tree;
    private PostService service;

    public FileSystemTree(MarklogApp app, MarklogController controller, ProjectConfiguration configuration, PostService service) {
        this.configuration = configuration;
        this.service = service;
        root = configuration.getDirectory();
        setName("fileSystemTree");
        this.app = app;
        this.controller = controller;
        fileSystemModel = new FileSystemModel(root);
        tree = makeTree(fileSystemModel);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(200, 400));
        setPreferredSize(new Dimension(200, 400));
        JPanel panel = makeScrollPaneWithTree(tree);
        add(CENTER, panel);
        setVisible(true);
        setupWorker(root);
    }

    private void setupWorker(File baseDirectory) {
        final FileWorker worker = new FileWorker(baseDirectory);
        worker.addPropertyChangeListener(new RefreshTreeOnPropertyChange());
        worker.execute();
    }

    private JTree makeTree(FileSystemModel model) {
        JTree tree = new JTree(model);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new TreeTransferHandler(root));
        FileSystemTreeCellRenderer renderer = new FileSystemTreeCellRenderer();
        tree.setCellRenderer(renderer);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellEditor(new FileTreeCellEditor(controller, renderer));
        when(tree).hasBeenDoubleClicked(openFile()).hasBeenRightClicked(showPopupMenu()).hasKeyBeenPressed("F2", renameSelectedFile());
        return tree;
    }

    private JPanel makeScrollPaneWithTree(JTree tree) {
        JPanel reply = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        final JButton syncButton = button(icon("/sync.png"), "Sync with Server").withClickAction(openSyncDialog()).ok();
        buttonPanel.add(syncButton);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tree);
        reply.add(CENTER, scrollPane);
        reply.add(SOUTH, buttonPanel);
        return reply;
    }

    private ButtonActionBuilder.ApplyChanged openSyncDialog() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                controller.openSyncDialog(configuration, service);
            }
        };
    }

    private TreeActionBuilder.ApplyChanged renameSelectedFile() {
        return new TreeActionBuilder.ApplyChanged() {
            @Override public void apply(FileModel node) {
                new RenameFileDialog(app,controller,node.getFile());
            }
        };
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