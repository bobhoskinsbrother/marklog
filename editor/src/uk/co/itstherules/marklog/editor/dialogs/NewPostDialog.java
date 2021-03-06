package uk.co.itstherules.marklog.editor.dialogs;

import net.miginfocom.swing.MigLayout;
import uk.co.itstherules.marklog.editor.MarklogApp;
import uk.co.itstherules.marklog.editor.MarklogController;
import uk.co.itstherules.marklog.editor.actionbuilder.ButtonActionBuilder;
import uk.co.itstherules.marklog.editor.actionbuilder.TextFieldActionBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static uk.co.itstherules.marklog.editor.actionbuilder.ActionBuilder.when;
import static uk.co.itstherules.marklog.editor.viewbuilder.ButtonBuilder.button;

public final class NewPostDialog extends JDialog {

    private final File file;
    private final MarklogController controller;
    private String postName = "";

    public NewPostDialog(MarklogApp app, MarklogController controller, File file) {
        super(app, true);
        setName("newPost");
        this.file = file;
        this.controller = controller;
        JTextField postNameTextField = new JTextField();
        postNameTextField.setPreferredSize(new Dimension(300, 30));
        JButton createButton = button("Create Post").withClickAction(verifyAndCreatePost()).ok();
        postNameTextField.setName("postName");
        when(postNameTextField).textHasChanged(applyToPostName());
        setView(app, postNameTextField, createButton);
    }

    private void setView(MarklogApp app, JTextField postNameTextField, JButton createButton) {
        setLayout(new MigLayout("insets 10"));
        setPreferredSize(new Dimension(475, 200));
        add(new JLabel("<html><h2>New Post</h2>"), "wrap");
        add(new JSeparator(), "wrap");
        add(new JLabel("Post Name:"));
        add(postNameTextField, "gapleft 10, wrap");
        add(createButton);
        pack();
        setLocationRelativeTo(app);
        setVisible(true);
    }

    private ButtonActionBuilder.ApplyChanged verifyAndCreatePost() {
        return new ButtonActionBuilder.ApplyChanged() {
            @Override public void apply() {
                if ("".equals(postName)) {
                    String message = "Please fill in post name";
                    JOptionPane.showMessageDialog(null, message, "No post name supplied", JOptionPane.ERROR_MESSAGE);
                } else {
                    controller.addNewPost(file, postName);
                    dispose();
                }
            }
        };
    }

    private TextFieldActionBuilder.ApplyChanged applyToPostName() {
        return new TextFieldActionBuilder.ApplyChanged() {
            @Override public void apply(String textFieldText) {
                postName = textFieldText;
            }
        };
    }

}