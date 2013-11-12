package uk.co.itstherules.marklog.editor.dialogs;

import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;

public final class NewProjectDialog extends ProjectDialog {

    public NewProjectDialog(MarklogApp app, MarklogController controller) {
        super(app, controller, new ProjectConfiguration());
    }
}