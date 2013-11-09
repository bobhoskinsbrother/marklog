package uk.co.itstherules.marklog.editor;

import uk.co.itstherules.marklog.editor.viewbuilder.Builder;

import javax.swing.*;
import java.awt.*;

public final class BlankPanelBuilder implements Builder<JPanel> {

    private final JPanel panel;

    public BlankPanelBuilder() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setName("blankPanel");
        JLabel label = new JLabel("Please select or create a new project");
        label.setHorizontalAlignment( SwingConstants.CENTER );
        panel.add(label, fillTheSpaceConstraints());
    }

    private static GridBagConstraints fillTheSpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        return constraints;
    }

    @Override public JPanel build() {
        return panel;
    }
}
