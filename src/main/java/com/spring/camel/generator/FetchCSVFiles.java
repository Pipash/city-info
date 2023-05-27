package com.spring.camel.generator;

import com.spring.camel.config.FTPConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;

@Slf4j
@Component
public class FetchCSVFiles {
    // set downloading dir where all the files will be
    // stored on local directory.
    @Value("${output.file.path}")
    private String downloadPath;
    public void downloadFiles() throws IOException {
        // create a instance of FtpConnector
        FTPConnector ftpConnector = new FTPConnector();

        // get ftp client object.
        FTPClient ftpClient = ftpConnector.connect();

        // list all the files which will be downloaded.
        FTPFile[] ftpFiles = ftpClient.listFiles();

        for(FTPFile file : ftpFiles) {
            File fileObj = new File(downloadPath
                    + file.getName());
            Files.createFile(fileObj.toPath());
            try (OutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(fileObj))) {

                // ftpclient.retrieveFile will get the file
                // from Ftp server and write it in
                // outputStream.
                boolean isFileRetrieve
                        = ftpClient.retrieveFile(file.getName(),
                        outputStream);
                log.info("{} file is downloaded : {}",
                        file.getName(), isFileRetrieve);
            }
        }
    }
}
