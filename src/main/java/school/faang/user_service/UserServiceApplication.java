package school.faang.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.user.UserSubscriptionController;

@ConfigurationPropertiesScan
@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients("school.faang.user_service.client")
public class UserServiceApplication implements CommandLineRunner {
    private final UserSubscriptionController userSubscriptionController;

    private final UserContext usercontext;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        return objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {

        usercontext.setUserId(1);


        userSubscriptionController.followUser(5);
        userSubscriptionController.followUser(1);
        userSubscriptionController.followUser(2);
        userSubscriptionController.followUser(3);
        userSubscriptionController.followUser(4);
        userSubscriptionController.unfollowUser(5);
        userSubscriptionController.followUser(5);
        userSubscriptionController.getFolloweesCount(5);
        userSubscriptionController.getFolloweesCount(5);
        userSubscriptionController.getFollowersCount(5);
        userSubscriptionController.getFollowersCount(1);
        userSubscriptionController.getFollowersCount(4);
        userSubscriptionController.getFollowers(5);
        userSubscriptionController.getFollowees(5);
    }
}