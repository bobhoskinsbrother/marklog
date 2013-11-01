package spike;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSystemTree extends JFrame {

    private JTree fileTree;
    private FileSystemModel fileSystemModel;

    public FileSystemTree(String directory) {
        super("JTree FileSystem Viewer - JavaProgrammingForums.com");
        fileSystemModel = new FileSystemModel(new File(directory));
        fileTree = new JTree(fileSystemModel);
        fileTree.setEditable(true);
        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent event) {
                File file = (File) fileTree.getLastSelectedPathComponent();
                System.out.println(file.getAbsolutePath());
            }
        });
        final JScrollPane scrollPane = new JScrollPane(fileTree);
        JPanel panel = new JPanel();
        panel.add(scrollPane);
        getContentPane().add(panel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(640, 480);
        setVisible(true);
    }

    public static void main(String args[]) {
        new FileSystemTree(System.getProperty("user.dir"));
    }
}

class FileSystemModel implements TreeModel {

    private File root;
    private List<TreeModelListener> listeners = new ArrayList(6);

    public FileSystemModel(File rootDirectory) {
        root = rootDirectory;
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        File directory = (File) parent;
        String[] children = directory.list();
        return new FileSystemFile(directory, children[index]);
    }

    public int getChildCount(Object parent) {
        File file = (File) parent;
        if (file.isDirectory()) {
            String[] fileList = file.list();
            return (fileList != null) ? file.list().length : 0;
        }
        return 0;
    }

    public boolean isLeaf(Object node) {
        File file = (File) node;
        return file.isFile();
    }

    public int getIndexOfChild(Object parent, Object child) {
        File directory = (File) parent;
        File file = (File) child;
        String[] children = directory.list();
        for (int i = 0; i < children.length; i++) {
            if (file.getName().equals(children[i])) {
                return i;
            }
        }
        return -1;

    }

    public void valueForPathChanged(TreePath path, Object value) {
        File oldFile = File.class.cast(path.getLastPathComponent());
        String fileParentPath = oldFile.getParent();
        String newFileName = (String) value;
        File targetFile = new File(fileParentPath, newFileName);
        oldFile.renameTo(targetFile);
        File parent = new File(fileParentPath);
        int[] changedChildrenIndices = {getIndexOfChild(parent, targetFile)};
        Object[] changedChildren = {targetFile};
        fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);

    }

    private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
        for (TreeModelListener listener : listeners) {
            listener.treeNodesChanged(event);
        }
    }

    public void addTreeModelListener(TreeModelListener listener) {
        listeners.add(listener);
    }

    public void removeTreeModelListener(TreeModelListener listener) {
        listeners.remove(listener);
    }

    private class FileSystemFile extends File {

        public FileSystemFile(File parent, String child) {
            super(parent, child);
        }

        public String toString() {
            return getName();
        }
    }
}

