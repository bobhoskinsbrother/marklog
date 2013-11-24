package uk.co.itstherules.marklog.editor.dialogs;

import uk.co.itstherules.marklog.actions.UpdateReporter;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.DialogActionBuilder;
import uk.co.itstherules.marklog.editor.model.PostService;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.editor.viewbuilder.Builder;
import uk.co.itstherules.marklog.editor.viewbuilder.TextPaneBuilder;
import uk.co.itstherules.marklog.publisher.HtmlPublisher;
import uk.co.itstherules.marklog.publisher.SearchIndexesPublisher;
import uk.co.itstherules.marklog.string.FileifyTitle;
import uk.co.itstherules.marklog.sync.Syncer;
import uk.co.itstherules.resourceserver.ResourceServer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import static java.awt.Color.darkGray;
import static uk.co.itstherules.marklog.editor.viewbuilder.ButtonBuilder.button;
import static uk.co.itstherules.marklog.editor.viewbuilder.DialogBuilder.modalDialog;
import static uk.co.itstherules.marklog.editor.viewbuilder.LabelBuilder.label;
import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;
import static uk.co.itstherules.marklog.editor.viewbuilder.ScrollerBuilder.scroller;
import static uk.co.itstherules.marklog.editor.viewbuilder.TextPaneBuilder.textPane;

public final class SyncDialog implements Builder<JDialog> {

    private final ProjectConfiguration configuration;
    private final TextPaneBuilder textPaneBuilder;
    private final JCheckBox copyMarkdownCheckbox;
    private final PostService service;
    private final JDialog dialog;
    private File targetDirectory;
    private ResourceServer resourceServer;

    public SyncDialog(MarklogApp app, ProjectConfiguration configuration, PostService service) {
        this.configuration = configuration;
        this.service = service;
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
                        .add(panel("syncButtons").withLayout("", "[left][center][right]", "[center]")
                                    .add(button("Publish").withClickAction(publish()).ok())
                                    .add(button("Preview").withClickAction(preview()).ok())
                                    .add(button("Sync").withClickAction(sync()).ok())
                                .ok(), "span 2")
                    .ok(), "wrap")
                .notResizable()
                .centerOnParent()
                .withCloseAction(closeThis())
                .ok();
    }

    private DialogActionBuilder.ApplyChanged closeThis() {
        return new DialogActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if(resourceServer!=null) {
                    resourceServer.stop();
                    resourceServer = null;
                }
            }
        };
    }

    public static SyncDialog syncDialog(MarklogApp app, ProjectConfiguration configuration, PostService service) {
        return new SyncDialog(app, configuration, service);
    }

    private ButtonActionBuilder.ApplyChanged sync() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                final Syncer syncer = new Syncer(configuration, reporter());
                syncer.sync(targetDirectory);
            }
        };
    }

    private ButtonActionBuilder.ApplyChanged preview() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                URI uri = null;
                if(resourceServer == null) {
                    resourceServer = new ResourceServer(targetDirectory);
                    uri = resourceServer.start();
                }
                reporter().report("Previewing the blog at: ", uri.toString());
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (final IOException e) {
                    throw new RuntimeException("Can't open URL", e);
                }
            }
        };
    }

    private ButtonActionBuilder.ApplyChanged publish() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                HtmlPublisher htmlPublisher = new HtmlPublisher(configuration, targetDirectory, reporter(), service);
                final SearchIndexesPublisher indexesPublisher = new SearchIndexesPublisher(targetDirectory, reporter(), service);
                final boolean copyOriginals = copyMarkdownCheckbox.isSelected();

                htmlPublisher.publishUsingTemplate("simple", copyOriginals);
                indexesPublisher.publish("simple");
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