package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.TextFieldActionBuilder;
import uk.co.itstherules.marklog.editor.filesystem.Validator;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;
import static uk.co.itstherules.marklog.editor.viewbuilder.TextFieldBuilder.textField;

public final class RenameFileDialog extends JDialog {

    private final File oldFile;
    private final MarklogController controller;
    private String newFileName;

    public RenameFileDialog(MarklogApp app, MarklogController controller, File oldFile) {
        super(app, true);
        setName("renameFileDialog");
        setPreferredSize(new Dimension(475, 250));
        this.controller = controller;
        this.oldFile = oldFile;
        String oldFileName = oldFile.getName();
        setLayout(new MigLayout("insets 10"));
        JButton renameButton = new JButton("Rename");
        renameButton.setName("renameButton");
        when(renameButton).hasBeenClicked(renameFile());
        add(new JLabel("<html><h2>Rename File</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        add(new JLabel("File Name: " + oldFile.getName()), "wrap");
        add(textField(oldFileName).withName("newFileName").ofSize(340, 30).withTextChangedAction(applyToFileName()).ok(), "wrap");
        add(renameButton);
        pack();
        setLocationRelativeTo(app);
        setVisible(true);
    }

    private TextFieldActionBuilder.ApplyChanged applyToFileName() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                newFileName = textFieldText;
            }
        };
    }

    private ButtonActionBuilder.ApplyChanged renameFile() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if (!Validator.isLegalFileName(newFileName)) {
                    String message = "Please fill in a legal file name\nNo weird characters please";
                    JOptionPane.showMessageDialog(null, message, "No or illegal file name supplied", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (controller.renameFile(oldFile, newFileName)) {
                        dispose();
                    }
                }
            }
        };

    }
}