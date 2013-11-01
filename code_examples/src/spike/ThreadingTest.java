package spike;

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

        private EventListenerList listenerList = new EventListenerList();
        private CachedFile root;

        FileTreeModel() {
            root = new CachedFile(new File(System.getProperty("user.dir")), null);
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
            listenerList.add(TreeModelListener.class, l);
        }

        public void removeTreeModelListener(TreeModelListener l) {
            listenerList.remove(TreeModelListener.class, l);
        }

        // A TreeNode like class that wraps a File. The children are
// loaded in a background thread when getChildCount is invoked.
        private class CachedFile {

            // Whether or not we've loaded the children
            private boolean loaded;
            // Whether or not we're in the process of loading
            private boolean loading;
            // The children
            private ArrayList children;
            // File we reference
            private File file;
            // Parent
            private CachedFile parent;

            CachedFile(File file, CachedFile parent) {
                this.file = file;
                this.parent = parent;
                loaded = false;
                children = new ArrayList();
            }

            //
// Mark, these are the 3 key methods, getChildCount,
// load and add.
//
            public int getChildCount() {
// Mark, this is the key, only start loading if you
// haven't loaded and are not in the process of loading.
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
// Loading spawns a thread to do the loading, nodes are
// added on the EDT and notification is then sent out.
                new Thread(new Runnable() {
                    public void run() {
                        File[] files = file.listFiles();
                        for (int counter = 0; counter < files.length; counter++) {
                            final File file = files[counter];
// Do a sleep here to show the effect you are
// after
                            try {
                                Thread.sleep(70);
                            } catch (InterruptedException ie) {
                            }
// Do actual insertion on EDT.
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    add(file);
                                }
                            });
                        }
                        System.out.println("finished loading: " + file);
                        synchronized (CachedFile.this) {
// Update state to indicate everything is loaded loaded = true;
                            loading = false;
                        }
                    }
                }).start();
            }

            private void add(File file) {
// Add to internal list
                children.add(new CachedFile(file, this));
// send notification
                Object[] listeners = listenerList.getListenerList();
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