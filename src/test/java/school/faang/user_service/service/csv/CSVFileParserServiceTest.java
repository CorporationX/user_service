package school.faang.user_service.service.csv;


import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.json.student.PersonSchemaV2;
import nonapi.io.github.classgraph.types.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.ParseFIleException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.testcontainers.shaded.com.google.common.base.Verify.verify;

@ExtendWith(MockitoExtension.class)
public class CSVFileParserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private Executor executor;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CsvMapper csvMapper;

    private MultipartFile file;

    @InjectMocks
    private CSVFileParserService parserService;

    @BeforeEach
    void setUp() {
        file = mock(MultipartFile.class);
    }

    @Test
    void testShouldThrowParseFIleException() {
        when(file.isEmpty()).thenReturn(true);
        assertThrows(ParseFIleException.class, () -> parserService.parseFile(file));
    }
}
