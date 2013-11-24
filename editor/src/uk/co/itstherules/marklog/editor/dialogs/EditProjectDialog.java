package uk.co.itstherules.marklog.editor.dialogs;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder;

public class EditProjectDialog extends ProjectDialog {

    public EditProjectDialog(MarklogApp app, MarklogController controller, ProjectConfiguration configuration) {
        super(app, controller, configuration);
    }

    @Override protected void addNewItems(PanelBuilder b) {
    }

}