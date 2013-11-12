package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class DeleteDirectoryDialog extends JDialog {

    private final File directory;
    private final MarklogController controller;
    private final JRadioButton moveUp;

    public DeleteDirectoryDialog(MarklogApp app, MarklogController controller, File directory) {
        super(app, true);
        this.controller = controller;
        this.directory = directory;
        setLayout(new MigLayout("insets 10"));
        ButtonGroup group = new ButtonGroup();
        JRadioButton deleteUnderneath = new JRadioButton("Delete any files underneath this directory", true);
        deleteUnderneath.setName("deleteFilesUnderneath");
        moveUp = new JRadioButton("Move up any files underneath this directory");
        moveUp.setName("moveUpFiles");
        group.add(deleteUnderneath);
        group.add(moveUp);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setName("deleteDirectory");
        when(deleteButton).hasBeenClicked(deleteDirectory());
        setPreferredSize(new Dimension(475, 250));
        add(new JLabel("<html><h2>Delete Directory</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        add(new JLabel("Directory Name: " + this.directory.getName()), "wrap");
        add(deleteUnderneath, "wrap");
        add(moveUp, "wrap");
        add(deleteButton);
        pack();
        setLocationRelativeTo(app);
        setVisible(true);
    }

    private ButtonActionBuilder.ApplyChanged deleteDirectory() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                controller.deleteDirectory(directory, moveUp.isSelected());
                dispose();
            }
        };

    }
}