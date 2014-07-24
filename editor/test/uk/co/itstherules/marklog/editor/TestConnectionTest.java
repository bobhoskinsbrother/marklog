package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.IOException;

import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;

public final class TestConnectionTest {

    private FakeFtpServer fakeFtpServer;

    private FrameFixture window;
    private int port;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void before() throws Exception {
        reset();
        window = openApp();
        FakeFtpServer fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0);  // use any free port
        FileSystem fileSystem = new UnixFakeFileSystem();
        fakeFtpServer.addUserAccount(new UserAccount("ben", "pwd", "/var/www"));
        fileSystem.add(new DirectoryEntry("/var/www"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.start();
        while(!fakeFtpServer.isStarted()) {
            Thread.sleep(100);
        }
        this.fakeFtpServer = fakeFtpServer;
        port = fakeFtpServer.getServerControlPort();
    }

    @After
    public void after() throws IOException {
        fakeFtpServer.stop();
        window.cleanUp();
        reset();
    }

    @Test public void canTestPositive() throws Exception {

        window.menuItemWithPath("File", "New Project...").click();
        final DialogFixture dialog = window.dialog("projectDialog");
        final String ftpHost = "localhost";
        final String ftpUserName = "ben";
        final String ftpPassword = "pwd";
        String wd = "/var/www";

        dialog.textBox("ftpHost").setText(ftpHost);
        dialog.textBox("ftpPort").setText(port +"");
        dialog.textBox("ftpWorkingDirectory").setText(wd);
        dialog.textBox("ftpUserName").setText(ftpUserName);
        dialog.textBox("ftpPassword").setText(ftpPassword);
        Thread.sleep(450);
        dialog.button("testConnection").click();
        window.dialog("dialog0").optionPane().requireInformationMessage().requireMessage("Connected successfully to the host with the credentials provided");
    }

    @Test public void canTestNegative() throws Exception {
        window.menuItemWithPath("File", "New Project...").click();
        final DialogFixture dialog = window.dialog("projectDialog");
        final String ftpHost = "localhost";
        final String ftpUserName = "fred";
        final String ftpPassword = "wilma";
        String wd = "/var/www";

        dialog.textBox("ftpHost").setText(ftpHost);
        dialog.textBox("ftpPort").setText(port+"");
        dialog.textBox("ftpWorkingDirectory").setText(wd);
        dialog.textBox("ftpUserName").setText(ftpUserName);
        dialog.textBox("ftpPassword").setText(ftpPassword);
        Thread.sleep(450);
        dialog.button("testConnection").click();
        window.dialog("dialog0").optionPane().requireErrorMessage().requireMessage("Unsuccessful connection: Exception in connecting to FTP Server");
    }
}
