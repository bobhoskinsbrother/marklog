package uk.co.itstherules.marklog.editor.model;

import uk.co.itstherules.marklog.string.FileifyTitle;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

public final class ProjectConfigurationModel {

    private static final String PROJECT_NAME = "project.name";
    private static final String PROJECT_DIRECTORY = "project.directory";
    private static final String PROJECT_FTP_URL = "project.ftp.url";
    private static final String PROJECT_FTP_USERNAME = "project.ftp.username";
    private static final String PROJECT_FTP_PASSWORD = "project.ftp.password";

    private File file;
    private File directory;
    private String name;
    private String ftpUrl;
    private String ftpUsername;
    private String ftpPassword;

    public ProjectConfigurationModel() {}

    public ProjectConfigurationModel(File propertyFile) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        name = props.getProperty(PROJECT_NAME);
        directory = new File(props.getProperty(PROJECT_DIRECTORY));
        ftpUrl = props.getProperty(PROJECT_FTP_URL);
        ftpUsername = props.getProperty(PROJECT_FTP_USERNAME);
        ftpPassword = props.getProperty(PROJECT_FTP_PASSWORD);
        file = propertyFile;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void setName(String name) {
        this.name = name;
        final FileifyTitle fileifyTitle = new FileifyTitle(".marklog");
        final String fileName = fileifyTitle.manipulate(name);
        file = new File(directory, fileName);
    }

    public void setFtpUrl(String ftpUrl) {
        this.ftpUrl = ftpUrl;
    }

    public void setFtpUsername(String ftpUsername) {
        this.ftpUsername = ftpUsername;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public void save() {
        try {
            if(!directory.exists()) { directory.mkdirs(); }
            Properties properties = new Properties();
            properties.setProperty(PROJECT_NAME, name);
            properties.setProperty(PROJECT_DIRECTORY, getDirectory().getAbsolutePath());
            properties.setProperty(PROJECT_FTP_URL, ftpUrl);
            properties.setProperty(PROJECT_FTP_USERNAME, ftpUsername);
            properties.setProperty(PROJECT_FTP_PASSWORD, ftpPassword);
            OutputStream out = new FileOutputStream(file);
            DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK);
            String comment = new StringBuilder("This project \"").append(name).append("\" was created on ")
                    .append(format.format(Calendar.getInstance().getTime())).toString();
            properties.store(out, comment);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to store configuration");
        }
    }

    public File getDirectory() {
        return directory;
    }

    public boolean isValid() {
        if(directory == null) { return false; }
        final List<String> values = Arrays.asList(name, directory.getAbsolutePath(), ftpUrl, ftpPassword, ftpUsername);
        for (String value : values) {
            if("".equals(value)) {
                return false;
            }
        }
        return true;
    }
}
