package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.DirectoryChooserActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.TextFieldActionBuilder;
import uk.co.itstherules.marklog.editor.filesystem.DirectoryChooserComponent;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;
import uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder;

import javax.swing.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;
import static uk.co.itstherules.marklog.editor.viewbuilder.ButtonBuilder.button;
import static uk.co.itstherules.marklog.editor.viewbuilder.LabelBuilder.label;
import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;
import static uk.co.itstherules.marklog.editor.viewbuilder.TextFieldBuilder.textField;

public final class NewProjectDialog extends JDialog {

    private final MarklogController controller;
    private ProjectConfigurationModel configuration;

    public NewProjectDialog(MarklogApp app, MarklogController controller) {
        super(app, true);
        this.controller = controller;
        configuration = new ProjectConfigurationModel();
        setLayout(new MigLayout("insets 10"));
        JTextField projectNameTextField = textField().ofSize(300, 30).withTextChangedAction(applyToProjectName()).ok();
        JTextField ftpUrlTextField = textField().ofSize(300, 30).withTextChangedAction(applyToFtpUrl()).ok();
        JTextField ftpUserNameTextField = textField().ofSize(300, 30).withTextChangedAction(applyToFtpUsername()).ok();
        JTextField ftpPasswordTextField = textField().ofSize(300, 30).withTextChangedAction(applyToFtpPassword()).ok();



        DirectoryChooserComponent directoryChooser = new DirectoryChooserComponent();
        JButton createButton = button("Create Project").withClickAction(verifyAndCreateProjectFile()).ok();

        when(directoryChooser).fileHasBeenChosen(applyToProjectDirectory());
        setLocationRelativeTo(app);

        add(new JLabel("<html><h2>New Project</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        final PanelBuilder b =
            panel("b").ofSize(475, 250)
                .withLayout("wrap 2",
                            "[  left  ][ right ]",
                            "[ center ]")
                .add(label("Project Name").ok()).add(projectNameTextField)
                .add(label("Path for Project").ok()).add(directoryChooser)
                .add(label("FTP Url").ok()).add(ftpUrlTextField)
                .add(label("FTP Username").ok()).add(ftpUserNameTextField)
                .add(label("FTP Password").ok()).add(ftpPasswordTextField)
                .add(createButton)
                ;
        add(b.ok());
        pack();
        setVisible(true);
    }

    private ButtonActionBuilder.ApplyChanged verifyAndCreateProjectFile() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if (!configuration.isValid()) {
                    String message = "Please fill in all of the fields";
                    JOptionPane.showMessageDialog(null, message, "Information Missing", JOptionPane.ERROR_MESSAGE);
                } else {
                    configuration.save();
                    controller.newMarklogProject(configuration);
                    dispose();
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

    private TextFieldActionBuilder.ApplyChanged applyToFtpUrl() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                configuration.setFtpUrl(textFieldText);
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