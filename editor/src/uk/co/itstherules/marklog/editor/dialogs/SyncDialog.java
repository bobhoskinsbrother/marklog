package uk.co.itstherules.marklog.editor.dialogs;

import uk.co.itstherules.marklog.actions.UpdateReporter;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.editor.viewbuilder.Builder;
import uk.co.itstherules.marklog.editor.viewbuilder.TextPaneBuilder;
import uk.co.itstherules.marklog.publisher.HtmlPublisher;
import uk.co.itstherules.marklog.string.FileifyTitle;
import uk.co.itstherules.marklog.sync.Syncer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static java.awt.Color.darkGray;
import static uk.co.itstherules.marklog.editor.viewbuilder.ButtonBuilder.button;
import static uk.co.itstherules.marklog.editor.viewbuilder.DialogBuilder.modalDialog;
import static uk.co.itstherules.marklog.editor.viewbuilder.LabelBuilder.label;
import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;
import static uk.co.itstherules.marklog.editor.viewbuilder.ScrollerBuilder.scroller;
import static uk.co.itstherules.marklog.editor.viewbuilder.TextPaneBuilder.textPane;

public final class SyncDialog implements Builder<JDialog> {

    private final ProjectConfiguration configuration;
    private final JDialog dialog;
    private final TextPaneBuilder textPaneBuilder;
    private final JCheckBox copyMarkdownCheckbox;
    private File targetDirectory;

    public SyncDialog(MarklogApp app, ProjectConfiguration configuration) {
        this.configuration = configuration;
        String hyphenated = new FileifyTitle("").manipulate(configuration.getName());
        targetDirectory = new File(System.getProperty("java.io.tmpdir"), ".marklog/" + hyphenated);
        textPaneBuilder = textPane("Waiting to publish and / or sync\n");
        copyMarkdownCheckbox = new JCheckBox("Copy Markdown files to server?", false);
        dialog = modalDialog(app).withLayout("inset 10").ofSize(600, 400)
                .withTitle("Publish and Sync with Server")
                .add(
                    panel("syncServerPanel").ofSize(600, 350).withLayout("inset 10", "[left]", "[top]")
                        .add(label("<html>" +
                                "<h2>Publish and Sync with Server</h2>" +
                                "<p>This will publish the markdown blog text and push to your website</p>" +
                                "</html>").ok(), "wrap")
                        .add(copyMarkdownCheckbox, "wrap")
                        .add(scroller(textPaneBuilder.ok()).ofSize(580,250).ok(), "wrap")
                        .add(button("Publish").withClickAction(publish()).ok())
                        .add(button("Sync").withClickAction(sync()).ok())
                    .ok(), "wrap")
                .notResizable().centerOnParent().ok();
    }

    public static SyncDialog syncDialog(MarklogApp app, ProjectConfiguration configuration) {
        return new SyncDialog(app, configuration);
    }

    private ButtonActionBuilder.ApplyChanged sync() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                final Syncer syncer = new Syncer(configuration, reporter());
                syncer.sync(targetDirectory);
            }
        };
    }

    private ButtonActionBuilder.ApplyChanged publish() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                final HtmlPublisher publisher = new HtmlPublisher(configuration, targetDirectory, reporter());
                publisher.publishUsingTemplate("simple", copyMarkdownCheckbox.isSelected());
            }
        };
    }

    private UpdateReporter reporter() {
        return new UpdateReporter() {

            private void info(Color color, String... toReport) {
                for (String s : toReport) {
                    textPaneBuilder.append(s, color);
                }
                textPaneBuilder.append("\n");
            }

            @Override public void report(String... toReport) {
                info(darkGray, toReport);
            }

            @Override public void error(String... toReport) {
                info(Color.decode("#AA0000"), toReport);
            }

            @Override public void success(String... success) {
                info(Color.decode("#00AA00"), success);
            }
        };
    }

    @Override public JDialog ok() {
        dialog.setVisible(true);
        return dialog;
    }
}