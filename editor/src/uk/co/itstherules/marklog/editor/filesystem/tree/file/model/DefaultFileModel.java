package uk.co.itstherules.marklog.editor.filesystem.tree.file.model;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class DefaultFileModel implements Cloneable, FileModel {

    static public final Enumeration<FileModel> EMPTY_ENUMERATION = Collections.emptyEnumeration();
    protected FileModel parent;
    protected Vector<FileModel> children;
    transient protected File file;
    protected boolean allowsChildren;

    public DefaultFileModel(final File file) {
        super();
        parent = null;
        this.allowsChildren = file.isDirectory();
        this.file = file;
        SwingWorker worker = new SwingWorker() {
            @Override protected Void doInBackground() throws Exception {
                if (allowsChildren) {
                    final File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        insert(new DefaultFileModel(files[i]), i);
                    }
                }
                return null;
            }
        };
        worker.execute();
    }

    public FileModel find(File file) {
        if (isSame(file)) {
            return this;
        }
        if (allowsChildren) {
            final Enumeration<FileModel> children = children();
            while (children.hasMoreElements()) {
                final FileModel child = children.nextElement();
                final FileModel reply = child.find(file);
                if (reply != null) {
                    return reply;
                }
            }
        }
        return null;
    }

    private boolean isSame(File file) {return file.getAbsolutePath().equals(getFile().getAbsolutePath());}

    public void insert(FileModel newChild, int childIndex) {
        if (!allowsChildren) {
            throw new IllegalStateException("node does not allow children");
        } else if (newChild == null) {
            throw new IllegalArgumentException("new child is null");
        } else if (isNodeAncestor(newChild)) {
            throw new IllegalArgumentException("new child is an ancestor");
        }
        FileModel oldParent = newChild.getParent();
        if (oldParent != null) {
            oldParent.remove(newChild);
        }
        newChild.setParent(this);
        if (children == null) {
            children = new Vector<FileModel>();
        }
        children.insertElementAt(newChild, childIndex);
    }

    public void remove(int childIndex) {
        FileModel child = getChildAt(childIndex);
        children.removeElementAt(childIndex);
        child.setParent(null);
    }

    public FileModel getParent() {
        return parent;
    }

    public void setParent(FileModel newParent) {
        parent = newParent;
    }

    public FileModel getChildAt(int index) {
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return children.elementAt(index);
    }

    public int getChildCount() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }
    }

    public int getIndex(FileModel child) {
        if (child == null) {
            throw new IllegalArgumentException("argument is null");
        }
        if (!isNodeChild(child)) {
            return -1;
        }
        return children.indexOf(child);
    }

    public Enumeration<FileModel> children() {
        if (children == null) {
            return EMPTY_ENUMERATION;
        } else {
            return children.elements();
        }
    }

    public boolean getAllowsChildren() {
        return allowsChildren;
    }

    public File getFile() {
        return file;
    }

    public void removeFromParent() {
        FileModel parent = getParent();
        if (parent != null) {
            parent.remove(this);
        }
    }

    public void remove(FileModel aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }
        if (!isNodeChild(aChild)) {
            throw new IllegalArgumentException("argument is not a child");
        }
        remove(getIndex(aChild));
    }

    public void add(FileModel newChild) {
        if (newChild != null && newChild.getParent() == this)
            insert(newChild, getChildCount() - 1);
        else
            insert(newChild, getChildCount());
    }

    public boolean isNodeAncestor(FileModel anotherNode) {
        if (anotherNode == null) {
            return false;
        }
        FileModel ancestor = this;
        do {
            if (ancestor == anotherNode) {
                return true;
            }
        } while ((ancestor = ancestor.getParent()) != null);
        return false;
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
            retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    public boolean isRoot() {
        return getParent() == null;
    }
    public boolean isNodeChild(FileModel aNode) {
        boolean retval;
        if (aNode == null) {
            retval = false;
        } else {
            if (getChildCount() == 0) {
                retval = false;
            } else {
                retval = (aNode.getParent() == this);
            }
        }
        return retval;
    }

    public FileModel getFirstChild() {
        if (getChildCount() == 0) {
            throw new NoSuchElementException("node has no children");
        }
        return getChildAt(0);
    }

    public FileModel getLastChild() {
        if (getChildCount() == 0) {
            throw new NoSuchElementException("node has no children");
        }
        return getChildAt(getChildCount() - 1);
    }

    public FileModel getChildAfter(FileModel aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int index = getIndex(aChild);
        if (index == -1) {
            throw new IllegalArgumentException("node is not a child");
        }
        if (index < getChildCount() - 1) {
            return getChildAt(index + 1);
        } else {
            return null;
        }
    }

    public FileModel getChildBefore(FileModel aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int index = getIndex(aChild);
        if (index == -1) {
            throw new IllegalArgumentException("argument is not a child");
        }
        if (index > 0) {
            return getChildAt(index - 1);
        } else {
            return null;
        }
    }

    public boolean isNodeSibling(FileModel anotherNode) {
        boolean retval;
        if (anotherNode == null) {
            retval = false;
        } else if (anotherNode == this) {
            retval = true;
        } else {
            FileModel myParent = getParent();
            retval = (myParent != null && myParent == anotherNode.getParent());
            if (retval && !((DefaultFileModel) getParent()).isNodeChild(anotherNode)) {
                throw new Error("sibling has different parent");
            }
        }
        return retval;
    }

    public DefaultFileModel getNextSibling() {
        DefaultFileModel retval;
        DefaultFileModel myParent = (DefaultFileModel) getParent();
        if (myParent == null) {
            retval = null;
        } else {
            retval = (DefaultFileModel) myParent.getChildAfter(this);
        }
        if (retval != null && !isNodeSibling(retval)) {
            throw new Error("child of parent is not a sibling");
        }
        return retval;
    }

    public DefaultFileModel getPreviousSibling() {
        DefaultFileModel retval;
        DefaultFileModel myParent = (DefaultFileModel) getParent();
        if (myParent == null) {
            retval = null;
        } else {
            retval = (DefaultFileModel) myParent.getChildBefore(this);
        }
        if (retval != null && !isNodeSibling(retval)) {
            throw new Error("child of parent is not a sibling");
        }
        return retval;
    }

    public boolean isLeaf() {
        return (getChildCount() == 0);
    }

    @Override public boolean hasChildren() {
        return getChildCount() > 0;
    }

    public DefaultFileModel getFirstLeaf() {
        DefaultFileModel node = this;
        while (!node.isLeaf()) {
            node = (DefaultFileModel) node.getFirstChild();
        }
        return node;
    }

    public DefaultFileModel getLastLeaf() {
        DefaultFileModel node = this;
        while (!node.isLeaf()) {
            node = (DefaultFileModel) node.getLastChild();
        }
        return node;
    }

    public DefaultFileModel getNextLeaf() {
        DefaultFileModel nextSibling;
        DefaultFileModel myParent = (DefaultFileModel) getParent();
        if (myParent == null)
            return null;
        nextSibling = getNextSibling();
        if (nextSibling != null)
            return nextSibling.getFirstLeaf();
        return myParent.getNextLeaf();
    }

    public DefaultFileModel getPreviousLeaf() {
        DefaultFileModel previousSibling;
        DefaultFileModel myParent = (DefaultFileModel) getParent();
        if (myParent == null)
            return null;
        previousSibling = getPreviousSibling();
        if (previousSibling != null)
            return previousSibling.getLastLeaf();
        return myParent.getPreviousLeaf();
    }

    public String toString() {
        if (file == null) {
            return "";
        } else {
            return file.toString();
        }
    }

    public Object clone() {
        DefaultFileModel newNode;
        try {
            newNode = (DefaultFileModel) super.clone();
            newNode.children = null;
            newNode.parent = null;

        } catch (CloneNotSupportedException e) {
            throw new Error(e.toString());
        }
        return newNode;
    }

}
