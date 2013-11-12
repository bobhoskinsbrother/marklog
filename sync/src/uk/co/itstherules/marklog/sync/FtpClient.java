package uk.co.itstherules.marklog.sync;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FtpClient {

    private final FTPClient client;

    public FtpClient(String host, int port, String username, String password) throws IOException {
        client = new FTPClient();
        client.connect(host, port);
        client.enterLocalPassiveMode();
        client.login(username, password);
        client.setKeepAlive(true);
        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
    }

    @Override protected void finalize() {
        try {
            super.finalize();
            close();
        } catch (Throwable e) {
        }
    }

    public void close() throws IOException {
        if (client.isConnected()) {
            try {
                client.logout();
                client.disconnect();
            } catch (IOException e) {
            }
        }
    }

    public FtpCode getFile(String remoteFileName, OutputStream localOutput) throws IOException {
        client.retrieveFile(remoteFileName, localOutput);
        return getFtpCode();
    }

    public FtpCode putFile(String remoteFileName, InputStream localInput) throws IOException {
        client.storeFile(remoteFileName, localInput);
        return getFtpCode();
    }

    public FtpCode deleteFile(String remoteFileName) throws IOException {
        client.deleteFile(remoteFileName);
        return getFtpCode();
    }

    private FtpCode getFtpCode() {return FtpCode.codeFor(client.getReplyCode());}

}
