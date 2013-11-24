package uk.co.itstherules.marklog.editor.dialogs;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.filesystem.DirectoryChooserComponent;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;
import static uk.co.itstherules.marklog.editor.viewbuilder.LabelBuilder.label;

public class NewProjectDialog extends ProjectDialog {

    public NewProjectDialog(MarklogApp app, MarklogController controller) {
        super(app, controller, new ProjectConfiguration());
    }

    protected void addNewItems(PanelBuilder b) {
        DirectoryChooserComponent directoryChooser = new DirectoryChooserComponent(configuration.getDirectory());
        when(directoryChooser).fileHasBeenChosen(applyToProjectDirectory());
        b.add(label("Project Directory").ok()).add(directoryChooser);
    }

}