package com.example.demo.controller;

import com.amazonaws.util.EC2MetadataUtils;
import com.example.demo.GetSelf;
import com.example.demo.User;
import com.example.demo.exception.EmailExistException;
import com.example.demo.exception.NotValidEmailException;
import com.example.demo.repository.UserRepository;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    StatsDClient statsd;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/healthz")
    public String test () {
        statsd.incrementCounter("healthz");

        logger.info("This is info message");
        logger.error("This is error message");
        logger.warn("This is warn message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time - get - heathz",DateTime.now().toString());

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
        return "";
    }

    @GetMapping("/v2/user/self")

    public GetSelf getUser() {
        statsd.incrementCounter("get-/v2/user/self");

        logger.info("This is info message");
        logger.error("This is error message");
        logger.warn("This is warn message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time - get - /v2/user/self",DateTime.now().toString());

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());
        GetSelf getSelf = new GetSelf();
        getSelf.setId(userData.getId());
        getSelf.setUsername(userData.getEmailAddress());
        getSelf.setFirst_name(userData.getFirstName());
        getSelf.setLast_name(userData.getLastName());
        getSelf.setAccount_created(userData.getAccount_created());
        getSelf.setAccount_updated(userData.getAccount_updated());
        return getSelf;
//        return userData;
    }

    @PostMapping("/v2/user")
    public User createUser(@Valid @RequestBody User providedUser) {
        statsd.incrementCounter("post-/v2/user");

        logger.info("This is info message");
        logger.error("This is error message");
        logger.warn("This is warn message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time - post - /v2/user",DateTime.now().toString());

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
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(providedUser.getEmailAddress());
        if (!mat.matches()) {
            throw new NotValidEmailException();
        }
        if (userRepository.findByEmailAddress(providedUser.getEmailAddress())!=null) {
            throw new EmailExistException();
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        providedUser.setPassword(bCryptPasswordEncoder.encode(providedUser.getPassword()));
        return userRepository.save(providedUser);
    }

    @PutMapping("/v2/user/self")
    public User updateUser(@Valid @RequestBody User userDetails) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());

        userData.setFirstName(userDetails.getFirstName());
        userData.setLastName(userDetails.getLastName());
        userData.setPassword(passwordEncoder.encode(userDetails.getPassword()));

        statsd.incrementCounter("put-/v2/user/self");

        logger.info("This is info message");
        logger.error("This is error message");
        logger.warn("This is warn message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time - put - /v2/user/self",DateTime.now().toString());

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

        return userRepository.save(userData);
    }

}
