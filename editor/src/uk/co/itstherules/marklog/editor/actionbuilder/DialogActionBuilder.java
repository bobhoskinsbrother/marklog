package uk.co.itstherules.marklog.editor.actionbuilder;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogActionBuilder {

    private final JDialog dialog;

    public DialogActionBuilder(JDialog dialog) {
        this.dialog = dialog;
    }

    public DialogActionBuilder windowIsClosing(final ApplyChanged applyChanged) {
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) { }

            public void windowClosing(WindowEvent e) {
                applyChanged.apply();
            }
        });
        return this;
    }

    public interface ApplyChanged {
        void apply();
    }
}
