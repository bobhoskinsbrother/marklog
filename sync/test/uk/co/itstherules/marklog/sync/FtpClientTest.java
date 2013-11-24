package uk.co.itstherules.marklog.sync;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.itstherules.marklog.sync.FtpCode.*;

public final class FtpClientTest {

    private FakeFtpServer fakeFtpServer;

    @Before
    public void before() {
        FakeFtpServer fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0);  // use any free port
        FileSystem fileSystem = new UnixFakeFileSystem();
        fakeFtpServer.addUserAccount(new UserAccount("ben", "ben", "/tmp/data"));
        fileSystem.add(new DirectoryEntry("/tmp/data"));
        fileSystem.add(new FileEntry("/tmp/data/file.txt", "abcdef 1234567890"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.start();
        this.fakeFtpServer = fakeFtpServer;
    }

    @After
    public void after() {
        fakeFtpServer.stop();
    }

    @Test public void canPullFile() throws Exception {
        int port = fakeFtpServer.getServerControlPort();
        FtpClient unit = new FtpClient("0.0.0.0", port,"/tmp/data", "ben", "ben");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        final FtpCode outcome = unit.getFile(output, "file.txt");
        String localString = new String(output.toByteArray());
        assertThat(localString, is("abcdef 1234567890"));
        assertThat(outcome, is(CLOSING_DATA_CONNECTION));
    }

    @Test public void cannotPullUnknownFile() throws Exception {
        int port = fakeFtpServer.getServerControlPort();
        FtpClient unit = new FtpClient("0.0.0.0", port,"/tmp/data", "ben", "ben");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        final FtpCode outcome = unit.getFile(output, "file_that_doesnt_exist.txt");
        assertThat(output.toByteArray().length, is(0));
        assertThat(outcome, is(FILE_UNAVAILABLE));
    }

    @Test public void canPutFile() throws Exception {
        int port = fakeFtpServer.getServerControlPort();
        FtpClient unit = new FtpClient("0.0.0.0", port,"/tmp/data", "ben", "ben");
        ByteArrayInputStream input = new ByteArrayInputStream("abcdef 1234567890".getBytes(Charset.forName("utf8")));
        final FtpCode outcome = unit.putFile(input, "remote_file.txt");
        FileSystem fileSystem = fakeFtpServer.getFileSystem();
        assertThat(fileSystem.isFile("/tmp/data/remote_file.txt"), is(true));
        assertThat(outcome, is(CLOSING_DATA_CONNECTION));
    }

    @Test public void canDeleteFile() throws Exception {
        int port = fakeFtpServer.getServerControlPort();
        FtpClient unit = new FtpClient("0.0.0.0", port,"/tmp/data", "ben", "ben");
        final FtpCode outcome = unit.deleteFile("file.txt");
        FileSystem fileSystem = fakeFtpServer.getFileSystem();
        assertThat(fileSystem.isFile("/tmp/data/file.txt"), is(false));
        assertThat(outcome, is(FILE_ACTION_OK));
    }

    @Test public void canOverwriteFile() throws Exception {
        int port = fakeFtpServer.getServerControlPort();
        FtpClient unit = new FtpClient("0.0.0.0", port,"/tmp/data", "ben", "ben");
        final String overriddenText = "fred flintstone were ere";
        ByteArrayInputStream input = new ByteArrayInputStream(overriddenText.getBytes(Charset.forName("utf8")));
        FtpCode outcome = unit.putFile(input, "file.txt");
        assertThat(outcome, is(CLOSING_DATA_CONNECTION));

        FileSystem fileSystem = fakeFtpServer.getFileSystem();
        assertThat(fileSystem.isFile("/tmp/data/file.txt"), is(true));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        outcome = unit.getFile(output, "file.txt");
        String reply = new String(output.toByteArray());

        assertThat(reply, is(overriddenText));
        assertThat(outcome, is(CLOSING_DATA_CONNECTION));
    }

}
