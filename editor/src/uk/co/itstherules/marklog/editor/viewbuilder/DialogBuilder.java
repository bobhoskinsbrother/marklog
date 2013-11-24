package uk.co.itstherules.marklog.editor.viewbuilder;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.actionbuilder.DialogActionBuilder;

import javax.swing.*;
import java.awt.*;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;

public final class DialogBuilder implements Builder<JDialog> {

    private final JFrame parent;
    private final JDialog item;

    public DialogBuilder(JFrame parent, boolean isModal) {
        this.parent = parent;
        item = new JDialog(parent,isModal);
    }

    public static DialogBuilder modalDialog(JFrame parent) {
        return new DialogBuilder(parent, true);
    }

    @Override public JDialog ok() {
        return item;
    }

    public DialogBuilder ofSize(int width, int height) {
        final Dimension dimension = new Dimension(width, height);
        item.setPreferredSize(dimension);
        item.setSize(dimension);
        return this;
    }

    private void layout(LayoutManager layoutManager) {item.setLayout(layoutManager);}

    public DialogBuilder add(Component component, String details) {
        item.add(component, details);
        return this;
    }

    public DialogBuilder add(Component component) {
        item.add(component);
        return this;
    }

    public DialogBuilder withLayout(String layoutInstructions) {
        layout(new MigLayout(layoutInstructions));
        return this;
    }

    public DialogBuilder withTitle(String title) {
        item.setTitle(title);
        return this;
    }

    public DialogBuilder notResizable() {
        item.setResizable(false);
        return this;
    }

    public DialogBuilder centerOnParent() {
        item.setLocationRelativeTo(parent);
        return this;
    }

    public DialogBuilder withCloseAction(DialogActionBuilder.ApplyChanged applyChanged) {
        when(item).windowIsClosing(applyChanged);
        return this;
    }

}
