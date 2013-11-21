package uk.co.itstherules.marklog.editor.viewbuilder;

import javax.swing.*;
import java.awt.*;

public final class ScrollerBuilder implements Builder<JScrollPane> {

    private JScrollPane item;

    public ScrollerBuilder(Component component) {
        item = new JScrollPane(component);
    }

    public static ScrollerBuilder scroller(Component component) {
        return new ScrollerBuilder(component);
    }

    @Override public JScrollPane ok() {
        return item;
    }

    public ScrollerBuilder ofSize(int width, int height) {
        final Dimension d = new Dimension(width, height);
        item.setPreferredSize(d);
        item.setSize(d);
        return this;
    }

    public ScrollerBuilder add(Component component) {
        item.add(component);
        return this;
    }
}
