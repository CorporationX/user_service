package school.faang.user_service.controller.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.user.PersonDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.CountryService;
import school.faang.user_service.model.service.impl.UserServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

public class UserServiceImplProcessCsvFileTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryService countryService;

    @Mock
    private PersonToUserMapper personToUserMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    public void testProcessCsvFile() throws Exception {
        String csvData = "FirstName,LastName,YearOfBirth,Group,StudentID,Email,Phone,Street,City,State,Country," +
                "PostalCode,Faculty,YearOfStudy,Major,GPA,Status,AdmissionDate,GraduationDate,Degree,Institution," +
                "CompletionYear,Scholarship,Employer\n" +
                "John,Doe,2000,CS101,12345,johndoe@example.com,1234567890,Main St,Anytown,CA,USA,12345," +
                "Computer Science, 2023,Software Engineering,3.5,,2020-09-01,2024-06-01,Bachelor," +
                "University of Example,2024, Example Company";

        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        Country mockCountry = new Country();
        mockCountry.setTitle("USA");
        User mockUser = new User();

        when(countryService.findOrCreateCountry("USA")).thenReturn(mockCountry);
        when(personToUserMapper.personToUser(any(PersonDto.class))).thenReturn(mockUser);

        userServiceImpl.processCsvFile(inputStream);

        verify(userRepository, times(1)).save(mockUser);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(mockCountry, userCaptor.getValue().getCountry());
    }

    @Test
    public void testProcessCsvFile_IncompleteData() throws Exception {
        String csvData = "FirstName,LastName,YearOfBirth,Group,StudentID,Email\n" +
                "John,Doe,2000,CS101,12345,johndoe@example.com";

        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        userServiceImpl.processCsvFile(inputStream);

        verify(userRepository, never()).save(any(User.class));
    }
}