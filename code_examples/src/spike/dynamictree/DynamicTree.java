/*
 * DynamicTree.java
 * 
 * Originally written by Joseph Bowbeer and released into the public domain.
 * This may be used for any purposes whatsoever without acknowledgment.
 */

package spike.dynamictree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;

/**
 * Demonstrates the use of a SwingWorker thread to dynamically
 * expand the nodes of a JTree.
 *
 * @author Joseph Bowbeer
 * @version 1.0
 */
public class DynamicTree extends JPanel {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JPanel jPanel;
    private javax.swing.JTree jTree;
    private javax.swing.JLabel jlblStatus;
// End of variables declaration//GEN-END:variables

    /**
     * Creates children for expanded nodes.
     */
    private static transient TreeNodeFactory factory;

    /**
     * The active worker.
     */
    private transient SwingWorker worker;

    /**
     * Initializes the form and sets a default source.
     */
    public DynamicTree() {
        initComponents();
        setSource(null);
    }

    /**
     * Initializes the tree model given a source param. Override
     * <code>createRoot</code> to create a root node and factory
     * appropriate for the source.
     */
    public void setSource(Object source) {
        stopWorker();
        DefaultMutableTreeNode root = createRoot(source);
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        model.setRoot(root);
        model.setAsksAllowsChildren(true);
    }

    /**
     * Stops the applet. Called on a system thread.
     */
    public void stop() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                stopWorker();
            }
        });
    }

    /**
     * Called from within the constructor to initialize the form.
     * The content of this method is generated by NetBeans.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        setLayout(new java.awt.BorderLayout());

        jScrollPane = new javax.swing.JScrollPane();
        jScrollPane.setPreferredSize(new java.awt.Dimension(320, 240));

        jTree = new javax.swing.JTree();
        jTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
                jTreeTreeCollapsed(evt);
            }

            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                jTreeTreeExpanded(evt);
            }
        }
        );
        jTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeValueChanged(evt);
            }
        }
        );
        jScrollPane.add(jTree);

        jScrollPane.setViewportView(jTree);
        add(jScrollPane, "Center");

        jPanel = new javax.swing.JPanel();
        jPanel.setLayout(new java.awt.BorderLayout());

        jlblStatus = new javax.swing.JLabel();
        jlblStatus.setToolTipText("Status");
        jlblStatus.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.CompoundBorder(
                        new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)),
                        new javax.swing.border.LineBorder(java.awt.Color.gray)),
                new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 2, 0, 0))));
        jlblStatus.setText("Idle");
        jPanel.add(jlblStatus, "Center");

        add(jPanel, "South");

    }//GEN-END:initComponents

    /**
     * Called when a node is expanded. Stops the active worker, if any,
     * and starts a new worker to create children for the expanded node.
     */
    private void jTreeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_jTreeTreeExpanded
        stopWorker();
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
        if (factory != null) {
            startWorker(factory, node);
        }
    }//GEN-LAST:event_jTreeTreeExpanded

    /**
     * Called when a node is collapsed. Stops the active worker, if any,
     * and removes all the children.
     */
    private void jTreeTreeCollapsed(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_jTreeTreeCollapsed
        stopWorker();
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
        node.removeAllChildren();
        ((DefaultTreeModel) jTree.getModel()).nodeStructureChanged(node);
        jlblStatus.setText("Collapsed " + node);
    }//GEN-LAST:event_jTreeTreeCollapsed

    /**
     * Updates the status line when a node is selected.
     */
    private void jTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeValueChanged
        Object node = evt.getPath().getLastPathComponent();
        String s = evt.isAddedPath() ? "Selected " + node : "";
        jlblStatus.setText(s);
    }//GEN-LAST:event_jTreeValueChanged


    /**
     * Given a node factory and an expanded node, starts a SwingWorker
     * to create children for the expanded node and insert them into
     * the tree.
     */
    protected void startWorker(final TreeNodeFactory fac,
                               final DefaultMutableTreeNode node) {
        final Object userObject = node.getUserObject();

        worker = new SwingWorker() {

            public void done() {
                /* Set the worker to null and stop the animation,
                   but only if we are the active worker. */
                if (worker == this) {
                    worker = null;
                }
                try {
                    /* Get the children created by the factory and
                       insert them into the local tree model. */
                    DefaultMutableTreeNode[] children =
                            (DefaultMutableTreeNode[]) get();
                    for (int i = 0; i < children.length; i++) {
                        node.insert(children[i], i);
                    }
                    DefaultTreeModel model =
                            (DefaultTreeModel) jTree.getModel();
                    model.nodeStructureChanged(node);
                    jlblStatus.setText("Expanded " + node);
                } catch (ExecutionException ex) {
                    jlblStatus.setText(ex.getLocalizedMessage());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                java.awt.Component pane = SwingUtilities.getRootPane(DynamicTree.this).getGlassPane();
                pane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                pane.setVisible(true);

            }

            @Override
            protected Object doInBackground() throws Exception {
                java.awt.Component pane = SwingUtilities.getRootPane(DynamicTree.this).getGlassPane();
                pane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                pane.setVisible(true);
                return fac.createChildren(userObject);
            }
        };

        /* Start worker, update status line, and start animation. */
        worker.execute();
        jlblStatus.setText("Expanding " + node + "...");
    }

    /**
     * Stops the active worker, if any.
     */
    protected void stopWorker() {
        if (worker != null) {
            worker.cancel(true);
            // worker set to null in finished
        }
    }

    /**
     * Given a source object, creates a root node for the local
     * tree model and sets the factory that will to be used to
     * create the children. The default implementation creates
     * a slow default factory.
     */
    protected DefaultMutableTreeNode createRoot(Object source) {
        TreeNodeFactory fac = new DefaultTreeNodeFactory();
        factory = new SlowTreeNodeFactory(fac, 1000);
        return new DefaultMutableTreeNode("Dynamic Tree", true);
    }


    /**
     * Launches this applet in a frame.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dynamic Tree");
        final DynamicTree applet = new DynamicTree() {
            // Install a slow file system factory. 
            protected DefaultMutableTreeNode createRoot(Object source) {
                String path = (source == null || source.equals(""))
                    ? System.getProperty("user.home") : source.toString();
                TreeNodeFactory fac = new FileSystemNodeFactory(path);
                factory = new SlowTreeNodeFactory(fac, 20);
                return new DefaultMutableTreeNode(path, true);
            }
        };
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                applet.stopWorker();
                System.exit(0);
            }
        });
        frame.getContentPane().add(applet, "Center");
        frame.pack();
        frame.setVisible(true);
    }

}