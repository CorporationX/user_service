package school.faang.user_service.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.properties.AmazonS3Properties;

@Configuration
@RequiredArgsConstructor
public class AmazonS3Config {
    private final AmazonS3Properties amazonS3Properties;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(amazonS3Properties.getAccessKey(), amazonS3Properties.getSecretKey());
        return AmazonS3ClientBuilder.standard()
            .withRegion(amazonS3Properties.getRegion())
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();
    }
}
