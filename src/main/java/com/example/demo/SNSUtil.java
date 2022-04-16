package com.example.demo;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SNSUtil {
    // Access key id will be read from the application.properties file during the application intialization.
    @Value("${aws.access_key_id}")
    private String accessKeyId;
    // Secret access key will be read from the application.properties file during the application intialization.
    @Value("${aws.secret_access_key}")
    private String secretAccessKey;
    // Region will be read from the application.properties file  during the application intialization.
    @Value("${aws.s3.region}")
    private String region;

    @Value("${sns.topic.arn}")
    private String snsTopicARN;

    private AmazonSNS amazonSNS;

    private static final Logger logger = LoggerFactory.getLogger(SNSUtil.class);

    @PostConstruct
    private void postConstructor() {
        logger.info("snsTopicARN: " + snsTopicARN);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKeyId, secretAccessKey)
        );
        this.amazonSNS = AmazonSNSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(region)
                .build();
    }
    public void publishSNSMessage(String message) {
        logger.info("Publishing SNS message: " + message);
        PublishResult result = this.amazonSNS.publish(this.snsTopicARN, message);
        logger.info("SNS Message ID: " + result.getMessageId());
    }
}
