package com.example.demo;

import com.amazonaws.util.EC2MetadataUtils;
import com.example.demo.exception.NotVerifyException;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.UserRepository;
import com.timgroup.statsd.StatsDClient;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    StatsDClient statsd;
    @Autowired
    private AWSS3Service service;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageRepository imageRepository;

    @Value("${aws.s3.bucket}")
    public String bucketName;

    @PostMapping(value= "/v1/user/self/pic")
    public Image uploadFile(@RequestPart(value= "file") final MultipartFile multipartFile) {
        Image image = new Image();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());
        if (!userData.isVerified()) {
            throw new NotVerifyException();
        }
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

        statsd.incrementCounter("post-/v2/user/self/pic");

        logger.info("This is info message");
        logger.error("This is error message");
        logger.warn("This is warn message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time - post - /v2/user/self/pic", DateTime.now().toString());

        try {
            jsonObject.put("PrivateIpAddress", EC2MetadataUtils.getPrivateIpAddress());
            jsonObject.put("InstanceId", EC2MetadataUtils.getInstanceId());
            jsonObject.put("AvailabilityZone", EC2MetadataUtils.getAvailabilityZone());
            jsonObject.put("InstanceRegion", EC2MetadataUtils.getEC2InstanceRegion());
            jsonObject.put("AmiID", EC2MetadataUtils.getAmiId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info(jsonObject.toString());

        return imageRepository.save(image);
    }

    @GetMapping (value= "/v1/user/self/pic")
    public Image getImage () {
        Image image = new Image();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());
        if (!userData.isVerified()) {
            throw new NotVerifyException();
        }
        String userID = userData.getId();
        image =  imageRepository.findByUserID(userID);
        statsd.incrementCounter("get-/v2/user/self/pic");

        logger.info("This is info message");
        logger.error("This is error message");
        logger.warn("This is warn message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time - get - /v2/user/self/pic", DateTime.now().toString());

        try {
            jsonObject.put("PrivateIpAddress", EC2MetadataUtils.getPrivateIpAddress());
            jsonObject.put("InstanceId", EC2MetadataUtils.getInstanceId());
            jsonObject.put("AvailabilityZone", EC2MetadataUtils.getAvailabilityZone());
            jsonObject.put("InstanceRegion", EC2MetadataUtils.getEC2InstanceRegion());
            jsonObject.put("AmiID", EC2MetadataUtils.getAmiId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info(jsonObject.toString());
        return image;
    }

    @DeleteMapping (value= "/v1/user/self/pic")
    public void deletePic () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());
        if (!userData.isVerified()) {
            throw new NotVerifyException();
        }
        Image image = imageRepository.findByUserID(userData.getId());

        String keyName = userData.getId() + "/" + image.getFileName();
        service.deleteFile(keyName);
        imageRepository.delete(image);

        statsd.incrementCounter("delete-/v2/user/self/pic");

        logger.info("This is info message");
        logger.error("This is error message");
        logger.warn("This is warn message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time - delete - /v2/user/self/pic", DateTime.now().toString());

        try {
            jsonObject.put("PrivateIpAddress", EC2MetadataUtils.getPrivateIpAddress());
            jsonObject.put("InstanceId", EC2MetadataUtils.getInstanceId());
            jsonObject.put("AvailabilityZone", EC2MetadataUtils.getAvailabilityZone());
            jsonObject.put("InstanceRegion", EC2MetadataUtils.getEC2InstanceRegion());
            jsonObject.put("AmiID", EC2MetadataUtils.getAmiId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info(jsonObject.toString());
    }
}
