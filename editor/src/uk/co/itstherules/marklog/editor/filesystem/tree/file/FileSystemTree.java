package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogPanel;
import uk.co.itstherules.marklog.editor.actionbuilder.TreeActionBuilder;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public class FileSystemTree extends JPanel {

    private final JScrollPane scrollPane;
    private JTree tree;
    private final MarklogApp app;
    private final FileSelectionController fileSelectionController;
    private final MarklogPanel.MarklogController marklogController;

    public FileSystemTree(MarklogApp app, final FileSelectionController fileSelectionController, MarklogPanel.MarklogController marklogController, final File baseDirectory) {
        this.app = app;
        this.fileSelectionController = fileSelectionController;
        this.marklogController = marklogController;

        tree = makeTree(baseDirectory);
        scrollPane = makeScrollPaneWithTree(tree);


        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(200, 400));
        setPreferredSize(new Dimension(200, 400));
        add(BorderLayout.CENTER, scrollPane);
        setVisible(true);

        try {
            final FileWorker worker = new FileWorker(baseDirectory);
            worker.addPropertyChangeListener(new RefreshTreeOnPropertyChange(baseDirectory));
            worker.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JTree makeTree(File baseDirectory) {
        JTree tree = new JTree(new FileSystemModel(baseDirectory));
        tree.setCellRenderer(new FileSystemTreeCellRenderer());
        when(tree).hasBeenDoubleClicked(openFile()).hasBeenRightClicked(showPopupMenu());
        return tree;
    }

    private JScrollPane makeScrollPaneWithTree(JTree tree) {
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);
        return scrollpane;
    }

    private TreeActionBuilder.ApplyChanged openFile() {
        return new TreeActionBuilder.ApplyChanged() {
            @Override public void apply(FileModel node) {
                if (node == null)
                    return;
                if (node.isFile()) {
                    fileSelectionController.onFile(node.getFile());
                }
            }
        };
    }

    private TreeActionBuilder.ApplyChanged showPopupMenu() {
        return new TreeActionBuilder.ApplyChanged() {
            @Override public void apply(FileModel node) {
                final Point location = MouseInfo.getPointerInfo().getLocation();
                final Point locationOnScreen = tree.getLocationOnScreen();
                final int x = location.x - locationOnScreen.x;
                final int y = location.y - locationOnScreen.y;
                new RightClickTreeMenu(app, marklogController, tree, node, x, y);
            }
        };
    }

    private class RefreshTreeOnPropertyChange implements PropertyChangeListener {

        private final File baseDirectory;

        public RefreshTreeOnPropertyChange(File baseDirectory) {
            this.baseDirectory = baseDirectory;
        }

        @Override public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            scrollPane.remove(tree);
            tree = makeTree(baseDirectory);
            scrollPane.add(tree);
            FileSystemTree.this.repaint();
        }
    }
}