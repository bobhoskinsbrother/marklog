package uk.co.itstherules.marklog.editor.filesystem.tree;

import uk.co.itstherules.marklog.editor.IconLoader;
import uk.co.itstherules.marklog.editor.actionbuilder.TreeActionBuilder;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public class FileTree extends JPanel {

    private final JTree tree;

    public FileTree(File dir) {
        setLayout(new BorderLayout());
        tree = new JTree(new FileTreeModel(dir));
        tree.setCellRenderer(new FileTreeCellRenderer());
        when(tree).hasBeenDblClicked(printOutFile());
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);
        add(BorderLayout.CENTER, scrollpane);
    }

    private TreeActionBuilder.ApplyChanged printOutFile() {
        return new TreeActionBuilder.ApplyChanged<FileModel>() {
            public void apply(FileModel node) {
                System.out.println("You selected " + node.getFile().getAbsolutePath());
            }
        };
    }

    private class FileTreeModel implements TreeModel {

        private FileModel root;
        private TreeModelListener removeTreeModelListener;
        private TreeModelListener addTreeModelListener;

        public FileTreeModel(File root) {
            this.root = new FileModel(root, this, true);
        }

        public Object getChild(Object parent, int index) {
            Object[] children = getChildren(parent);
            return children[index];
        }

        public int getChildCount(Object parent) {
            return getChildren(parent).length;
        }

        public int getIndexOfChild(Object parent, Object child) {
            Object[] children = getChildren(parent);
            return Arrays.asList(children).indexOf(child);
        }

        private Object[] getChildren(Object parent) {
            return FileModel.class.cast(parent).getChildren().toArray();
        }

        public Object getRoot() {
            return root;
        }

        public boolean isLeaf(Object node) {
            return (node != root && !FileModel.class.cast(node).isDirectory());
        }

        public void removeSelected() {
            TreePath treePath = tree.getSelectionPath();
            final FileModel fileModel = FileModel.class.cast(treePath.getLastPathComponent());
            final int index = treePath.getPathCount();
            TreeModelEvent event = new TreeModelEvent(this, treePath, new int[]{index}, new Object[]{fileModel});
            removeTreeModelListener.treeNodesRemoved(event);
        }

        public void removeTreeModelListener(TreeModelListener treeModelListener) {
            this.removeTreeModelListener = treeModelListener;
        }

        public void addTreeModelListener(TreeModelListener addTreeModelListener) {
            this.addTreeModelListener = addTreeModelListener;
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
            // omitted.
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(200, 400);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 400);
    }

    public static void main(final String[] av) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("FileTree");
                frame.setForeground(Color.black);
                frame.setBackground(Color.lightGray);
                Container cp = frame.getContentPane();
                if (av.length == 0) {
                    cp.add(new FileTree(new File(System.getProperty("user.dir"))));
                } else {
                    cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
                    for (int i = 0; i < av.length; i++) {
                        cp.add(new FileTree(new File(av[i])));
                    }
                }
                frame.pack();
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }

    public class FileModel {

        private final File file;
        private final FileTreeModel fileTreeModel;
        private final boolean root;
        private java.util.List<FileModel> children;

        public FileModel(File file, FileTreeModel fileTreeModel, boolean isRoot) {
            this.file = file;
            this.fileTreeModel = fileTreeModel;
            root = isRoot;
        }

        public FileModel(File file, FileTreeModel fileTreeModel) {
            this(file, fileTreeModel, false);
        }

        private java.util.List<FileModel> makeChildren(File file) {
            final ArrayList<FileModel> models = new ArrayList<FileModel>();
            final File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File current = files[i];
                models.add(new FileModel(current, fileTreeModel));
            }
            return models;
        }

        public java.util.List<FileModel> getChildren() {
            if (children == null) {
                children = makeChildren(file);
            }
            return children;
        }

        public boolean isDirectory() {
            return file.isDirectory();
        }

        @Override public String toString() {
            return file.getName();
        }

        public File getFile() {
            return file;
        }

        public boolean isRoot() {
            return root;
        }
    }

    class FileTreeCellRenderer extends DefaultTreeCellRenderer {

        private Map<String, Icon> iconCache = new HashMap<String, Icon>();
        private Map<File, String> rootNameCache = new HashMap<File, String>();
        private Icon defaultDirectory;
        private Icon defaultFile;

        FileTreeCellRenderer() {
            defaultDirectory = IconLoader.fromResource("/folder.png");
            defaultFile = IconLoader.fromResource("/file.png");
            iconCache.put("", defaultDirectory);
            iconCache.put("md", IconLoader.fromResource("/md_file.png"));
            iconCache.put("marklog", IconLoader.fromResource("/marklog_file.png"));
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            FileModel fileModel = FileModel.class.cast(value);
            File file = fileModel.getFile();
            String filename = "";
            if (file != null) {
                if (fileModel.isRoot()) {
                    filename = getRootFilename(file);
                } else {
                    filename = file.getName();
                }
            }
            JLabel label = JLabel.class.cast(super.getTreeCellRendererComponent(tree, filename, sel, expanded, leaf, row, hasFocus));
            if (file != null) {
                Icon icon = getIconFor(file);
                label.setIcon(icon);
            }
            return label;
        }

        private Icon getIconFor(File file) {
            final String extension = getExtensionFor(file);
            final Icon icon = this.iconCache.get(extension);
            if (icon == null) {
                return defaultFile;
            }
            return icon;
        }

        private String getExtensionFor(File file) {
            final String fileName = file.getName();
            String extension = "";
            int dotIndex = fileName.lastIndexOf('.');
            int slashIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
            if (dotIndex > slashIndex) {
                extension = fileName.substring(dotIndex + 1);
            }
            return extension;
        }

        private String getRootFilename(File file) {
            String filename;
            filename = this.rootNameCache.get(file);
            if (filename == null) {
                filename = TreeBrowser.fileSystemView.getSystemDisplayName(file);
                this.rootNameCache.put(file, filename);
            }
            return filename;
        }
    }

}