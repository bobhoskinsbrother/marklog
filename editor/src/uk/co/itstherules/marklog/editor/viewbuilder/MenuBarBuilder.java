package uk.co.itstherules.marklog.editor.viewbuilder;

import javax.swing.*;

public final class MenuBarBuilder implements Builder<JMenuBar>{

    private final JMenuBar bar;

    public MenuBarBuilder(String name) {
        bar = new JMenuBar();
        bar.setName(name);
    }

    public MenuBarBuilder add(JMenu menu) {
        bar.add(menu);
        return this;
    }

    public static MenuBarBuilder menuBar(String text) {
        return new MenuBarBuilder(text);
    }

    public static MenuBuilder menu(String text) {
        return new MenuBuilder(text);
    }

    @Override public JMenuBar build() {
        return bar;
    }
}
