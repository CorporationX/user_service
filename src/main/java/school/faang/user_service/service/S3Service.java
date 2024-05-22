package school.faang.user_service.service;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class S3Service {
    private AmazonS3 s3client;
    @Value("${services.s3.endpoint}")
    private String endpoint;
    @Value("${services.s3.accessKey}")
    private String accessKey;
    @Value("${services.s3.secretKey}")
    private String secretKey;
    @Value("${services.s3.bucketName}")
    private String bucketName;


    public void uploadFile(ByteArrayOutputStream outputStream, String key) {
        s3client = getClient();

        ObjectMetadata objectMetadata = getObjectMetadata(outputStream);

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, new ByteArrayInputStream(outputStream.toByteArray()), objectMetadata
        );

        s3client.putObject(putObjectRequest);
    }

    public InputStream downloadFile(String key) {
        s3client = getClient();

        return s3client.getObject(bucketName, key).getObjectContent();
    }

    public void deleteFile(String key) {
        s3client = getClient();

        s3client.deleteObject(bucketName, key);
    }

    private ObjectMetadata getObjectMetadata(ByteArrayOutputStream outputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(outputStream.size());
        objectMetadata.setContentType(MediaType.IMAGE_JPEG.getType());
        return objectMetadata;
    }

    private AmazonS3 getClient() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))
                )
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, AmazonS3Client.S3_SERVICE_NAME)
                )
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
