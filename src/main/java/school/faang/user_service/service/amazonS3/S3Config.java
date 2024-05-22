package school.faang.user_service.service.amazonS3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConfigurationProperties(prefix = "services.s3")
@ConfigurationPropertiesScan
@Data
class S3Config {
    private AmazonS3 s3client;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @PostConstruct
    public void setUp() {
        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))
                )
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, AmazonS3Client.S3_SERVICE_NAME)
                )
                .withPathStyleAccessEnabled(true)
                .build();

        log.info("Connected to AmazonS3 system.");
    }
}
