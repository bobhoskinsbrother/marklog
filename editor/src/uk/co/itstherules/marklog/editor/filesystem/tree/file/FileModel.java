package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class FileModel {

    private final File file;
    private final FileSystemModel fileSystemModel;
    private final boolean root;
    private java.util.List<FileModel> children;
    private final String uuid;

    public FileModel(File file, FileSystemModel fileSystemModel, boolean isRoot) {
        this.file = file;
        this.fileSystemModel = fileSystemModel;
        root = isRoot;
        uuid = UUID.randomUUID().toString();
    }

    public FileModel(File file, FileSystemModel fileSystemModel) {
        this(file, fileSystemModel, false);
    }

    private java.util.List<FileModel> makeChildren(File file) {
        final ArrayList<FileModel> models = new ArrayList<FileModel>();
        final File[] files = file.listFiles();
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        FileModel fileModel = FileModel.class.cast(other);

        if (root != fileModel.root) return false;
        if (uuid != null ? !uuid.equals(fileModel.uuid) : fileModel.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (root ? 1 : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }
}
