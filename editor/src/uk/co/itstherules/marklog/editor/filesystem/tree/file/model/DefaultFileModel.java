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

    public void setAllowsChildren(boolean allows) {
        if (allows != allowsChildren) {
            allowsChildren = allows;
            if (!allowsChildren) {
                removeAllChildren();
            }
        }
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

    public void removeAllChildren() {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            remove(i);
        }
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

    public boolean isNodeDescendant(DefaultFileModel anotherNode) {
        if (anotherNode == null)
            return false;
        return anotherNode.isNodeAncestor(this);
    }

    public FileModel getSharedAncestor(DefaultFileModel aNode) {
        if (aNode == this) {
            return this;
        } else if (aNode == null) {
            return null;
        }
        int level1, level2, diff;
        FileModel node1, node2;
        level1 = getLevel();
        level2 = aNode.getLevel();
        if (level2 > level1) {
            diff = level2 - level1;
            node1 = aNode;
            node2 = this;
        } else {
            diff = level1 - level2;
            node1 = this;
            node2 = aNode;
        }
        while (diff > 0) {
            node1 = node1.getParent();
            diff--;
        }
        do {
            if (node1 == node2) {
                return node1;
            }
            node1 = node1.getParent();
            node2 = node2.getParent();
        } while (node1 != null);
        if (node1 != null || node2 != null) {
            throw new Error("nodes should be null");
        }
        return null;
    }

    public boolean isNodeRelated(DefaultFileModel aNode) {
        return (aNode != null) && (getRoot() == aNode.getRoot());
    }

    public int getDepth() {
        Object last = null;
        Enumeration enum_ = breadthFirstEnumeration();
        while (enum_.hasMoreElements()) {
            last = enum_.nextElement();
        }
        if (last == null) {
            throw new Error("nodes should be null");
        }
        return ((DefaultFileModel) last).getLevel() - getLevel();
    }

    public int getLevel() {
        FileModel ancestor;
        int levels = 0;
        ancestor = this;
        while ((ancestor = ancestor.getParent()) != null) {
            levels++;
        }
        return levels;
    }

    public FileModel[] getPath() {
        return getPathToRoot(this, 0);
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

    public Object[] getUserObjectPath() {
        FileModel[] realPath = getPath();
        Object[] retPath = new Object[realPath.length];
        for (int counter = 0; counter < realPath.length; counter++)
            retPath[counter] = ((DefaultFileModel) realPath[counter]).getFile();
        return retPath;
    }

    public FileModel getRoot() {
        FileModel ancestor = this;
        FileModel previous;
        do {
            previous = ancestor;
            ancestor = ancestor.getParent();
        } while (ancestor != null);
        return previous;
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public DefaultFileModel getNextNode() {
        if (getChildCount() == 0) {
            DefaultFileModel nextSibling = getNextSibling();
            if (nextSibling == null) {
                DefaultFileModel aNode = (DefaultFileModel) getParent();
                do {
                    if (aNode == null) {
                        return null;
                    }
                    nextSibling = aNode.getNextSibling();
                    if (nextSibling != null) {
                        return nextSibling;
                    }
                    aNode = (DefaultFileModel) aNode.getParent();
                } while (true);
            } else {
                return nextSibling;
            }
        } else {
            return (DefaultFileModel) getChildAt(0);
        }
    }

    public DefaultFileModel getPreviousNode() {
        DefaultFileModel previousSibling;
        DefaultFileModel myParent = (DefaultFileModel) getParent();
        if (myParent == null) {
            return null;
        }
        previousSibling = getPreviousSibling();
        if (previousSibling != null) {
            if (previousSibling.getChildCount() == 0)
                return previousSibling;
            else
                return previousSibling.getLastLeaf();
        } else {
            return myParent;
        }
    }

    public Enumeration<FileModel> preorderEnumeration() {
        return new PreorderEnumeration(this);
    }

    public Enumeration<FileModel> postorderEnumeration() {
        return new PostorderEnumeration(this);
    }

    public Enumeration<FileModel> breadthFirstEnumeration() {
        return new BreadthFirstEnumeration(this);
    }

    public Enumeration<FileModel> depthFirstEnumeration() {
        return postorderEnumeration();
    }

    public Enumeration<FileModel> pathFromAncestorEnumeration(FileModel ancestor) {
        return new PathBetweenNodesEnumeration(ancestor, this);
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

    public int getSiblingCount() {
        FileModel myParent = getParent();
        if (myParent == null) {
            return 1;
        } else {
            return myParent.getChildCount();
        }
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

    public int getLeafCount() {
        int count = 0;
        FileModel node;
        Enumeration enum_ = breadthFirstEnumeration();
        while (enum_.hasMoreElements()) {
            node = (FileModel) enum_.nextElement();
            if (node.isLeaf()) {
                count++;
            }
        }
        if (count < 1) {
            throw new Error("tree has zero leaves");
        }
        return count;
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

    private final class PreorderEnumeration implements Enumeration<FileModel> {

        private final Stack<Enumeration> stack = new Stack<Enumeration>();

        public PreorderEnumeration(FileModel rootNode) {
            super();
            Vector<FileModel> v = new Vector<FileModel>(1);
            v.addElement(rootNode);
            stack.push(v.elements());
        }

        public boolean hasMoreElements() {
            return (!stack.empty() && stack.peek().hasMoreElements());
        }

        public FileModel nextElement() {
            Enumeration enumer = stack.peek();
            FileModel node = (FileModel) enumer.nextElement();
            Enumeration children = node.children();
            if (!enumer.hasMoreElements()) {
                stack.pop();
            }
            if (children.hasMoreElements()) {
                stack.push(children);
            }
            return node;
        }

    }

    final class PostorderEnumeration implements Enumeration<FileModel> {

        protected FileModel root;
        protected Enumeration<FileModel> children;
        protected Enumeration<FileModel> subtree;

        public PostorderEnumeration(FileModel rootNode) {
            super();
            root = rootNode;
            children = root.children();
            subtree = EMPTY_ENUMERATION;
        }

        public boolean hasMoreElements() {
            return root != null;
        }

        public FileModel nextElement() {
            FileModel retval;
            if (subtree.hasMoreElements()) {
                retval = subtree.nextElement();
            } else if (children.hasMoreElements()) {
                subtree = new PostorderEnumeration(children.nextElement());
                retval = subtree.nextElement();
            } else {
                retval = root;
                root = null;
            }
            return retval;
        }

    }

    final class BreadthFirstEnumeration implements Enumeration<FileModel> {

        protected Queue queue;

        public BreadthFirstEnumeration(FileModel rootNode) {
            super();
            Vector<FileModel> v = new Vector<FileModel>(1);
            v.addElement(rootNode);
            queue = new Queue();
            queue.enqueue(v.elements());
        }

        public boolean hasMoreElements() {
            return (!queue.isEmpty() && ((Enumeration) queue.firstObject()).hasMoreElements());
        }

        public FileModel nextElement() {
            Enumeration enumer = (Enumeration) queue.firstObject();
            FileModel node = (FileModel) enumer.nextElement();
            Enumeration children = node.children();
            if (!enumer.hasMoreElements()) {
                queue.dequeue();
            }
            if (children.hasMoreElements()) {
                queue.enqueue(children);
            }
            return node;
        }

        final class Queue {

            QNode head;
            QNode tail;

            public void enqueue(Object anObject) {
                if (head == null) {
                    head = tail = new QNode(anObject, null);
                } else {
                    tail.next = new QNode(anObject, null);
                    tail = tail.next;
                }
            }

            public Object dequeue() {
                if (head == null) {
                    throw new NoSuchElementException("No more elements");
                }
                Object retval = head.object;
                QNode oldHead = head;
                head = head.next;
                if (head == null) {
                    tail = null;
                } else {
                    oldHead.next = null;
                }
                return retval;
            }

            public Object firstObject() {
                if (head == null) {
                    throw new NoSuchElementException("No more elements");
                }
                return head.object;
            }

            public boolean isEmpty() {
                return head == null;
            }

            final class QNode {

                public Object object;
                public QNode next;

                public QNode(Object object, QNode next) {
                    this.object = object;
                    this.next = next;
                }
            }

        }

    }

    final class PathBetweenNodesEnumeration implements Enumeration<FileModel> {

        protected Stack<FileModel> stack;

        public PathBetweenNodesEnumeration(FileModel ancestor, FileModel descendant) {
            super();
            if (ancestor == null || descendant == null) {
                throw new IllegalArgumentException("argument is null");
            }
            FileModel current;
            stack = new Stack<FileModel>();
            stack.push(descendant);
            current = descendant;
            while (current != ancestor) {
                current = current.getParent();
                if (current == null && descendant != ancestor) {
                    throw new IllegalArgumentException("node " + ancestor +
                            " is not an ancestor of " + descendant);
                }
                stack.push(current);
            }
        }

        public boolean hasMoreElements() {
            return stack.size() > 0;
        }

        public FileModel nextElement() {
            try {
                return stack.pop();
            } catch (EmptyStackException e) {
                throw new NoSuchElementException("No more elements");
            }
        }

    }

}
