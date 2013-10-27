package uk.co.itstherules.marklog.editor.actionbuilder;

import uk.co.itstherules.marklog.editor.filesystem.DirectoryChooserComponent;

import java.io.File;

public class DirectoryChooserActionBuilder {

    private final DirectoryChooserComponent directoryChooser;

    public DirectoryChooserActionBuilder(DirectoryChooserComponent directoryChooser) {
        this.directoryChooser = directoryChooser;
    }

    public DirectoryChooserActionBuilder fileHasBeenChosen(final ApplyChanged applyChanged) {
        directoryChooser.addFileChosenListener(new DirectoryChooserComponent.FileChosenListener() {
            @Override public void fileChosen(File file) {
                applyChanged.apply(file);
            }
        });
        return this;
    }

    public interface ApplyChanged {
        void apply(File file);
    }
}
