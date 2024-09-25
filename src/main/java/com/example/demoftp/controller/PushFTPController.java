package com.example.demoftp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

@RestController
@RequestMapping("/ftp")
public class PushFTPController {

    @Autowired
    private MessageChannel ftpUploadChannel;

    @PostMapping
    public ResponseEntity<?> pushMessage(@RequestParam("file") MultipartFile file) throws IOException {
        File file1 = new File("D:\\Users\\vsd\\ftp\\Send\\demo_send");
        try (OutputStream outStream = new FileOutputStream(file1)) {
            outStream.write(file.getBytes());
        }
        ftpUploadChannel.send(MessageBuilder.withPayload(file1)
                        .setHeader("remote-directory", "/Send")
                .build());
        return ResponseEntity.noContent().build();
    }
}
