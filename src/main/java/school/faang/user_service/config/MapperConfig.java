package school.faang.user_service.config;

import school.faang.user_service.mapper.GoalMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public GoalMapper goalMapper() {
        return Mappers.getMapper(GoalMapper.class);
    }
}
