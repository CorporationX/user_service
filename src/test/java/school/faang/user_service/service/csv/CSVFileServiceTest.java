package school.faang.user_service.service.csv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.person.PersonMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.csv.CsvValidator;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CSVFileServiceTest {

    @InjectMocks
    private CSVFileService csvFileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CsvValidator csvValidator;

    @Mock
    private PersonMapper personMapper;

    @Mock
    @Qualifier("threadPoolForConvertCsvFile")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Mock
    private MultipartFile file;

    @Mock
    private ExecutorService executorService;

    @Test
    public void testSetUpAndSaveToDB() {
        User user = new User();
        Country country = new Country();
        country.setTitle("TestCountry");
        user.setCountry(country);

        when(countryRepository.findByName("TestCountry")).thenReturn(Optional.of(country));

        csvFileService.setUpAndSaveToDB(user);

        assertNotNull(user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    public void testGeneratePassword() throws Exception {
        Method generatePasswordMethod = CSVFileService.class.getDeclaredMethod("generatePassword");
        generatePasswordMethod.setAccessible(true);
        String password = (String) generatePasswordMethod.invoke(csvFileService);

        assertNotNull(password);
        assertEquals(20, password.length());
    }
}
