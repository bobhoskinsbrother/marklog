package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.TextFieldActionBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class AddNewDirectoryDialog extends JDialog {

    private final MarklogApp app;
    private final File directory;
    private String directoryName = "";

    public AddNewDirectoryDialog(MarklogApp app, File directory) {
        super(app, true);
        this.app = app;
        this.directory = directory;
        setLayout(new MigLayout("insets 10"));
        JTextField directoryNameTextField = new JTextField();
        directoryNameTextField.setPreferredSize(new Dimension(300, 30));
        JButton createButton = new JButton("Create");
        when(directoryNameTextField).textHasChanged(applyToDirectoryName());
        when(createButton).hasBeenClicked(verifyAndCreateDirectory());
        paintView(directoryNameTextField, createButton);
    }

    private void paintView(JTextField directoryNameTextField, JButton createButton) {
        setPreferredSize(new Dimension(475, 200));
        setLocationRelativeTo(app);
        add(new JLabel("<html><h2>New Directory</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        add(new JLabel("Directory Name:"));
        add(directoryNameTextField, "gapleft 10, wrap");
        add(createButton);
        pack();
        setVisible(true);
    }

    private ButtonActionBuilder.ApplyChanged verifyAndCreateDirectory() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if ("".equals(directoryName) && isIllegalDirectoryName()) {
                    String message = "Please fill in a legal directory name\nNo weird characters please";
                    JOptionPane.showMessageDialog(null, message, "No directory name supplied", JOptionPane.ERROR_MESSAGE);
                } else {
                    File directory = new File(AddNewDirectoryDialog.this.directory, directoryName);
                    directory.mkdirs();
                    dispose();
                }
            }

            private boolean isIllegalDirectoryName() {
                char[] illegal = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
                for (int i = 0; i < illegal.length; i++) {
                    char illegalChar = illegal[i];
                    if (directoryName.contains(Character.toString(illegalChar))) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private TextFieldActionBuilder.ApplyChanged applyToDirectoryName() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                directoryName = textFieldText;
            }
        };
    }

}