package school.faang.user_service.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.DataValidationException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvFileConverterTest {

    @Mock
    private CsvMapper csvMapper;

    @Mock
    private CsvSchema schema;

    @Mock
    private MultipartFile file;

    @Mock
    private MappingIterator<Map<String, String>> mappingIterator;

    @InjectMocks
    CsvFileConverter converter;

    @Test
    void testCallCreateUserServiceWhenCreateUserIsInvoked() {
        when(file.isEmpty()).thenReturn(true);
        assertThrows(DataValidationException.class, () -> converter.convertCsvToPerson(file));
    }
}
