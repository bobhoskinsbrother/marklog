package components;
 
/*
 * This code is based on an example provided by Richard Stanford, 
 * a tutorial reader.
 */

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;

public class DynamicTree extends JPanel {
    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    public DynamicTree(final String rootPath) {
        super(new GridLayout(1,0));

        rootNode = scan(new File(rootPath));
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new MyTreeModelListener());
        tree = new JTree(treeModel);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                while(true){
                    refresh(rootPath);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {}
                }
            }
        };
        worker.execute();

    }

    public void refresh(String rootPath) {
       rootNode = scan(new File(rootPath));

      String exp = TreeUtil.getExpansionState(tree, 0);
      tree.setModel(new DefaultTreeModel(rootNode));
      TreeUtil.restoreExpanstionState(tree, 0, exp);
    }

    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we've already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */

            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)(node.getChildAt(index));

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }

        public void treeNodesInserted(TreeModelEvent e) {
            System.out.println("treeNodeInsterted");
        }
        public void treeNodesRemoved(TreeModelEvent e) {
            System.out.println("treeNodeRemoved");
        }
        public void treeStructureChanged(TreeModelEvent e) {
            System.out.println("treeStructureChanged");
        }
    }

    private static DefaultMutableTreeNode scan(File node)
    {

        DefaultMutableTreeNode dMTN = scan(node, "");
     // System.out.println("------------------------");
        return dMTN;
    }

    private static DefaultMutableTreeNode scan(File node, String tab)
    {
        //System.out.println(tab+node.getName());
        DefaultMutableTreeNode ret = new DefaultMutableTreeNode(node.getName());

        if (node.isDirectory()) {
           // System.out.println(tab+"|__");
            for (File child: node.listFiles()){
                ret.add(scan(child, tab+"   "));
            }
        }
        return ret;
    }
}