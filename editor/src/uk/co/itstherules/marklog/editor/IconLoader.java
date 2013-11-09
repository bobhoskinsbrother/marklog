package uk.co.itstherules.marklog.editor;

import javax.swing.*;

public final class IconLoader {

    private IconLoader() { }

    public static ImageIcon icon(String path) {
        return new ImageIcon(MarklogApp.class.getResource(path));
    }


}
