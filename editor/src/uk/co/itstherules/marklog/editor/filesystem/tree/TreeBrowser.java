package uk.co.itstherules.marklog.editor.filesystem.tree;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogPanel;
import uk.co.itstherules.marklog.editor.actionbuilder.TreeActionBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public class TreeBrowser extends JPanel {

    final static FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private final MarklogApp app;
    private final FileSelectionController fileSelectionController;
    private final MarklogPanel.MarklogController marklogController;
    private JTree tree;

    public TreeBrowser(MarklogApp app, final FileSelectionController fileSelectionController, MarklogPanel.MarklogController marklogController, final String baseDirectory) {
        final File file = new File(baseDirectory);
        if (!file.isDirectory()) { throw new IllegalArgumentException("File supplied (" + baseDirectory + ") is not a directory"); }
        this.app = app;
        this.marklogController = marklogController;
        this.fileSelectionController = fileSelectionController;

        setLayout(new BorderLayout());
        tree = new JTree(new FileTreeNode(file));
        tree.setCellRenderer(new FileTreeCellRenderer());
        tree.setRootVisible(false);
        tree.setToggleClickCount(1);

        when(tree)
                .hasBeenDoubleClicked(openFile())
                .hasBeenRightClicked(showPopupMenu());
        addScrollPane();
    }


    private void addScrollPane() {
        final JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void updateTree() {
        tree.updateUI();
    }

    private TreeActionBuilder.ApplyChanged openFile() {
        return new TreeActionBuilder.ApplyChanged<FileTreeNode>() {
            @Override public void apply(FileTreeNode node) {
                if (node == null)
                   return;
                if (node.isLeaf()) {
                    fileSelectionController.onFile(node.getFile());
                }
            }
        };
    }

    private TreeActionBuilder.ApplyChanged showPopupMenu() {
        return new TreeActionBuilder.ApplyChanged<FileTreeNode>() {
            @Override public void apply(FileTreeNode node) {
                final Point location = MouseInfo.getPointerInfo().getLocation();
                final Point locationOnScreen = tree.getLocationOnScreen();
                final int x = location.x - locationOnScreen.x;
                final int y = location.y - locationOnScreen.y;
                new RightClickTreeMenu(app, marklogController, tree, node, x, y);
            }
        };
    }

}