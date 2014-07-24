package uk.co.itstherules.marklog.editor.model;

import uk.co.itstherules.marklog.string.FileifyTitle;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

public final class ProjectConfiguration {

    private static final String PROJECT_NAME = "project.name";
    private static final String PROJECT_FTP_HOST = "project.ftp.host";
    private static final String PROJECT_FTP_PORT = "project.ftp.port";
    private static final String PROJECT_FTP_WORKING_DIRECTORY= "project.ftp.working.directory";
    private static final String PROJECT_FTP_USERNAME = "project.ftp.username";
    private static final String PROJECT_FTP_PASSWORD = "project.ftp.password";
    public static final int DEFAULT_FTP_PORT = 21;
    public static final String DEFAULT_WORKING_DIRECTORY = "/";
    public static final String DEFAULT_FTP_HOST = "";
    public static final String DEFAULT_BLOG_TITLE_NAME = "New Blog Post";
    public static final String DEFAULT_FTP_USERNAME = "";
    public static final String DEFAULT_FTP_PASSWORD = "";
    private File directory;
    private String name;
    private String ftpHost;
    private String ftpUsername;
    private String ftpPassword;
    private int ftpPort;
    private String ftpWorkingDirectory;

    public ProjectConfiguration() {
        directory = new File(System.getProperty("user.dir"));
        setName(DEFAULT_BLOG_TITLE_NAME);
        ftpHost = DEFAULT_FTP_HOST;
        ftpWorkingDirectory = DEFAULT_WORKING_DIRECTORY;
        ftpPort = DEFAULT_FTP_PORT;
        ftpUsername = DEFAULT_FTP_USERNAME;
        ftpPassword = DEFAULT_FTP_PASSWORD;
    }

    public void load(File file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        name = properties.getProperty(PROJECT_NAME, DEFAULT_BLOG_TITLE_NAME);
        directory = file.getParentFile();
        ftpHost = properties.getProperty(PROJECT_FTP_HOST, DEFAULT_FTP_HOST);
        ftpPort = Integer.parseInt(properties.getProperty(PROJECT_FTP_PORT, String.valueOf(DEFAULT_FTP_PORT)));
        ftpWorkingDirectory = properties.getProperty(PROJECT_FTP_WORKING_DIRECTORY, DEFAULT_WORKING_DIRECTORY);
        ftpUsername = properties.getProperty(PROJECT_FTP_USERNAME, DEFAULT_FTP_USERNAME);
        ftpPassword = properties.getProperty(PROJECT_FTP_PASSWORD, DEFAULT_FTP_PASSWORD);
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
            properties.setProperty(PROJECT_FTP_PORT, String.valueOf(ftpPort));
            properties.setProperty(PROJECT_FTP_WORKING_DIRECTORY, ftpWorkingDirectory);
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
        final List<String> values = Arrays.asList(ftpHost, ftpWorkingDirectory, ftpPassword, ftpUsername);
        for (String value : values) {
            if ("".equals(value)) {
                return false;
            }
        }
        return ftpPort > 0;
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

    public void setFtpWorkingDirectory(String ftpWorkingDirectory) {
        this.ftpWorkingDirectory = ftpWorkingDirectory;
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

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int port) {
        this.ftpPort = port;
    }

    public String getFtpWorkingDirectory() {
        return ftpWorkingDirectory;
    }
}
