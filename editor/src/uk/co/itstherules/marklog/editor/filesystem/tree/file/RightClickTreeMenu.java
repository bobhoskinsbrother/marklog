package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogPanel;
import uk.co.itstherules.marklog.editor.actionbuilder.MenuItemActionBuilder;
import uk.co.itstherules.marklog.editor.dialogs.AddNewDirectoryDialog;
import uk.co.itstherules.marklog.editor.dialogs.AddNewPostDialog;
import uk.co.itstherules.marklog.editor.dialogs.DeleteDirectoryDialog;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.FileModel;

import javax.swing.*;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class RightClickTreeMenu extends JPopupMenu {

    private final FileModel fileModel;
    private final MarklogApp app;
    private final MarklogPanel.MarklogController controller;

    public RightClickTreeMenu(MarklogApp app, MarklogPanel.MarklogController controller, JTree tree, FileModel fileModel, int x, int y) {
        this.app = app;
        this.controller = controller;
        this.fileModel = fileModel;
        final JMenuItem addNewPostMenuItem = new JMenuItem("Add New Post");
        final JMenuItem addNewDirectoryMenuItem = new JMenuItem("Add New Directory");
        addNewDirectoryMenuItem.setName("addNewDirectory");
        addNewPostMenuItem.setName("addNewPost");
        when(addNewPostMenuItem).hasBeenClicked(openAddNewPostDialog());
        when(addNewDirectoryMenuItem).hasBeenClicked(openAddNewDirectoryDialog());

        add(addNewPostMenuItem);
        add(addNewDirectoryMenuItem);
        if (fileModel.getAllowsChildren()) {
            final JMenuItem deleteDirectoryMenuItem = new JMenuItem("Delete Directory");
            deleteDirectoryMenuItem.setName("deleteDirectory");
            if (fileModel.hasChildren()) {
                when(deleteDirectoryMenuItem).hasBeenClicked(openDeleteDirectoryDialog());
            } else {
                when(deleteDirectoryMenuItem).hasBeenClicked(deleteDirectory());
            }
            add(deleteDirectoryMenuItem);
        } else {
            if(fileModel.getFile().getName().endsWith(".md")) {
                final JMenuItem deletePostMenuItem = new JMenuItem("Delete Post");
                deletePostMenuItem.setName("deletePost");
                when(deletePostMenuItem).hasBeenClicked(deletePost());
                add(deletePostMenuItem);
            }
        }
        show(tree, x, y);
    }

    private MenuItemActionBuilder.ApplyChanged deletePost() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                controller.deleteFile(fileModel.getFile());
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged deleteDirectory() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                controller.deleteDirectory(fileModel.getFile(), false);
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openDeleteDirectoryDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                new DeleteDirectoryDialog(app, controller, fileModel.getFile());
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openAddNewDirectoryDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if (fileModel.isRoot() || fileModel.getAllowsChildren()) {
                    new AddNewDirectoryDialog(app, fileModel.getFile());
                } else {
                    new AddNewDirectoryDialog(app, fileModel.getParent().getFile());
                }
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openAddNewPostDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if (fileModel.isRoot() || fileModel.getAllowsChildren()) {
                    new AddNewPostDialog(app, controller, fileModel.getFile());
                } else {
                    new AddNewPostDialog(app, controller, fileModel.getParent().getFile());
                }
            }
        };
    }
}
