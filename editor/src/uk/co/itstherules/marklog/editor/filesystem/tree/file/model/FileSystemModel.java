package uk.co.itstherules.marklog.editor.filesystem.tree.file.model;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.beans.ConstructorProperties;
import java.io.*;
import java.util.EventListener;
import java.util.Vector;

public class FileSystemModel implements Serializable, TreeModel {

    protected FileModel root;
    protected EventListenerList listenerList = new EventListenerList();

    @ConstructorProperties({"root"})
    public FileSystemModel(File root) {
        this.root = new DefaultFileModel(root);
    }

    public Object getRoot() {
        return root;
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null)
            return -1;
        return ((FileModel) parent).getIndex((FileModel) child);
    }

    public Object getChild(Object parent, int index) {
        return ((FileModel) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        return ((FileModel) parent).getChildCount();
    }

    public boolean isLeaf(Object node) {
        final FileModel fileModel = FileModel.class.cast(node);
        return !fileModel.getAllowsChildren();
    }


    public FileModel findFileModelFor(File file) {
        return root.find(file);
    }

    public void reload() {
        reload(root);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        FileModel node = (FileModel) path.getLastPathComponent();
        nodeChanged(node);
    }

    public void insertNodeInto(FileModel parent, FileModel newChild, int index) {
        parent.insert(newChild, index);
        int[] indexes = new int[1];
        indexes[0] = index;
        nodesWereInserted(parent, indexes);
    }

    public void removeNodeFromParent(FileModel node) {
        FileModel parent = node.getParent();
        if (parent == null)
            throw new IllegalArgumentException("node does not have a parent.");
        int[] childIndex = new int[1];
        Object[] removedArray = new Object[1];
        childIndex[0] = parent.getIndex(node);
        parent.remove(childIndex[0]);
        removedArray[0] = node;
        nodesWereRemoved(parent, childIndex, removedArray);
    }

    public void nodeChanged(FileModel node) {
        if (listenerList != null && node != null) {
            FileModel parent = node.getParent();
            if (parent != null) {
                int index = parent.getIndex(node);
                if (index != -1) {
                    int[] childIndexes = new int[1];
                    childIndexes[0] = index;
                    nodesChanged(parent, childIndexes);
                }
            } else if (node == getRoot()) {
                nodesChanged(node, null);
            }
        }
    }

    public void reload(FileModel node) {
        if (node != null) {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    public void nodesWereInserted(FileModel node, int[] childIndexes) {
        if (listenerList != null && node != null && childIndexes != null && childIndexes.length > 0) {
            int childrenCount = childIndexes.length;
            Object[] newChildren = new Object[childrenCount];
            for (int counter = 0; counter < childrenCount; counter++)
                newChildren[counter] = node.getChildAt(childIndexes[counter]);
            fireTreeNodesInserted(this, getPathToRoot(node), childIndexes, newChildren);
        }
    }

    public void nodesWereRemoved(FileModel node, int[] childIndices, Object[] removedChildren) {
        if (node != null && childIndices != null) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
        }
    }

    public void nodesChanged(FileModel node, int[] childIndices) {
        if (node != null) {
            if (childIndices != null) {
                int cCount = childIndices.length;
                if (cCount > 0) {
                    Object[] cChildren = new Object[cCount];
                    for (int counter = 0; counter < cCount; counter++)
                        cChildren[counter] = node.getChildAt(childIndices[counter]);
                    fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
                }
            } else if (node == getRoot()) {
                fireTreeNodesChanged(this, getPathToRoot(node), null, null);
            }
        }
    }

    public void nodeStructureChanged(FileModel node) {
        if (node != null) {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    public FileModel[] getPathToRoot(FileModel aNode) {
        return getPathToRoot(aNode, 0);
    }

    protected FileModel[] getPathToRoot(FileModel aNode, int depth) {
        FileModel[] retNodes;
        if (aNode == null) {
            if (depth == 0)
                return null;
            else
                retNodes = new FileModel[depth];
        } else {
            depth++;
            if (aNode == root)
                retNodes = new FileModel[depth];
            else
                retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    public TreeModelListener[] getTreeModelListeners() {
        return listenerList.getListeners(TreeModelListener.class);
    }

    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    private void fireTreeStructureChanged(Object source, TreePath path) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        Vector<Object> values = new Vector<Object>();
        s.defaultWriteObject();
        if (root != null && root instanceof Serializable) {
            values.addElement("root");
            values.addElement(root);
        }
        s.writeObject(values);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Vector values = (Vector) s.readObject();
        int indexCounter = 0;
        int maxCounter = values.size();
        if (indexCounter < maxCounter && values.elementAt(indexCounter).
                equals("root")) {
            root = (FileModel) values.elementAt(++indexCounter);
            indexCounter++;
        }
    }
}
