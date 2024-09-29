package school.faang.user_service.config.user;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.pojo.Person;

import java.util.Random;

@Configuration
public class UserUploadConfig {

    @Bean
    public ObjectReader csvReader() {
        return new CsvMapper()
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader());
    }

    @Bean
    public Random random() {
        return new Random(System.currentTimeMillis() / 72);
    }
}
