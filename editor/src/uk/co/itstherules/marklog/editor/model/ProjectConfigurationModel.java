package uk.co.itstherules.marklog.editor.model;

import uk.co.itstherules.marklog.string.FileifyTitle;

import java.io.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

public final class ProjectConfigurationModel {

    private final String name;
    private final String directory;
    private final File file;

    public ProjectConfigurationModel(String name, String directory) {
        this.name = name;
        this.directory = directory;
        final FileifyTitle fileifyTitle = new FileifyTitle(".marklog");
        final String fileName = fileifyTitle.manipulate(name);
        file = new File(directory, fileName);
    }

    public ProjectConfigurationModel(File file) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.name = props.getProperty("project.name");
        this.directory = props.getProperty("project.directory");
        this.file = file;
    }

    public void save() {
        try {
            Properties props = new Properties();
            props.setProperty("project.name", name);
            props.setProperty("project.directory", directory);
            OutputStream out = new FileOutputStream(file);
            DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK);
            String comment = new StringBuilder("This project \"").append(name).append("\" was created on ")
                    .append(format.format(Calendar.getInstance().getTime())).toString();
            props.store(out, comment);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to store configuration");
        }
    }

    public String getDirectory() {
        return directory;
    }
}
