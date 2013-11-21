package uk.co.itstherules.marklog.editor.viewbuilder;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class PanelBuilder implements Builder<JPanel> {

    private JPanel item;

    public PanelBuilder(String text) {
        item = new JPanel();
        item.setName(text);
    }

    public static PanelBuilder panel(String text) {
        return new PanelBuilder(text);
    }

    @Override public JPanel ok() {
        return item;
    }

    public PanelBuilder borderLayout() {
        layout(new BorderLayout());
        return this;
    }

    public PanelBuilder addFullyExpanded(Component component) {
        layout(new GridBagLayout());
        item.add(component, fillTheSpaceConstraints());
        return this;
    }

    public PanelBuilder ofSize(int width, int height) {
        item.setPreferredSize(new Dimension(width, height));
        return this;
    }

    private void layout(LayoutManager layoutManager) {item.setLayout(layoutManager);}

    private GridBagConstraints fillTheSpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        return constraints;

    }

    public PanelBuilder add(Component component, String details) {
        item.add(component, details);
        return this;
    }

    public PanelBuilder add(Component component) {
        item.add(component);
        return this;
    }

    public PanelBuilder withLayout(String layoutInstructions, String columnLayout, String rowLayout) {
        layout(new MigLayout(layoutInstructions,columnLayout,rowLayout));
        return this;
    }
}
