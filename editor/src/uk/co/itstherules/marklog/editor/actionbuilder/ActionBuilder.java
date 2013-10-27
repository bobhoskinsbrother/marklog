package uk.co.itstherules.marklog.editor.actionbuilder;

import uk.co.itstherules.marklog.editor.filesystem.DirectoryChooserComponent;

import javax.swing.*;

public final class ActionBuilder {

    private ActionBuilder() {}

    public static TextFieldActionBuilder when(JTextField textField) {
        return new TextFieldActionBuilder(textField);
    }

    public static ButtonActionBuilder when(JButton button) {
        return new ButtonActionBuilder(button);
    }

    public static DirectoryChooserActionBuilder when(DirectoryChooserComponent directoryChooser) {
        return new DirectoryChooserActionBuilder(directoryChooser);
    }

    public static MenuItemActionBuilder when(JMenuItem menuItem) {
        return new MenuItemActionBuilder(menuItem);
    }

    public static TreeActionBuilder when(JTree tree) {
        return new TreeActionBuilder(tree);
    }

}
