package uk.co.itstherules.marklog.editor.filesystem.tree;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogPanel;
import uk.co.itstherules.marklog.editor.actionbuilder.MenuItemActionBuilder;
import uk.co.itstherules.marklog.editor.dialogs.AddNewDirectoryDialog;
import uk.co.itstherules.marklog.editor.dialogs.AddNewPostDialog;
import uk.co.itstherules.marklog.editor.dialogs.DeleteDirectoryDialog;

import javax.swing.*;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class RightClickTreeMenu extends JPopupMenu {

    private final FileTreeNode node;
    private final MarklogApp app;
    private final MarklogPanel.MarklogController controller;

    public RightClickTreeMenu(MarklogApp app, MarklogPanel.MarklogController controller, JTree tree, FileTreeNode node, int x, int y) {
        this.app = app;
        this.controller = controller;
        this.node = node;
        final JMenuItem addNewPostMenuItem = new JMenuItem("Add New Post");
        final JMenuItem addNewDirectoryMenuItem = new JMenuItem("Add New Directory");
        when(addNewPostMenuItem).hasBeenClicked(openAddNewPostDialog());
        when(addNewDirectoryMenuItem).hasBeenClicked(openAddNewDirectoryDialog());
        add(addNewPostMenuItem);
        add(addNewDirectoryMenuItem);
        if (node.isBranch()) {
            final JMenuItem deleteDirectoryMenuItem = new JMenuItem("Delete Directory");
            if (node.hasChildren()) {
                when(deleteDirectoryMenuItem).hasBeenClicked(openDeleteDirectoryDialog());
            } else {
                when(deleteDirectoryMenuItem).hasBeenClicked(deleteDirectory());
            }
            add(deleteDirectoryMenuItem);
        }
        show(tree, x, y);
    }

    private MenuItemActionBuilder.ApplyChanged deleteDirectory() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                node.getDirectory().delete();
                controller.refresh();
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openDeleteDirectoryDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                new DeleteDirectoryDialog(app, controller, node.getDirectory());
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openAddNewDirectoryDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                new AddNewDirectoryDialog(app, controller, node.getDirectory());
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openAddNewPostDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                new AddNewPostDialog(app, controller, node.getDirectory());
            }
        };
    }
}
