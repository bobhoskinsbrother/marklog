package uk.co.itstherules.marklog.editor.filesystem.tree;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.ArrayList;

public class ThreadingTest {

    public static void main(String[] args) {
        TreeModel model = new FileTreeModel();
        JTree tree = new JTree(model);
        JFrame frame = new JFrame();
        frame.add(new JScrollPane(tree));
        frame.setBounds(0, 0, 400, 400);
        frame.show();
    }

    private static class FileTreeModel implements TreeModel {

        private EventListenerList listeners = new EventListenerList();
        private CachedFile root;

        FileTreeModel() {
            root = new CachedFile(new File("/"), null);
        }

        public Object getRoot() {
            return root;
        }

        public Object getChild(Object parent, int index) {
            return ((CachedFile) parent).getChild(index);
        }

        public int getChildCount(Object parent) {
            return ((CachedFile) parent).getChildCount();
        }

        public boolean isLeaf(Object node) {
            return ((CachedFile) node).isLeaf();
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        public int getIndexOfChild(Object parent, Object child) {
            return ((CachedFile) parent).getIndexOfChild(child);
        }

        public void addTreeModelListener(TreeModelListener l) {
            listeners.add(TreeModelListener.class, l);
        }

        public void removeTreeModelListener(TreeModelListener l) {
            listeners.remove(TreeModelListener.class, l);
        }

        private class CachedFile {

            private boolean loaded;
            private boolean loading;
            private ArrayList children;
            private File file;
            private CachedFile parent;

            CachedFile(File file, CachedFile parent) {
                this.file = file;
                this.parent = parent;
                loaded = false;
                children = new ArrayList();
            }

            public int getChildCount() {
                boolean shouldLoad = false;
                synchronized (this) {
                    if (!loaded && !loading) {
                        loading = true;
                        shouldLoad = true;
                    }
                }
                if (shouldLoad) {
                    load();
                }
                return children.size();
            }

            private void load() {
                System.out.println("loading: " + file);
                new Thread(new Runnable() {
                    public void run() {
                        File[] files = file.listFiles();
                        for (int counter = 0; counter < files.length; counter++) {
                            final File file = files[counter];
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    add(file);
                                }
                            });
                        }
                        System.out.println("finished loading: " + file);
                        synchronized (CachedFile.this) {
                            loading = false;
                        }
                    }
                }).start();
            }

            private void add(File file) {
                children.add(new CachedFile(file, this));
                Object[] listeners = FileTreeModel.this.listeners.getListenerList();
                TreeModelEvent e = new TreeModelEvent(FileTreeModel.this, getPath(), new int[]{children.size() - 1}, new Object[]{children});
                for (int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == TreeModelListener.class) {
                        ((TreeModelListener) listeners[i + 1]).
                                treeNodesInserted(e);
                    }
                }
            }

            public Object getChild(int index) {
                return children.get(index);
            }

            public boolean isLeaf() {
                return file.isFile();
            }

            public int getIndexOfChild(Object child) {
                return children.indexOf(child);
            }

            private Object[] getPath() {
                return getPathToRoot(this, 0);
            }

            private Object[] getPathToRoot(CachedFile aNode, int depth) {
                Object[] retNodes;
                if (aNode == null) {
                    if (depth == 0)
                        return null;
                    else
                        retNodes = new Object[depth];
                } else {
                    depth++;
                    if (aNode == root)
                        retNodes = new Object[depth];
                    else
                        retNodes = getPathToRoot(aNode.parent, depth);
                    retNodes[retNodes.length - depth] = aNode;
                }
                return retNodes;
            }

            public String toString() {
                return file.toString();
            }
        }
    }
}