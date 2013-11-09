package uk.co.itstherules.marklog.editor.viewbuilder;

import uk.co.itstherules.marklog.string.VariableName;

import javax.swing.*;

public final class LabelBuilder implements Builder<JLabel> {

    private JLabel item;

    public LabelBuilder(String text) {
        item = new JLabel(text);
        item.setName(new VariableName().manipulate(text));
    }

    public static LabelBuilder label(String text) {
        return new LabelBuilder(text);
    }

    public LabelBuilder withCenteredText() {
        item.setHorizontalAlignment(SwingConstants.CENTER);
        return this;
    }

    @Override public JLabel ok() {
        return item;
    }

}
