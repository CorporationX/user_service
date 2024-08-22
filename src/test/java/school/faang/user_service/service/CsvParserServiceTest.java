package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.csv.CsvParserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
public class CsvParserServiceTest {

    @InjectMocks
    CsvParserService csvParserService;

    @Test
    public void test() throws IOException {
        csvParserService.convertCsvToPerson(Files.newInputStream(Path.of("src/main/resources/files/students.csv")));
    }
}
