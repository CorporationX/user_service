package school.faang.user_service.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.json.student.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.service.user.PersonDeserializer;

@Configuration
public class MvcConfig {
    @Bean
    public CsvMapper getCsvMapper () {
        return CsvMapper.builder()
                .addModules(new SimpleModule().addDeserializer(Person.class, new PersonDeserializer()))
                .build();
    }
}
