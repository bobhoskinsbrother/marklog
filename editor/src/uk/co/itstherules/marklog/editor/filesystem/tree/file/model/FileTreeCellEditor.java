package uk.co.itstherules.marklog.editor.filesystem.tree.file.model;

import uk.co.itstherules.marklog.editor.MarklogController;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.EventObject;

import static uk.co.itstherules.marklog.editor.viewbuilder.PanelBuilder.panel;
import static uk.co.itstherules.marklog.editor.viewbuilder.TextFieldBuilder.textField;

public class FileTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

    private JPanel panel;
    private JTree tree;
    private MarklogController controller;
    private TreeCellRenderer renderer;
    private String value;
    private JTextField textField;
    private FileModel fileModel;

    public FileTreeCellEditor(JTree tree, MarklogController controller, TreeCellRenderer renderer) {
        super();
        this.tree = tree;
        this.controller = controller;
        this.renderer = renderer;
        textField = textField().name("textField").ok();
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                value = textField.getText();
                for (CellEditorListener l : listenerList.getListeners(CellEditorListener.class)) {
                    l.editingStopped(new ChangeEvent(value));
                }
                stopEditing(value);
            }
        });
        panel = panel("node").add(textField).ok();
    }

    protected void stopEditing(String value) {
        if (fileModel != null) {
            File oldFile = fileModel.getFile();
            controller.renameFile(oldFile, value);
        }
        stopCellEditing();
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        fileModel = FileModel.class.cast(value);
        if(fileModel.isRoot()) {
            return renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
        }
        this.tree = tree;
        textField.setText(fileModel.getFile().getName());
        textField.setSelectionStart(0);
        textField.setSelectionEnd(textField.getText().length());
        return panel;
    }

    @Override public boolean isCellEditable(EventObject e) {
        return !MouseEvent.class.isInstance(e);
    }

    @Override public boolean shouldSelectCell(EventObject event) {
        return fileModel != null && !fileModel.isRoot();
    }

    @Override public Object getCellEditorValue() {
        return value;
    }
}