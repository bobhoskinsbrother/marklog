package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.MenuItemActionBuilder;
import uk.co.itstherules.marklog.editor.dialogs.NewDirectoryDialog;
import uk.co.itstherules.marklog.editor.dialogs.NewPostDialog;
import uk.co.itstherules.marklog.editor.dialogs.DeleteDirectoryDialog;
import uk.co.itstherules.marklog.editor.filesystem.tree.file.model.FileModel;

import javax.swing.*;

import static uk.co.itstherules.marklog.editor.viewbuilder.MenuItemBuilder.item;

public final class RightClickTreeMenu extends JPopupMenu {

    private final FileModel fileModel;
    private final MarklogApp app;
    private final MarklogController controller;

    public RightClickTreeMenu(MarklogApp app, MarklogController controller, JTree tree, FileModel fileModel, int x, int y) {
        this.app = app;
        this.controller = controller;
        this.fileModel = fileModel;

        add(item("Add New Post").withClickAction(openAddNewPostDialog()).ok());
        add(item("Add New Directory").withClickAction(openAddNewDirectoryDialog()).ok());

        if (fileModel.getAllowsChildren()) {
            if(!fileModel.isRoot()) {
                if (fileModel.hasChildren()) {
                    add(item("Delete Directory").withClickAction(openDeleteDirectoryDialog()).ok());
                } else {
                    add(item("Delete Directory").withClickAction(deleteDirectory()).ok());
                }
            }
        } else {
            if(fileModel.getFile().getName().endsWith(".md")) {
                add(item("Delete Post").withClickAction(deleteFile()).ok());
            } else if(!fileModel.getFile().getName().endsWith(".marklog")) {
                add(item("Delete File").withClickAction(deleteFile()).ok());
            }
        }
        show(tree, x, y);
    }

    private MenuItemActionBuilder.ApplyChanged deleteFile() {
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
                    new NewDirectoryDialog(app, fileModel.getFile());
                } else {
                    new NewDirectoryDialog(app, fileModel.getParent().getFile());
                }
            }
        };
    }

    private MenuItemActionBuilder.ApplyChanged openAddNewPostDialog() {
        return new MenuItemActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if (fileModel.isRoot() || fileModel.getAllowsChildren()) {
                    new NewPostDialog(app, controller, fileModel.getFile());
                } else {
                    new NewPostDialog(app, controller, fileModel.getParent().getFile());
                }
            }
        };
    }
}
