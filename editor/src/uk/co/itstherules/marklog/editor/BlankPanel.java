package uk.co.itstherules.marklog.editor;

import javax.swing.*;
import java.awt.*;

public final class BlankPanel extends JPanel {

    public BlankPanel() {
        setLayout(new GridBagLayout());
        setName("blankPanel");
        JLabel label = new JLabel("Please select or create a new project");
        label.setHorizontalAlignment( SwingConstants.CENTER );
        add(label, fillTheSpaceConstraints());
    }

    private static GridBagConstraints fillTheSpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        return constraints;
    }

}
