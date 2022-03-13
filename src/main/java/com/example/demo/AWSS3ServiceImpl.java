package com.example.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

    @Autowired
    private AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    // @Async annotation ensures that the method is executed in a different background thread
    // but not consume the main thread.
    @Async
    public void uploadFile(final MultipartFile multipartFile, String userID) {
        LOGGER.info("File upload in progress.");
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            uploadFileToS3Bucket(bucketName, file, userID);

            LOGGER.info("File upload is completed.");
            file.delete();  // To remove the file locally created in the project folder.
        } catch (final AmazonServiceException ex) {
            LOGGER.info("File upload is failed.");
            LOGGER.error("Error= {} while uploading file.", ex.getMessage());
        }
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
        }
        return file;
    }

    private void uploadFileToS3Bucket(final String bucketName, final File file, String userID) {
        final String uniqueFileName = file.getName();
        String name = userID + "/" + uniqueFileName;
        LOGGER.info("Uploading file with name= " + uniqueFileName);
//        final PutObjectRequest putObjectResponse = amazonS3.putObject(PutObjectRequest
//                .builder().bucket(bucketName).key(key).build(), RequestBody.fromBytes(file.getBytes()));
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, name, file);
        amazonS3.putObject(putObjectRequest);
    }

    @Override
    // @Async annotation ensures that the method is executed in a different background thread
    // but not consume the main thread.
    @Async
    public void deleteFile(final String keyName) {
        LOGGER.info("Deleting file with name= " + keyName);
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, keyName);
        amazonS3.deleteObject(deleteObjectRequest);
        LOGGER.info("File deleted successfully.");
    }
}
