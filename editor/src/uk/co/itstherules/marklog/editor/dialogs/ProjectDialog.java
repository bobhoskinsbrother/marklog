package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.DirectoryChooserActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.TextFieldActionBuilder;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder;
import uk.co.itstherules.marklog.sync.FtpClient;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static uk.co.itstherules.marklog.editor.viewbuilder.ButtonBuilder.button;
import static uk.co.itstherules.marklog.editor.viewbuilder.LabelBuilder.label;
import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;
import static uk.co.itstherules.marklog.editor.viewbuilder.TextFieldBuilder.textField;

public abstract class ProjectDialog extends JDialog {

    private final MarklogController controller;
    protected ProjectConfiguration configuration;

    public ProjectDialog(MarklogApp app, MarklogController controller, ProjectConfiguration configuration) {
        super(app, true);
        this.setName("projectDialog");
        this.controller = controller;
        this.configuration = configuration;
        setLayout(new MigLayout("insets 10"));
        JTextField projectNameTextField = textFieldWhenTextChanged(applyToProjectName(), "projectName", configuration.getName());
        JTextField ftpHostTextField = textFieldWhenTextChanged(applyToFtpHost(), "ftpHost", configuration.getFtpHost());
        JTextField ftpPortTextField = textFieldWhenTextChanged(applyToFtpPort(), "ftpPort", String.valueOf(configuration.getFtpPort()));
        JTextField ftpWorkingDirectoryTextField = textFieldWhenTextChanged(applyToFtpWorkingDirectory(), "ftpWorkingDirectory", configuration.getFtpWorkingDirectory());
        JTextField ftpUserNameTextField = textFieldWhenTextChanged(applyToFtpUsername(), "ftpUserName", configuration.getFtpUsername());
        JTextField ftpPasswordTextField = textFieldWhenTextChanged(applyToFtpPassword(), "ftpPassword", configuration.getFtpPassword());
        JButton saveButton = button("Save Project").withClickAction(verifyAndSaveProjectFile()).ok();
        JButton testConnectionButton = button("Test Connection").withClickAction(testConnection()).ok();
        add(new JLabel("<html><h2>Project</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        final PanelBuilder b = panel("projectPanel").ofSize(475, 350).withLayout("wrap 2", "[  left  ][ right ]", "[ center ]")
                .add(label("Project Name").ok()).add(projectNameTextField);
                addNewItems(b);
                b.add(label("<html><h3>FTP Details</h3>").ok()).add(label("").ok())
                .add(label("Host").ok()).add(ftpHostTextField)
                .add(label("Port").ok()).add(ftpPortTextField)
                .add(label("Working Directory").ok()).add(ftpWorkingDirectoryTextField)
                .add(label("Username").ok()).add(ftpUserNameTextField)
                .add(label("Password").ok()).add(ftpPasswordTextField)
                .add(saveButton).add(testConnectionButton);
        add(b.ok());
        pack();
        setLocationRelativeTo(app);
        setVisible(true);

    }

    protected abstract void addNewItems(PanelBuilder b);

    protected JTextField textFieldWhenTextChanged(TextFieldActionBuilder.ApplyChanged applyChanged, String name, String text) {
        return textField(text).ofSize(340, 30).withName(name).withTextChangedAction(applyChanged).ok();
    }

    protected ButtonActionBuilder.ApplyChanged verifyAndSaveProjectFile() {
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
        String message = "Please fill in all of the fields with the correct info";
        JOptionPane.showMessageDialog(null, message, "Information Missing or Incorrect", JOptionPane.ERROR_MESSAGE);
    }

    protected ButtonActionBuilder.ApplyChanged testConnection() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if (!configuration.isFtpInformationValid()) {
                    informationMissingDialog();
                }
                try {
                    final FtpClient client = new FtpClient(configuration.getFtpHost(), configuration.getFtpPort(), configuration.getFtpWorkingDirectory(), configuration.getFtpUsername(), configuration.getFtpPassword());
                    client.close();
                    JOptionPane.showMessageDialog(null, "Connected successfully to the host with the credentials provided", "Connected Successfully", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Unsuccessful connection: " + e.getMessage(), "Connected Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    protected DirectoryChooserActionBuilder.ApplyChanged applyToProjectDirectory() {
        return new DirectoryChooserActionBuilder.ApplyChanged() {
            @Override public void apply(File file) {
                configuration.setDirectory(file.getAbsoluteFile());
            }
        };
    }

    protected TextFieldActionBuilder.ApplyChanged applyToProjectName() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setName(textFieldText);
            }
        };
    }

    protected TextFieldActionBuilder.ApplyChanged applyToFtpPort() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                if(!"".equals(textFieldText)) {
                    configuration.setFtpPort(Integer.parseInt(textFieldText));
                }
            }
        };
    }

    protected TextFieldActionBuilder.ApplyChanged applyToFtpHost() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setFtpHost(textFieldText);
            }
        };
    }

    protected TextFieldActionBuilder.ApplyChanged applyToFtpWorkingDirectory() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setFtpWorkingDirectory(textFieldText);
            }
        };
    }

    protected TextFieldActionBuilder.ApplyChanged applyToFtpUsername() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setFtpUsername(textFieldText);
            }
        };
    }

    protected TextFieldActionBuilder.ApplyChanged applyToFtpPassword() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setFtpPassword(textFieldText);
            }
        };
    }

}