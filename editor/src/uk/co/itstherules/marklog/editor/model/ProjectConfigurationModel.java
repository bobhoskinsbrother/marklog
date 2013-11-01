package uk.co.itstherules.marklog.editor.model;

import uk.co.itstherules.marklog.string.FileifyTitle;

import java.io.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

public final class ProjectConfigurationModel {

    private final String name;
    private final File directory;
    private final File file;

    public ProjectConfigurationModel(File directory, String name) {
        this.name = name;
        this.directory = directory;
        final FileifyTitle fileifyTitle = new FileifyTitle(".marklog");
        final String fileName = fileifyTitle.manipulate(name);
        file = new File(directory, fileName);
    }

    public ProjectConfigurationModel(File propertyFile) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.name = props.getProperty("project.name");
        this.directory = new File(props.getProperty("project.directory"));
        this.file = propertyFile;
    }

    public void save() {
        try {
            if(!directory.exists()) { directory.mkdirs(); }
            Properties props = new Properties();
            props.setProperty("project.name", name);
            props.setProperty("project.directory", getDirectory().getAbsolutePath());
            OutputStream out = new FileOutputStream(file);
            DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK);
            String comment = new StringBuilder("This project \"").append(name).append("\" was created on ")
                    .append(format.format(Calendar.getInstance().getTime())).toString();
            props.store(out, comment);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to store configuration");
        }
    }

    public File getDirectory() {
        return directory;
    }
}
