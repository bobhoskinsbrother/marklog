package uk.co.itstherules.marklog.editor.viewbuilder;

import javax.swing.*;

public final class MenuBuilder implements Builder<JMenu> {

    private final JMenu menu;

    public MenuBuilder(String text) {
        menu = new JMenu(text);
    }

    public MenuBuilder add(JMenuItem item) {
        menu.add(item);
        return this;
    }

    public static MenuItemBuilder item(String text) {
        return new MenuItemBuilder(text);
    }

    @Override public JMenu build() {
        return menu;
    }
}
