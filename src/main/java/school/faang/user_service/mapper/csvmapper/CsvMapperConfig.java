package school.faang.user_service.mapper.csvmapper;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvMapperConfig {

    @Bean
    public CsvMapper csvMapper() {
        return new CsvMapper();
    }
}
