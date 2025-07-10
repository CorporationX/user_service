package school.faang.user_service.service.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EducationRepository educationRepository;
    @Spy
    private EducationMapper educationMapper;

    private User user = new User();
    private EducationDto educationDto;
    private EducationService educationService;

    @BeforeEach
    void setUp() {
        educationService = new EducationService(userRepository, educationRepository, educationMapper);
    }

    EducationDto createEducationDto(int year) {
        educationDto = new EducationDto();
        educationDto.setYearFrom(year);
        return educationDto;
    }

    //тесты на добавление данных об образовании
    @Test
    void testAddEducationEducationDtoNotNull() {
        EducationDto educationDto = null;
        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, educationDto));
    }

    @Test
    void testAddEducationYearFromNotExceedTheCurrentYear() {
        EducationDto educationDto = createEducationDto(2025);
        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, educationDto));
    }

    @Test
    void testAddEducationNotFindUserById() {
        EducationDto educationDto = createEducationDto(2024);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, educationDto));
    }

    @Test
    void testAddEducationSave() {
        EducationDto educationDto = createEducationDto(2024);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Education education = educationMapper.toEducationWithUser(educationDto, user);
        when(educationRepository.save(education)).thenReturn(education);
        educationService.addEducation(anyLong(), educationDto);
        verify(educationRepository, times(1)).save(education);
    }

    //тесты на обновление данных об образовании
    @Test
    void testUpdateEducationEducationDtoNotNull() {
        EducationDto educationDto = null;
        assertThrows(DataValidationException.class, () -> educationService.updateEducation(1L, educationDto));
    }

    @Test
    void testUpdateEducationYearFromNotExceedTheCurrentYear() {
        EducationDto educationDto = createEducationDto(2025);
        assertThrows(DataValidationException.class, () -> educationService.updateEducation(1L, educationDto));
    }

    @Test
    void testUpdateEducationNotFoundById() {
        EducationDto educationDto = createEducationDto(2024);
        when(educationRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> educationService.updateEducation(1L, educationDto));
    }

    @Test
    void testUpdateEducationIncorrectUserId() {
        EducationDto educationDto = createEducationDto(2024);
        educationDto.setId(1L);
        user.setId(2L);
        Education education = new Education();
        education.setId(1L);
        education.setUser(user);

        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));

        long userId = 1L;
        assertThrows(DataValidationException.class, () -> educationService.updateEducation(userId, educationDto));
    }

    @Test
    void testUpdateEducationMethodSaveIsCalled() {
        EducationDto educationDto = createEducationDto(2024);
        educationDto.setId(1L);
        user.setId(1L);
        Education education = new Education();
        education.setId(1L);
        education.setUser(user);
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));
        long userId = 1L;

        when(educationRepository.save(education)).thenReturn(education);
        educationService.updateEducation(userId, educationDto);
        verify(educationRepository, times(1)).save(education);
    }

    //тесты на возврат данных об образовании по Id
    @Test
    void testGetEducationByIdNonNegative() {
        long educationId = -1;
        EducationDto educationDto = createEducationDto(2024);
        assertThrows(DataValidationException.class, () -> educationService.getById(educationId));
    }

    @Test
    void testGetEducationByIdNotFoundById() {
        EducationDto educationDto = createEducationDto(2024);
        when(educationRepository.findById(anyLong())).
                thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> educationService.getById(1L));
    }

    @Test
    void testGetEducationByIdMethodFindByIdIsCalled() {
        EducationDto educationDto = createEducationDto(2024);
        long educationId = 1L;
        Education education = new Education();
        when(educationRepository.findById(educationId)).thenReturn(Optional.of(education));
        educationService.getById(educationId);
        verify(educationRepository, times(1)).findById(educationId);
    }
}