package uk.co.itstherules.marklog.editor.viewbuilder;

import uk.co.itstherules.marklog.editor.actionbuilder.MenuItemActionBuilder;
import uk.co.itstherules.marklog.string.VariableName;

import javax.swing.*;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class MenuItemBuilder implements Builder<JMenuItem> {

    private JMenuItem item;

    public MenuItemBuilder(String text) {
        item = new JMenuItem(text);
        item.setName(new VariableName().manipulate(text));
    }

    @Override public JMenuItem build() {
        return item;
    }

    public MenuItemBuilder withClickAction(MenuItemActionBuilder.ApplyChanged applyChanged) {
        when(item).hasBeenClicked(applyChanged);
        return this;
    }
}
