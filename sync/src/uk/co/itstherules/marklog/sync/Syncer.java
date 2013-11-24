package uk.co.itstherules.marklog.sync;

import uk.co.itstherules.marklog.actions.UpdateReporter;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.filesystem.FilePaths;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class Syncer {

    private final ProjectConfiguration c;
    private final UpdateReporter reporter;

    public Syncer(ProjectConfiguration c, UpdateReporter reporter) {
        this.c = c;
        this.reporter = reporter;
    }

    public void sync(final File source) {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                boolean error = false;
                try {
                    String canonicalRoot = FilePaths.canonicalFor(source);
                    reporter.report("Attempting to connect to the remote ftp server");
                    FtpClient client = new FtpClient(c.getFtpHost(), c.getFtpPort(), c.getFtpWorkingDirectory(), c.getFtpUsername(), c.getFtpPassword());
                    reporter.success("Successfully connected to the remote ftp server");
                    walkDirectory(canonicalRoot, source, client);
                    client.close();
                    reporter.success("Connection successfully closed");
                } catch (IOException e) {
                    reporter.error("Sync failed! ", e.getMessage());
                    error = true;
                }
                if (!error) reporter.success("Successfully synced!");
                return null;
            }

            private void walkDirectory(String canonicalRoot, File directory, FtpClient client) throws IOException {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            reporter.report("Found directory: ", FilePaths.canonicalFor(file), " walking the directory now");
                            walkDirectory(canonicalRoot, file, client);
                        } else {
                            String canonicalFilePath = FilePaths.canonicalFor(file);
                            reporter.success("Found file: ", canonicalFilePath);
                            FileInputStream inputStream = new FileInputStream(file);
                            reporter.report("Attempting to put the file remotely");
                            client.putFile(inputStream, canonicalFilePath.substring(canonicalRoot.length() + 1));
                            reporter.success("File successfully put remotely");

                        }
                    }
                }
            }
        }.execute();
    }
}
