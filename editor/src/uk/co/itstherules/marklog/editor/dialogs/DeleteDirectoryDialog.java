package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogPanel;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class DeleteDirectoryDialog extends JDialog {

    private final MarklogApp app;
    private final File directory;
    private final MarklogPanel.MarklogController controller;

    public DeleteDirectoryDialog(MarklogApp app, MarklogPanel.MarklogController controller, File directory) {
        super(app, true);
        this.app = app;
        this.controller = controller;
        this.directory = directory;
        setLayout(new MigLayout("insets 10"));
        ButtonGroup group = new ButtonGroup();
        JRadioButton deleteUnderneath = new JRadioButton("Delete any file underneath this directory", true);
        JRadioButton moveUp = new JRadioButton("Move up any file underneath this directory");
        group.add(deleteUnderneath);
        group.add(moveUp);
        JButton deleteButton = new JButton("Delete");
        when(deleteButton).hasBeenClicked(deleteDirectory());
        setPreferredSize(new Dimension(475, 250));
        setLocationRelativeTo(this.app);
        add(new JLabel("<html><h2>Delete Directory</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        add(new JLabel("Directory Name: " + this.directory.getName()), "wrap");
        add(deleteUnderneath, "wrap");
        add(moveUp, "wrap");
        add(deleteButton);
        pack();
        setVisible(true);
    }

    private ButtonActionBuilder.ApplyChanged deleteDirectory() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                controller.delete(directory);
                dispose();
            }
        };

    }
}