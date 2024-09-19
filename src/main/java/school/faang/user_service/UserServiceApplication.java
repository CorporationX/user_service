package school.faang.user_service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@EnableFeignClients("school.faang.user_service.client")
@OpenAPIDefinition(
        info = @Info(
                title = "User Service",
                version = "1.0.0")
)
public class UserServiceApplication {

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public AmazonS3 amazonS3() {
        var credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration("http://127.0.0.1:9000", "us-east-1");

        return AmazonS3Client.builder()
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(endpointConfiguration)
                .enablePathStyleAccess()
                .build();
    }

    @Bean
    public byte[] defaultAvatar() {
        try {
            ClassPathResource resource = new ClassPathResource("image/default-profile-picture.svg");
            Path path = resource.getFile().toPath();
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("The default avatar was not found" ,e);
        }
    }
}