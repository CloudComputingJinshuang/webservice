package com.example.demo.controller;

import com.example.demo.AWSS3Service;
import com.example.demo.Image;
import com.example.demo.User;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {
    @Autowired
    private AWSS3Service service;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageRepository imageRepository;

    @Value("${aws.s3.bucket}")
    public String bucketName;

    @PostMapping(value= "/v2/user/self/pic")
    public Image uploadFile(@RequestPart(value= "file") final MultipartFile multipartFile) {
        Image image = new Image();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());
        String fileName = multipartFile.getOriginalFilename();
        String url = "https://s3.us-west-2.amazonaws.com/" + bucketName + "/" + userData.getId() + "/"+ multipartFile.getOriginalFilename();
        image.setUserID(userData.getId());
        image.setUrl(url);
        image.setFileName(fileName);
        Image foundImage = imageRepository.findByUserID(userData.getId());
        if (foundImage!=null) {
            imageRepository.delete(foundImage);
            String keyName = userData.getId() +"/" + foundImage.getFileName();
            service.deleteFile(keyName);
        }
        service.uploadFile(multipartFile, userData.getId());
        return imageRepository.save(image);
    }

    @GetMapping (value= "/v2/user/self/pic")
    public Image getImage () {
        Image image = new Image();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());
        String userID = userData.getId();
        image =  imageRepository.findByUserID(userID);
        return image;
    }

    @DeleteMapping (value= "/v2/user/self/pic")
    public void deletePic () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());
        Image image = imageRepository.findByUserID(userData.getId());

        String keyName = userData.getId() + "/" + image.getFileName();
        service.deleteFile(keyName);
        imageRepository.delete(image);

    }
}
