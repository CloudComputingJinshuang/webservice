package com.example.demo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {
    void uploadFile(MultipartFile multipartFile, String userid);
    void deleteFile(String keyName);
}
