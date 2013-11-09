package uk.co.itstherules.marklog.editor.markdown;

import uk.co.itstherules.marklog.editor.viewbuilder.Builder;
import uk.co.itstherules.marklog.string.MakeString;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class MarkdownTextAreaBuilder implements Builder<JTextArea> {

    private final HtmlPanel htmlPanel;
    private final File file;
    private final UndoManager undoManager;
    private final JTextArea textArea;

    public MarkdownTextAreaBuilder(final HtmlPanel htmlPanel, final File file) {
        textArea = new JTextArea();
        textArea.setName("markdownTextArea");
        this.htmlPanel = htmlPanel;
        this.file = file;
        this.undoManager = new UndoManager();
        setStyling();
        listenForChangesToDocument();
        setupUndoRedo();
        addTextFrom(file);
    }

    private void setupUndoRedo() {
        Document doc = textArea.getDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        final int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutKeyMask), "Undo");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, shortcutKeyMask), "Redo");
        actionMap.put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canUndo()) { undoManager.undo(); }
                } catch (CannotUndoException exp) {
                    exp.printStackTrace();
                }
            }
        });
        actionMap.put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canRedo()) { undoManager.redo(); }
                } catch (CannotUndoException exp) {
                    exp.printStackTrace();
                }
            }
        });
    }

    private void addTextFrom(File file) {
        textArea.setText(getTextFrom(file));
    }

    private String getTextFrom(File file) {
        try {
            return MakeString.from(file);
        } catch (IOException e) {
            return "";
        }
    }

    private void setStyling() {
        textArea.setUI(new NiceInlineColouringForMarkdownTextArea());
        textArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        textArea.setBackground(Color.WHITE);
        textArea.setMargin(new Insets(10, 10, 10, 10));
    }

    private void listenForChangesToDocument() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent documentEvent) { updateHtmlPanelAndFile(); }
            @Override public void removeUpdate(DocumentEvent documentEvent) { updateHtmlPanelAndFile(); }
            @Override public void changedUpdate(DocumentEvent documentEvent) { updateHtmlPanelAndFile(); }
        });
    }

    private void updateHtmlPanelAndFile() {
        final String markdownText = textArea.getText();
        updateHtmlPanelWith(markdownText);
        updateFileWith(markdownText);
    }

    private void updateFileWith(String markdownText) {
        try {
            final FileWriter writer = new FileWriter(file);
            writer.write(markdownText);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateHtmlPanelWith(String text) {
        final String html = MarkdownTransformer.toHtml(text);
        htmlPanel.setHtmlText(html);
    }

    @Override public JTextArea ok() {
        return textArea;
    }
}