package uk.co.itstherules.marklog.editor.viewbuilder;

import javax.swing.*;
import java.awt.*;

public final class TextAreaBuilder implements Builder<JTextArea> {

    private JTextArea item;

    public TextAreaBuilder(String text) {
        item = new JTextArea(text);
    }

    public static TextAreaBuilder textArea(String text) {
        return new TextAreaBuilder(text);
    }

    public static TextAreaBuilder textArea() {
        return new TextAreaBuilder("");
    }

    public TextAreaBuilder name(String name) {
        item.setName(name);
        return this;
    }

    @Override public JTextArea ok() {
        return item;
    }

    public TextAreaBuilder ofSize(int width, int height) {
        item.setPreferredSize(new Dimension(width, height));
        return this;
    }

}
