package components;
 
/*
 * This code is based on an example provided by Richard Stanford, 
 * a tutorial reader.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DynamicTreeDemo extends JPanel {
    private int newNodeSuffix = 1;
    private static String REFRESH_COMMAND = "refresh";

    private DynamicTree treePanel;

    public DynamicTreeDemo() {
        super(new BorderLayout());

        //Lay everything out.
        String rootPath = System.getProperty("user.dir");
        if(rootPath == null || rootPath.trim().isEmpty()){
            throw new RuntimeException("Please set up user.dir before starting!");
        }
        System.out.print("User dir"+rootPath);
        treePanel = new DynamicTree(rootPath);
        treePanel.setPreferredSize(new Dimension(300, 150));

        add(treePanel, BorderLayout.CENTER);
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("DynamicTreeDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        DynamicTreeDemo newContentPane = new DynamicTreeDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}