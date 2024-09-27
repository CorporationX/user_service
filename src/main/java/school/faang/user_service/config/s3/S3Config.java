package school.faang.user_service.config.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
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
public class S3Config {
    private AmazonS3 s3Client;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucketName;

    @PostConstruct
    public void setUp() {
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration
                        (new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials
                        (new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .build();
        log.info("Connected to AmazonS3 system.");
    }
}
