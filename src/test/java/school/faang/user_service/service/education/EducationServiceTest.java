package school.faang.user_service.service.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.education.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.service.EducationService;
import school.faang.user_service.service.UserService;

import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EducationServiceTest {

    @Mock
    private UserService userServiceEducation;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationService educationService;

    private User user;
    private EducationDto validEducationDto;
    private EducationDto invalidYearFromEducationDto;
    private Education existingEducation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        validEducationDto = new EducationDto(1L, 2010, 2014,
                "University", "Bachelor", "Computer Science");
        invalidYearFromEducationDto = new EducationDto(1L, Year.now().getValue() + 1, 2014,
                "University", "Bachelor", "Computer Science");

        existingEducation = new Education();
        existingEducation.setId(1L);
        existingEducation.setUser(user);

        when(educationMapper.toEducation(any(EducationDto.class))).thenAnswer(invocation -> {
            EducationDto dto = invocation.getArgument(0);
            Education education = new Education();
            education.setId(dto.id());
            education.setYearFrom(dto.yearFrom());
            education.setYearTo(dto.yearTo());
            education.setInstitution(dto.institution());
            education.setEducationLevel(dto.educationLevel());
            education.setSpecialization(dto.specialization());
            return education;
        });

        when(educationMapper.toEducationDto(any(Education.class))).thenAnswer(invocation -> {
            Education education = invocation.getArgument(0);
            return new EducationDto(
                    education.getId(),
                    education.getYearFrom(),
                    education.getYearTo(),
                    education.getInstitution(),
                    education.getEducationLevel(),
                    education.getSpecialization()
            );
        });
    }


    @Test
    void addEducation_ValidData_ReturnsEducationDto() {
        when(userServiceEducation.existsById(1L)).thenReturn(true);
        when(userServiceEducation.getUserById(1L)).thenReturn(user);
        when(educationRepository.save(any(Education.class))).thenAnswer(invocation
                -> invocation.getArgument(0));

        EducationDto result = educationService.addEducation(1L, validEducationDto);

        assertNotNull(result);
        assertEquals(validEducationDto.institution(), result.institution());
        verify(userServiceEducation, times(1)).getUserById(1L);
        verify(educationRepository, times(1)).save(any(Education.class));
    }


    @Test
    void addEducation_InvalidYearFrom_ThrowsException() {
        assertThrows(DataValidationException.class, ()
                -> educationService.addEducation(1L, invalidYearFromEducationDto));
        verify(userServiceEducation, never()).getUserById(anyLong());
        verify(educationRepository, never()).save(any(Education.class));
    }

    @Test
    void addEducation_UserNotFound_ThrowsException() {
        when(userServiceEducation.existsById(1L)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, validEducationDto));

        verify(userServiceEducation, times(1)).existsById(1L);
        verify(userServiceEducation, never()).getUserById(anyLong());
        verify(educationRepository, never()).save(any(Education.class));
    }


    @Test
    void updateEducation_ValidData_ReturnsEducationDto() {
        when(educationRepository.findById(1L)).thenReturn(Optional.of(existingEducation));
        when(educationRepository.save(any(Education.class))).thenAnswer(invocation
                -> invocation.getArgument(0));

        EducationDto result = educationService.updateEducation(1L, validEducationDto);

        assertNotNull(result);
        assertEquals(validEducationDto.institution(), result.institution());
        verify(educationRepository, times(1)).findById(1L);
        verify(educationRepository, times(1)).save(any(Education.class));
    }

    @Test
    void updateEducation_InvalidYearFrom_ThrowsException() {
        assertThrows(DataValidationException.class, ()
                -> educationService.updateEducation(1L, invalidYearFromEducationDto));
        verify(educationRepository, never()).findById(anyLong());
        verify(educationRepository, never()).save(any(Education.class));
    }

    @Test
    void updateEducation_EducationNotFound_ThrowsException() {
        when(educationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, ()
                -> educationService.updateEducation(1L, validEducationDto));
        verify(educationRepository, times(1)).findById(1L);
        verify(educationRepository, never()).save(any(Education.class));
    }

    @Test
    void updateEducation_UserMismatch_ThrowsException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        existingEducation.setUser(anotherUser);

        when(educationRepository.findById(1L)).thenReturn(Optional.of(existingEducation));

        assertThrows(DataValidationException.class, ()
                -> educationService.updateEducation(1L, validEducationDto));
        verify(educationRepository, times(1)).findById(1L);
        verify(educationRepository, never()).save(any(Education.class));
    }

    @Test
    void getById_ValidId_ReturnsEducationDto() {
        when(educationRepository.findById(1L)).thenReturn(Optional.of(existingEducation));

        EducationDto result = educationService.getById(1L);

        assertNotNull(result);
        assertEquals(existingEducation.getInstitution(), result.institution());
        verify(educationRepository, times(1)).findById(1L);
    }

    @Test
    void getById_EducationNotFound_ThrowsException() {
        when(educationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, ()
                -> educationService.getById(1L));
        verify(educationRepository, times(1)).findById(1L);
    }
}