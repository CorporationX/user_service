package school.faang.user_service.service.user.parse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.user.parse.Util.*;


@ExtendWith(MockitoExtension.class)
public class DataFromFileServiceTest {

    @InjectMocks
    private DataFromFileService dataFromFileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CsvParser csvParser;

    @Mock
    private ReadAndDivide readAndDivide;

    @Captor
    private ArgumentCaptor<List<User>> userArgumentCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(csvParser, "THREAD_POOL_SIZE", 4);
    }

    @Nested
    class PositiveTests {

        @DisplayName("should return data with Country when passed")
        @Test
        void saveUsersFromFileTest() {
            InputStream inputStream = getInputStream();
            List<CsvPart> csvParts = getCsvParts();
            Person person = getPersons().get(0);
            Country country = new Country();
            country.setTitle("USA");
            Iterable<Country> countries = List.of(country);
            when(readAndDivide.toCsvPartDivider(inputStream)).thenReturn(csvParts);
            when(csvParser.multiParseCsv(anyList())).thenReturn(getPersons());
            when(countryRepository.findAll()).thenReturn(countries);
            when(userMapper.toUser(person)).thenReturn(new User());

            dataFromFileService.saveUsersFromFile(inputStream);

            verify(userRepository).saveAll(userArgumentCaptor.capture());
            assertEquals("USA", userArgumentCaptor.getValue().get(0).getCountry().getTitle());
        }
    }
}
