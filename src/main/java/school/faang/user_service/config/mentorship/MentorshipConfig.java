package school.faang.user_service.config.mentorship;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Period;

@Configuration
public class MentorshipConfig {

    @Bean
    public Period mentorshipRequestLimit(@Value("${mentorship.request.limitation:3}") int months) {
        return Period.ofMonths(months);
    }
}