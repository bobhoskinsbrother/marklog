package uk.co.itstherules.marklog.editor.viewbuilder;

import uk.co.itstherules.marklog.editor.actionbuilder.TextFieldActionBuilder;

import javax.swing.*;
import java.awt.*;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class TextFieldBuilder implements Builder<JTextField> {

    private JTextField item;

    public TextFieldBuilder(String text) {
        item = new JTextField(text);
    }

    public static TextFieldBuilder textField(String text) {
        return new TextFieldBuilder(text);
    }

    public static TextFieldBuilder textField() {
        return new TextFieldBuilder("");
    }

    public TextFieldBuilder name(String name) {
        item.setName(name);
        return this;
    }

    public TextFieldBuilder value(String value) {
        item.setText(value);
        return this;
    }

    public TextFieldBuilder withCenteredText() {
        item.setHorizontalAlignment(SwingConstants.CENTER);
        return this;
    }

    @Override public JTextField ok() {
        return item;
    }

    public TextFieldBuilder ofSize(int width, int height) {
        item.setPreferredSize(new Dimension(width, height));
        return this;
    }

    public TextFieldBuilder withTextChangedAction(TextFieldActionBuilder.ApplyChanged applyChanged) {
        when(item).textHasChanged(applyChanged);
        return this;
    }
}
