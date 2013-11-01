package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import java.io.File;
import java.util.ArrayList;

public class FileModel {

    private final File file;
    private final FileSystemModel fileSystemModel;
    private final boolean root;
    private java.util.List<FileModel> children;

    public FileModel(File file, FileSystemModel fileSystemModel, boolean isRoot) {
        this.file = file;
        this.fileSystemModel = fileSystemModel;
        root = isRoot;
    }

    public FileModel(File file, FileSystemModel fileSystemModel) {
        this(file, fileSystemModel, false);
    }

    private java.util.List<FileModel> makeChildren(File file) {
        final ArrayList<FileModel> models = new ArrayList<FileModel>();
        final File[] files = file.listFiles();
        if(files == null) { return models; }
        for (int i = 0; i < files.length; i++) {
            File current = files[i];
            models.add(new FileModel(current, fileSystemModel));
        }
        return models;
    }

    public boolean hasChildren() {
        return getChildren().size() > 0;
    }
    public java.util.List<FileModel> getChildren() {
        if (children == null) {
            children = makeChildren(file);
        }
        return children;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override public String toString() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }

    public boolean isRoot() {
        return root;
    }

    public boolean isFile() {
        return file.isFile();
    }

    File getDirectory() {
        if (!file.isDirectory()) {
            return file.getParentFile();
        }
        return file;
    }

}
