package uk.co.itstherules.marklog.editor.markdown;

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

public final class MarkdownTextArea extends JTextArea {

    private final HtmlPanel htmlPanel;
    private final File file;
    private final UndoManager undoManager;

    public MarkdownTextArea(final HtmlPanel htmlPanel, final File file) {
        setName("markdownTextArea");
        this.htmlPanel = htmlPanel;
        this.file = file;
        this.undoManager = new UndoManager();
        setStyling();
        listenForChangesToDocument();
        setupUndoRedo();
        addTextFrom(file);
    }

    private void setupUndoRedo() {
        Document doc = getDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });
        InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = getActionMap();
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
        setText(getTextFrom(file));
    }

    private String getTextFrom(File file) {
        try {
            return MakeString.from(file);
        } catch (IOException e) {
            return "";
        }
    }

    private void setStyling() {
        setUI(new NiceInlineColouringForMarkdownTextArea());
        setFont(new Font("monospaced", Font.PLAIN, 14));
        setBackground(Color.WHITE);
        setMargin(new Insets(10, 10, 10, 10));
    }

    private void listenForChangesToDocument() {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent documentEvent) { updateHtmlPanelAndFile(); }
            @Override public void removeUpdate(DocumentEvent documentEvent) { updateHtmlPanelAndFile(); }
            @Override public void changedUpdate(DocumentEvent documentEvent) { updateHtmlPanelAndFile(); }
        });
    }

    private void updateHtmlPanelAndFile() {
        final String markdownText = getText();
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
}