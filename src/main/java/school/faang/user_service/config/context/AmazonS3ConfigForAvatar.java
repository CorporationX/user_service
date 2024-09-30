package school.faang.user_service.config.context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmazonS3ConfigForAvatar {

    @Bean
    public AmazonS3 amazonS3Bean() {
        var credentials = new BasicAWSCredentials("user", "password");
        var credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration("http://127.0.0.1:9000/", "us-east-1");
        var clientConfig = new ClientConfiguration()
                .withConnectionTimeout(10_000)
                .withSocketTimeout(10_000)
                .withProtocol(Protocol.HTTP);

        return AmazonS3ClientBuilder.standard()
                .withClientConfiguration(clientConfig)
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(endpointConfiguration)
                .enablePathStyleAccess()
                .build();
    }
}
