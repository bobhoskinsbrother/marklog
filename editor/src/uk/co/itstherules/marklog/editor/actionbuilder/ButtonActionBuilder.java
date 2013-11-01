package uk.co.itstherules.marklog.editor.actionbuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonActionBuilder {

    private final JButton button;

    public ButtonActionBuilder(JButton button) {
        this.button = button;
    }

    public ButtonActionBuilder hasBeenClicked(final ApplyChanged applyChanged) {
        button.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent actionEvent) {
                applyChanged.apply();
            }
        });
        return this;
    }

    public interface ApplyChanged {
        void apply();
    }
}
