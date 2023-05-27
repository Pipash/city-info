package com.spring.camel.config;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.UnknownHostException;

//@Configuration

public class FTPConnector {
    Logger logger = LoggerFactory.getLogger(FTPConnector.class);

    @Value("${ftp-host}")
    private String ftp_host;

    @Value("${ftp-port}")
    private int ftp_port;
    @Value("${ftp-user-name}")
    private String ftp_user_name;
    @Value("${ftp-password}")
    private String ftp_password;

    public FTPClient connect() throws IOException
    {
        // Create an instance of FTPClient
        FTPClient ftpClient = new FTPClient();
        try {
            // establish a connection with specific host and
            // port.
            ftpClient.connect(ftp_host, ftp_port);

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.info(
                        "Operation failed. Server reply code: "
                                + replyCode);
                ftpClient.disconnect();
            }

            // login to ftp server with username and
            // password.
            boolean success
                    = ftpClient.login(ftp_user_name, ftp_password);
            if (!success) {
                ftpClient.disconnect();
            }
            // assign file type according to the server.
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            // change specific directory of ftp server from
            // you want to download files.
            boolean changedRemoteDir
                    = ftpClient.changeWorkingDirectory(
                    "/home/testuser/directory");
            if (!changedRemoteDir) {
                logger.info("Remote directory not found.");
            }
        }
        catch (UnknownHostException E) {
            logger.info("No such ftp server");
        }
        catch (IOException e) {
            logger.info(e.getMessage());
        }
        return ftpClient;
    }
}
