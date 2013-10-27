package uk.co.itstherules.marklog.editor.model;

import uk.co.itstherules.marklog.string.FileifyTitle;

import java.io.File;
import java.io.FileWriter;

public final class PostModel {

    private final File directory;
    private final String name;
    private final String fileName;

    public PostModel(File directory, String name) {
        this.name = name;
        final FileifyTitle fileifyTitle = new FileifyTitle(".md");
        fileName = fileifyTitle.manipulate(name);
        this.directory = directory;
    }

    public void save() {
        try {
            final String toWrite = new StringBuilder(name).append("\n").append("====").toString();
            final FileWriter writer = new FileWriter(getFile());
            writer.write(toWrite);
            writer.close();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to store post");
        }
    }

    public File getFile() {return new File(directory, fileName);}

}
