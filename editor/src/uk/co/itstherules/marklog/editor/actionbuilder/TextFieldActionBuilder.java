package uk.co.itstherules.marklog.editor.actionbuilder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextFieldActionBuilder {

    private final JTextField textField;

    public TextFieldActionBuilder(JTextField textField) {
        this.textField = textField;
    }

    public TextFieldActionBuilder textHasChanged(final ApplyChanged applyChanged) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void insertUpdate(DocumentEvent e) { update(); }

            public void update() {
                String textFieldText = textField.getText();
                applyChanged.apply(textFieldText);
            }
        });
        return this;
    }

    public interface ApplyChanged {
        void apply(String text);
    }
}
