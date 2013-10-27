package uk.co.itstherules.marklog.editor.filesystem;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public final class DirectoryChooserComponent extends JPanel {

    private final JButton button;
    private final JLabel label;
    private final java.util.List<FileChosenListener> listeners;


    public DirectoryChooserComponent() {
        listeners = new ArrayList<FileChosenListener>();
        setLayout(new MigLayout());
        button = new JButton("Choose Directory");
        label = new JLabel("No directory chosen");
        add(button);
        add(label, "gapleft 10");
        button.addActionListener(new ChooseDirectoryButtonClicked(this));
    }

    public void addFileChosenListener(FileChosenListener listener) {
        listeners.add(listener);
    }

    private void setLabelText(String text) {
        label.setToolTipText(text);
        label.setText(abbreviatePath(text));

    }

    private String abbreviatePath(String absolutePath) {
        final int length = absolutePath.length();
        final int maxLength = 15;
        final int stringLength = Math.min(length, maxLength);
        StringBuilder b = new StringBuilder();
        if(stringLength == maxLength) { b.append("..."); }
        b.append(absolutePath.substring(length - stringLength, length));
        return b.toString();
    }


    private class ChooseDirectoryButtonClicked extends AbstractAction{

        private final DirectoryChooserComponent directoryChooserComponent;

        public ChooseDirectoryButtonClicked(DirectoryChooserComponent directoryChooserComponent) {
            this.directoryChooserComponent = directoryChooserComponent;
        }

        @Override public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == button) {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(directoryChooserComponent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File directory = fileChooser.getSelectedFile();
                    for (FileChosenListener listener : listeners) {
                        listener.fileChosen(directory);
                    }
                    setLabelText(directory.getAbsolutePath());
                }
            }
        }

    }

    public interface FileChosenListener {
        public void fileChosen(File file);
    }
}
