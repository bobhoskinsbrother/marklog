package uk.co.itstherules.marklog.editor.model;

import uk.co.itstherules.marklog.string.FileifyTitle;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

public final class ProjectConfiguration {

    private static final String PROJECT_NAME = "project.name";
    private static final String PROJECT_FTP_HOST = "project.ftp.host";
    private static final String PROJECT_FTP_USERNAME = "project.ftp.username";
    private static final String PROJECT_FTP_PASSWORD = "project.ftp.password";
    private File directory;
    private String name;
    private String ftpHost;
    private String ftpUsername;
    private String ftpPassword;

    public ProjectConfiguration() {
        directory = new File(System.getProperty("user.dir"));
        setName("New Blog");
        ftpHost = "";
        ftpUsername = "";
        ftpPassword = "";
    }

    public void load(File file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        name = properties.getProperty(PROJECT_NAME);
        directory = file.getParentFile();
        ftpHost = properties.getProperty(PROJECT_FTP_HOST);
        ftpUsername = properties.getProperty(PROJECT_FTP_USERNAME);
        ftpPassword = properties.getProperty(PROJECT_FTP_PASSWORD);
    }

    public void save() {
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = makeFile();

            Properties properties = new Properties();
            properties.setProperty(PROJECT_NAME, name);
            properties.setProperty(PROJECT_FTP_HOST, ftpHost);
            properties.setProperty(PROJECT_FTP_USERNAME, ftpUsername);
            properties.setProperty(PROJECT_FTP_PASSWORD, ftpPassword);
            OutputStream out = new FileOutputStream(file);
            DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK);
            String comment = new StringBuilder("This project \"").append(name).append("\" was created on ").append(format.format(Calendar.getInstance().getTime())).toString();
            properties.store(out, comment);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to store configuration");
        }
    }

    private File makeFile() {
        final FileifyTitle fileifyTitle = new FileifyTitle(".marklog");
        final String fileName = fileifyTitle.manipulate(name);
        return new File(directory, fileName);
    }

    public File getDirectory() {
        return directory;
    }

    public boolean isValid() {
        return (directory != null && !"".equals(name) && isFtpInformationValid());
    }

    public boolean isFtpInformationValid() {
        final List<String> values = Arrays.asList(ftpHost, ftpPassword, ftpUsername);
        for (String value : values) {
            if ("".equals(value)) {
                return false;
            }
        }
        return true;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    public void setFtpUsername(String ftpUsername) {
        this.ftpUsername = ftpUsername;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public String getName() {
        return name;
    }

    public String getFtpHost() {
        return ftpHost;
    }
    public String getFtpUsername() {
        return ftpUsername;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }
}
