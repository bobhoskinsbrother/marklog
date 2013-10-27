package uk.co.itstherules.marklog.editor.markdown;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class MarkdownEditorPanel extends JPanel {


    public MarkdownEditorPanel(File file) {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        setLayout(layout);
        setPreferredSize(new Dimension(1024, 720));
        add(new MarkdownAndHtmlPanel(file), constraints);
    }
}