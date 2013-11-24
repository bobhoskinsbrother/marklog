package uk.co.itstherules.marklog.editor.viewbuilder;

import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.string.VariableName;

import javax.swing.*;
import java.awt.*;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class ButtonBuilder implements Builder<JButton> {

    private JButton item;

    public ButtonBuilder(String text) {
        item = new JButton(text);
        item.setName(new VariableName().manipulate(text));
    }

    public ButtonBuilder(Icon icon, String text) {
        item = new JButton(icon);
        item.setToolTipText(text);
        item.setName(new VariableName().manipulate(text));
    }

    public static ButtonBuilder button(String text) {
        return new ButtonBuilder(text);
    }

    public static ButtonBuilder button(Icon icon, String tooltipText) {
        return new ButtonBuilder(icon, tooltipText);
    }

    @Override public JButton ok() {
        return item;
    }

    public ButtonBuilder withClickAction(ButtonActionBuilder.ApplyChanged applyChanged) {
        when(item).hasBeenClicked(applyChanged);
        return this;
    }

    public ButtonBuilder ofSize(int width, int height) {
        final Dimension size = new Dimension(width, height);
        item.setSize(size);
        item.setPreferredSize(size);
        item.setMaximumSize(size);
        item.setMinimumSize(size);
        return this;
    }
}
