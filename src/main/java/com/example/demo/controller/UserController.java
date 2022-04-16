package com.example.demo.controller;

import antlr.Token;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.util.EC2MetadataUtils;
import com.example.demo.*;
import com.example.demo.exception.EmailExistException;
import com.example.demo.exception.NotValidEmailException;
import com.example.demo.exception.NotVerifyException;
import com.example.demo.repository.UserRepository;
import com.google.gson.Gson;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController {
    @Autowired
    private SNSUtil snsUtil;
    @Value("${aws.access_key_id}")
    private String accessKeyId;
    // Secret access key will be read from the application.properties file during the application intialization.
    @Value("${aws.secret_access_key}")
    private String secretAccessKey;

    @Autowired
    DynamoService dynamoService;

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
        return "demo 7/8";
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
        if (!userData.isVerified()) {
            throw new NotVerifyException();
        }
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

    @PostMapping("/v1/user")
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

        String token = RandomStringUtils.random(8,true,true);
        dynamoService.putItemInTable("email",providedUser.getEmailAddress(),"token", token);
        Message message = new Message();
        String link = "http://prod.csye6225jinshuang.me/v1/verifyUserEmail?email="+ providedUser.getEmailAddress()+"&token="+token;
        message.setFirst_name(providedUser.getFirstName());
        message.setUsername(providedUser.getEmailAddress());
        message.setOne_time_token(token);
        message.setLink(link);
        message.setMessage_type("String");
        this.snsUtil.publishSNSMessage(new Gson().toJson(message));
        return userRepository.save(providedUser);
    }

    @GetMapping(value = "/v1/verifyUserEmail")
    public void verifyUser(@RequestParam String email,
                           @RequestParam String token){
        User userData = userRepository.findByEmailAddress(email);
        HashMap<String,AttributeValue> queryItem = (HashMap<String, AttributeValue>) dynamoService.getDynamoDBItem("email",email);
        if (queryItem==null) return;
        userData.setVerified(true);
        userRepository.save(userData);
    }

    @PutMapping("/v2/user/self")
    public User updateUser(@Valid @RequestBody User userDetails) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());

        if (!userData.isVerified()) {
            throw new NotVerifyException();
        }

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
