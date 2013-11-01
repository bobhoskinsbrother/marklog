package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;

class FileSystemModel implements TreeModel {

    private FileModel root;
    private TreeModelListener removeTreeModelListener;
    private TreeModelListener addTreeModelListener;

    public FileSystemModel(File root) {
        this.root = new FileModel(root, this, true);
    }

    public Object getChild(Object parent, int index) {
        return getChildren(parent).get(index);
    }

    public int getChildCount(Object parent) {
        return getChildren(parent).size();
    }

    public int getIndexOfChild(Object parent, Object child) {
        return getChildren(parent).indexOf(child);
    }

    private java.util.List<FileModel> getChildren(Object parent) {
        return FileModel.class.cast(parent).getChildren();
    }

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return (node != root && !FileModel.class.cast(node).isDirectory());
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        this.removeTreeModelListener = treeModelListener;
    }

    public void addTreeModelListener(TreeModelListener addTreeModelListener) {
        this.addTreeModelListener = addTreeModelListener;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {}

}
