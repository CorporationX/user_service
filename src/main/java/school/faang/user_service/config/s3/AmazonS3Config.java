package school.faang.user_service.config.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        var credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        var clientConfig = new ClientConfiguration()
                .withConnectionTimeout(10_000)
                .withSocketTimeout(10_000)
                .withProtocol(Protocol.HTTPS);

        return AmazonS3Client.builder()
                .withClientConfiguration(clientConfig)
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(endpointConfiguration)
                .enablePathStyleAccess()
                .build();
    }
}
