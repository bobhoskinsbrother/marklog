package uk.co.itstherules.marklog.editor;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.DirectoryChooserActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.TextFieldActionBuilder;
import uk.co.itstherules.marklog.editor.filesystem.DirectoryChooserComponent;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class NewProjectDialog extends JDialog {

    private final MarklogPanel.MarklogController marklogController;
    private String projectName;
    private String projectDirectory;

    public NewProjectDialog(MarklogApp marklogApp, MarklogPanel.MarklogController marklogController) {
        super(marklogApp, true);
        this.marklogController = marklogController;
        projectName = "";
        projectDirectory = "";
        setLayout(new MigLayout("insets 10"));
        JTextField projectNameTextField = new JTextField();
        projectNameTextField.setPreferredSize(new Dimension(300, 30));
        DirectoryChooserComponent directoryChooser = new DirectoryChooserComponent();
        JButton createButton = new JButton("Create");

        when(projectNameTextField).textHasChanged(applyToProjectName());
        when(directoryChooser).fileHasBeenChosen(applyToProjectDirectory());
        when(createButton).hasBeenClicked(verifyAndCreateProjectFile());

        setPreferredSize(new Dimension(475, 250));
        setLocationRelativeTo(marklogApp);
        add(new JLabel("<html><h2>New Project</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        add(new JLabel("Project Name:"));
        add(projectNameTextField, "gapleft 10, wrap");
        add(new JLabel("Path for project:"));
        add(directoryChooser, "gapleft 10, wrap");
        add(createButton);
        pack();
        setVisible(true);
    }

    private ButtonActionBuilder.ApplyChanged verifyAndCreateProjectFile() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if ("".equals(projectDirectory) || "".equals(projectName)) {
                    String message = "Please fill in both the project name\nand select a directory";
                    JOptionPane.showMessageDialog(null, message, "No name or directory supplied", JOptionPane.ERROR_MESSAGE);
                } else {
                    ProjectConfigurationModel configuration = new ProjectConfigurationModel(new File(projectDirectory), projectName);
                    configuration.save();
                    marklogController.newMarklogEditor(configuration);
                    dispose();
                }
            }
        };
    }

    private DirectoryChooserActionBuilder.ApplyChanged applyToProjectDirectory() {
        return new DirectoryChooserActionBuilder.ApplyChanged() {
            @Override public void apply(File file) {
                projectDirectory = file.getAbsolutePath();
            }
        };
    }

    private TextFieldActionBuilder.ApplyChanged applyToProjectName() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                projectName = textFieldText;
            }
        };
    }

}