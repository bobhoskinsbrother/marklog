package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.viewbuilder.Builder;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.viewbuilder.LabelBuilder.label;
import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;

public final class SyncDialog implements Builder<JDialog> {

    private final File root;
    private final JDialog dialog;

    public SyncDialog(MarklogApp app, File root) {
        dialog = new JDialog(app, true);
        dialog.setLayout(new MigLayout("inset 10"));
        dialog.setPreferredSize(new Dimension(600, 400));
        dialog.setSize(new Dimension(600, 400));
        this.root = root;
        dialog.add(
                panel("syncServerPanel")
                        .ofSize(600, 400)
                        .withLayout("inset 10", "[left]", "[top]")
                        .add(
                                label("<html>" +
                                        "<h2>Publish and Sync with Server</h2>" +
                                        "<p>This will publish the markdown blog text and push to your website</p>" +
                                        "</html>").ok())
                .ok(), "wrap");
        dialog.setTitle("Publish and Sync with Server");
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(app);
    }

    public static SyncDialog syncDialog(MarklogApp app, File root) {
        return new SyncDialog(app, root);
    }

    @Override public JDialog ok() {
        dialog.setVisible(true);
        return dialog;
    }
}
