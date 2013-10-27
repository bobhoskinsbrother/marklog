package uk.co.itstherules.marklog.editor.markdown;

import javax.swing.*;
import java.io.File;

public final class MarkdownEditorApp extends JFrame {

    public MarkdownEditorApp(File file) {
        super("Marklog Editor - " + file.getAbsolutePath());
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(new MarkdownEditorPanel(file));
        pack();
        setVisible(true);
    }
}