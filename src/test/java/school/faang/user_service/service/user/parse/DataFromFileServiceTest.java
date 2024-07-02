package school.faang.user_service.service.user.parse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.service.user.parse.Util.getInputStream;

@ExtendWith(MockitoExtension.class)
public class DataFromFileServiceTest {

    @InjectMocks
    private DataFromFileService dataFromFileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private CsvParser csvParser;

    @Spy
    private ReadAndDivide readAndDivide;

    @Captor
    private ArgumentCaptor<List<InputStream>> inputStreamArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<User>> userArgumentCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(csvParser, "THREAD_POOL_SIZE", 4);
    }

    @Nested
    class PositiveTests {

        @DisplayName("should return data with 4 elements")
        @Test
        void testForSaveUsersFromFilet() throws FileNotFoundException {
            assertEquals(4, dataFromFileService.saveUsersFromFile(getInputStream()).size());
        }
    }

    @Nested
    class NegativeTests {

        @DisplayName("should return data with 4 elements")
        @Test
        void testForSaveUsersFromFileCheckCsvParserOnClick() throws IOException {
            dataFromFileService.saveUsersFromFile(getInputStream());

            verify(csvParser).multiParseCsv(inputStreamArgumentCaptor.capture());
            assertEquals(4, inputStreamArgumentCaptor.getValue().size());
        }

        @DisplayName("should return data with 4 elements")
        @Test
        void testForSaveUsersFromFileCheckUserRepositoryOnClick() throws FileNotFoundException {
            dataFromFileService.saveUsersFromFile(getInputStream());

            verify(userRepository).saveAll(userArgumentCaptor.capture());
            assertEquals(4, userArgumentCaptor.getValue().size());
        }
    }
}
