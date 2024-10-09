package school.faang.user_service.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.stream.Stream;

@Configuration
public class S3CompatibleConfig {
    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String region;

    public S3CompatibleConfig(@Value("${services.s3.endpoint}") String endpoint,
                              @Value("${services.s3.accessKey}") String accessKey,
                              @Value("${services.s3.secretKey}") String secretKey,
                              @Value("${services.s3.region}") String region) {
        if (Stream.of(endpoint, accessKey, secretKey, region).anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("Some important storage params (from config) are missing/empty");
        }
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
    }

    @Bean
    public AmazonS3 s3client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
