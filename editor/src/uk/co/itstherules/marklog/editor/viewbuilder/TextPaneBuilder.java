package uk.co.itstherules.marklog.editor.viewbuilder;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

public final class TextPaneBuilder implements Builder<JTextPane> {

    private JTextPane item;

    public TextPaneBuilder(String text) {
        item = new JTextPane();
        item.setText(text);
    }

    public static TextPaneBuilder textPane(String text) {
        return new TextPaneBuilder(text);
    }

    public static TextPaneBuilder textPane() {
        return new TextPaneBuilder("");
    }

    public TextPaneBuilder name(String name) {
        item.setName(name);
        return this;
    }

    @Override public JTextPane ok() {
        return item;
    }

    public TextPaneBuilder ofSize(int width, int height) {
        item.setPreferredSize(new Dimension(width, height));
        return this;
    }

    public TextPaneBuilder append(String text, Color color) {
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontFamily, "Lucida Console");
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        int len = item.getDocument().getLength();
        item.setCaretPosition(len);
        item.setCharacterAttributes(attributeSet, false);
        item.replaceSelection(text);
        return this;
    }

    public TextPaneBuilder append(String text) {
        return append(text, Color.BLACK);
    }
}
