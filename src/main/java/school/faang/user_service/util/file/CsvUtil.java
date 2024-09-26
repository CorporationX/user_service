package school.faang.user_service.util.file;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class CsvUtil {

    public <T> List<T> parseCsvToPojo(MultipartFile multipartFile, @Valid Class<T> clazz) {
        try {
            return new CsvMapper()
                    .readerWithSchemaFor(clazz)
                    .with(CsvSchema.emptySchema().withHeader())
                    .<T>readValues(multipartFile.getBytes())
                    .readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}