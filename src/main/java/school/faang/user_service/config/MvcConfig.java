package school.faang.user_service.config;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.entity.student.Person;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class MvcConfig {
    @Bean
    public ObjectReader csvOpenReader () {
        return new CsvMapper()
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader());
    }

    @Bean
    public InputStream csvInputStreamBuilder () throws IOException {
        return BOMInputStream.builder()
                .setByteOrderMarks(ByteOrderMark.UTF_8)
                .setInclude(false)
                .get();
    }
}
