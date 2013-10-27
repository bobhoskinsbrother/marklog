package uk.co.itstherules.marklog.editor.filesystem.tree;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class FileTreeNode implements TreeNode {

    private File file;
    private File[] children;
    private TreeNode parent;
    private boolean isRoot;

    public FileTreeNode(File file, boolean isRoot, TreeNode parent) {
        this.file = file;
        this.isRoot = isRoot;
        this.parent = parent;
        this.children = this.file.listFiles();
        if (this.children == null) {
            this.children = new File[0];
        }
    }

    public FileTreeNode(File... children) {
        this.file = null;
        this.parent = null;
        this.children = children;
    }

    public Enumeration<?> children() {
        final int elementCount = this.children.length;
        return new Enumeration<File>() {
            int count = 0;

            public boolean hasMoreElements() {
                return this.count < elementCount;
            }

            public File nextElement() {
                if (this.count < elementCount) {
                    return FileTreeNode.this.children[this.count++];
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };

    }

    public boolean getAllowsChildren() {
        return true;
    }

    public TreeNode getChildAt(int childIndex) {
        return new FileTreeNode(this.children[childIndex], this.parent == null, this);
    }

    public int getChildCount() {
        return this.children.length;
    }

    public int getIndex(TreeNode node) {
        FileTreeNode treeNode = FileTreeNode.class.cast(node);
        for (int i = 0; i < this.children.length; i++) {
            if (treeNode.file.equals(this.children[i]))
                return i;
        }
        return -1;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public boolean isBranch() {
        return file.isDirectory() && !isRoot();
    }

    public boolean hasChildren() {
        return getChildCount() > 0;
    }

    public boolean isLeaf() {
        return (this.getChildCount() == 0);
    }

    File getFile() {
        return file;
    }

    File getDirectory() {
        if (!file.isDirectory()) {
            return file.getParentFile();
        }
        return file;
    }

    boolean isRoot() {
        return isRoot;
    }
}
