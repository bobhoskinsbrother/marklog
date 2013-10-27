package uk.co.itstherules.marklog.editor.actionbuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuItemActionBuilder {

    private final JMenuItem menuItem;

    public MenuItemActionBuilder(JMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public MenuItemActionBuilder hasBeenClicked(final ApplyChanged applyChanged) {
        menuItem.addActionListener(new ActionListener() {
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
