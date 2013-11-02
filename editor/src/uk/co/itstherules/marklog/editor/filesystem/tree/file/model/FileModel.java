package uk.co.itstherules.marklog.editor.filesystem.tree.file.model;

import java.io.File;
import java.util.Enumeration;

public interface FileModel {

    void insert(FileModel model, int i);
    void remove(int i);
    void remove(FileModel model);
    void removeFromParent();
    void setParent(FileModel model);
    boolean getAllowsChildren();
    boolean isLeaf();
    boolean hasChildren();
    int getIndex(FileModel model);
    int getChildCount();
    FileModel getChildAt(int i);
    FileModel getParent();
    Enumeration<FileModel> children();
    File getFile();
    boolean isRoot();
    FileModel find(File file);
}