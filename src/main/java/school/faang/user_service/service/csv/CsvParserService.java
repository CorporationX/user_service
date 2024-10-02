package school.faang.user_service.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public abstract class CsvParserService<T> {

    public List<T> convertCsv(InputStream inputStream) throws IOException {

        CsvMapper csvMapper = new CsvMapper();

        MappingIterator<T> mappingIterator = csvMapper
                .readerFor(getClassType())
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(inputStream);

        List<T> list = mappingIterator.readAll();
        log.info(list.toString());
        inputStream.close();
        return list;
    }

    public abstract Class<?> getClassType();
}
