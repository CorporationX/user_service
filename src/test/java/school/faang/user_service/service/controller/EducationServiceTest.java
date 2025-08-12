package school.faang.user_service.service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.education.EducationServiceImpl;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Spy
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationServiceImpl educationService;

    @Captor
    private ArgumentCaptor<Education> educationCaptor;

    private long userId;
    private long educationId;
    private EducationDto educationDto;
    private int correctYearFrom;
    private User user;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        educationId = 12L;
        correctYearFrom = 1923;
        educationDto = EducationDto.builder()
                .yearFrom(correctYearFrom)
                .id(educationId)
                .build();
        user = User.builder()
                .id(userId)
                .build();
    }

    @Test
    void testAddEducationIncorrectYear() {
        int incorrectYear = 2026;
        educationDto.setYearFrom(incorrectYear);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> educationService.addEducation(userId, educationDto));
        assertEquals("Year of start must be early", dataValidationException.getMessage());

    }

    @Test
    void testAddEducationIncorrectUserId() {
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> educationService.addEducation(userId, educationDto));
        assertEquals(String
                .format("User with id %d was not found", userId), entityNotFoundException.getMessage());
    }


    @Test
    void testUpdateEducationNotExistEducation() {

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> educationService.updateEducation(userId, educationDto));
        assertEquals(String.format("Education by id %d was not found!", educationId)
                , entityNotFoundException.getMessage());
    }

    @Test
    void testUpdateEducationIncorrectUserId() {
        when(educationRepository.existsById(educationId)).thenReturn(true);
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> educationService.updateEducation(userId, educationDto));
        assertEquals(String
                .format("User with id %d was not found", userId), entityNotFoundException.getMessage());
    }


    @Test
    void testGetByIdIncorrectUser() {
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> educationService.getById(educationId));
        assertEquals(String
                .format("Education with id %d was not found", educationId), entityNotFoundException.getMessage());
    }
}