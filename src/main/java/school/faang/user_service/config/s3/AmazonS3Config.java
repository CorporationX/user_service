package school.faang.user_service.config.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmazonS3Config {

    private final AmazonS3Properties properties;

    @Bean
    public AmazonS3 amazonS3() {
        var credentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
        var credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), properties.getRegion());
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
