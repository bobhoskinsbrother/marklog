package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.DirectoryChooserActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.TextFieldActionBuilder;
import uk.co.itstherules.marklog.editor.filesystem.DirectoryChooserComponent;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder;
import uk.co.itstherules.marklog.sync.FtpClient;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;
import static uk.co.itstherules.marklog.editor.viewbuilder.ButtonBuilder.button;
import static uk.co.itstherules.marklog.editor.viewbuilder.LabelBuilder.label;
import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;
import static uk.co.itstherules.marklog.editor.viewbuilder.TextFieldBuilder.textField;

public class NewProjectDialog extends JDialog {

    private final MarklogController controller;
    private ProjectConfiguration configuration;

    public NewProjectDialog(MarklogApp app, MarklogController controller) {
        super(app, true);
        this.controller = controller;
        this.configuration = new ProjectConfiguration();
        setLayout(new MigLayout("insets 10"));
        JTextField projectNameTextField = textFieldWhenTextChanged(applyToProjectName(), "projectName", configuration.getName());
        JTextField ftpHostTextField = textFieldWhenTextChanged(applyToFtpHost(), "ftpHost", configuration.getFtpHost());
        JTextField ftpUserNameTextField = textFieldWhenTextChanged(applyToFtpUsername(), "ftpUserName", configuration.getFtpUsername());
        JTextField ftpPasswordTextField = textFieldWhenTextChanged(applyToFtpPassword(), "ftpPassword", configuration.getFtpPassword());
        DirectoryChooserComponent directoryChooser = new DirectoryChooserComponent(configuration.getDirectory());
        JButton saveButton = button("Save Project").withClickAction(verifyAndSaveProjectFile()).ok();
        JButton testConnectionButton = button("Test Connection").withClickAction(testConnection()).ok();
        when(directoryChooser).fileHasBeenChosen(applyToProjectDirectory());
        add(new JLabel("<html><h2>Project</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        final PanelBuilder b = panel("projectPanel").ofSize(475, 250).withLayout("wrap 2", "[  left  ][ right ]", "[ center ]").add(label("Project Name").ok()).add(projectNameTextField).add(label("Path for Project").ok()).add(directoryChooser).add(label("FTP Host").ok()).add(ftpHostTextField).add(label("FTP Username").ok()).add(ftpUserNameTextField).add(label("FTP Password").ok()).add(ftpPasswordTextField).add(saveButton).add(testConnectionButton);
        add(b.ok());
        pack();
        setLocationRelativeTo(app);
        setVisible(true);
    }

    private JTextField textFieldWhenTextChanged(TextFieldActionBuilder.ApplyChanged applyChanged, String name, String text) {
        return textField(text).ofSize(340, 30).withName(name).withTextChangedAction(applyChanged).ok();
    }

    private ButtonActionBuilder.ApplyChanged verifyAndSaveProjectFile() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if (!configuration.isValid()) {
                    informationMissingDialog();
                } else {
                    configuration.save();
                    controller.newMarklogProject(configuration);
                    dispose();
                }
            }
        };
    }

    private void informationMissingDialog() {
        String message = "Please fill in all of the fields";
        JOptionPane.showMessageDialog(null, message, "Information Missing", JOptionPane.ERROR_MESSAGE);
    }

    private ButtonActionBuilder.ApplyChanged testConnection() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if (!configuration.isFtpInformationValid()) {
                    informationMissingDialog();
                }
                try {
                    final FtpClient client = new FtpClient(configuration.getFtpHost(), 21, configuration.getFtpUsername(), configuration.getFtpPassword());
                    client.close();
                    JOptionPane.showMessageDialog(null, "Connected successfully to the host with the credentials provided", "Connected Successfully", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Unsuccessful connection: " + e.getMessage(), "Connected Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private DirectoryChooserActionBuilder.ApplyChanged applyToProjectDirectory() {
        return new DirectoryChooserActionBuilder.ApplyChanged() {
            @Override public void apply(File file) {
                configuration.setDirectory(file.getAbsoluteFile());
            }
        };
    }

    private TextFieldActionBuilder.ApplyChanged applyToProjectName() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setName(textFieldText);
            }
        };
    }

    private TextFieldActionBuilder.ApplyChanged applyToFtpHost() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setFtpHost(textFieldText);
            }
        };
    }

    private TextFieldActionBuilder.ApplyChanged applyToFtpUsername() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setFtpUsername(textFieldText);
            }
        };
    }

    private TextFieldActionBuilder.ApplyChanged applyToFtpPassword() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setFtpPassword(textFieldText);
            }
        };
    }

}