package school.faang.user_service.config.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class S3Config {
    private final S3Params s3Params;

    @Bean
    public AmazonS3 amazonS3(S3Params s3Params) {
        AWSCredentials credentials = new BasicAWSCredentials(
                s3Params.getAccessKey(),
                s3Params.getSecretKey()
        );
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration
                        (new AwsClientBuilder.EndpointConfiguration(s3Params.getEndpoint(), null))
                .withCredentials
                        (new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
