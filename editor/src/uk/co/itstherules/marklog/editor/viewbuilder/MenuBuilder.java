package uk.co.itstherules.marklog.editor.viewbuilder;

import javax.swing.*;

public final class MenuBuilder implements Builder<JMenu> {

    private final JMenu menu;

    public MenuBuilder(String text) {
        menu = new JMenu(text);
    }

    public static MenuBuilder menu(String text) {
        return new MenuBuilder(text);
    }

    public MenuBuilder add(JMenuItem item) {
        menu.add(item);
        return this;
    }

    @Override public JMenu ok() {
        return menu;
    }
}
