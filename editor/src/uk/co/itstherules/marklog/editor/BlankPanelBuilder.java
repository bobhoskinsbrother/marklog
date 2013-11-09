package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.viewbuilder.Builder;

import javax.swing.*;

import static uk.co.itstherules.marklog.editor.viewbuilder.LabelBuilder.label;
import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;

public final class BlankPanelBuilder implements Builder<JPanel> {

    private final JPanel panel;

    public BlankPanelBuilder() {
        panel = panel("blankPanel").addFullyExpanded(
                label("Please select a project or create one").withCenteredText().ok()
        ).ok();
    }

    public static BlankPanelBuilder blankPanel(){
        return new BlankPanelBuilder();
    }

    @Override public JPanel ok() {
        return panel;
    }
}
