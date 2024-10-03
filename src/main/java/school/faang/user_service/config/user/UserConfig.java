package school.faang.user_service.config.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class UserConfig {

    @Bean
    public Random random() {
        return new Random(System.currentTimeMillis() / 72);
    }
}
