package school.faang.user_service.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.education.EducationServiceImpl;

import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationOperationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationServiceImpl educationService;

    @Test
    @DisplayName("Should add education when input is valid")
    void addEducationShouldAddEducationWhenValidInput() {
        long userId = 1L;
        EducationDto inputDto = new EducationDto();
        inputDto.setYearFrom(Year.now().getValue() - 1);
        inputDto.setYearTo(Year.now().getValue());
        inputDto.setInstitution("Test University");

        User user = new User();
        user.setId(userId);

        Education education = new Education();
        education.setUser(user);

        EducationDto resultDto = new EducationDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationMapper.toEducation(inputDto)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(resultDto);

        EducationDto actual = educationService.addEducation(userId, inputDto);

        assertEquals(resultDto, actual);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when yearFrom is current year")
    void addEducationShouldThrowExceptionWhenYearFromInvalid() {
        EducationDto dto = new EducationDto();
        dto.setYearFrom(Year.now().getValue());
        dto.setInstitution("Test University");

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> educationService.addEducation(1L, dto)
        );

        assertEquals("YearFrom must be earlier than the current year", exception.getMessage());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when yearTo is before yearFrom")
    void addEducationShouldThrowExceptionWhenYearToBeforeYearFrom() {
        EducationDto dto = new EducationDto();
        dto.setYearFrom(2020);
        dto.setYearTo(2019);
        dto.setInstitution("Test University");

        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, dto));
    }

    @Test
    @DisplayName("Should throw exception when yearTo is in future")
    void addEducationShouldThrowExceptionWhenYearToInFuture() {
        EducationDto dto = new EducationDto();
        dto.setYearFrom(2020);
        dto.setYearTo(Year.now().getValue() + 1);
        dto.setInstitution("Test University");

        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, dto));
    }

    @Test
    @DisplayName("Should update education when input is valid")
    void updateEducationShouldUpdateWhenValid() {
        long userId = 1L;
        long educationId = 10L;

        EducationDto dto = new EducationDto();
        dto.setId(educationId);
        dto.setYearFrom(2010);
        dto.setYearTo(2014);
        dto.setInstitution("University");

        User user = new User();
        user.setId(userId);

        Education education = new Education();
        education.setId(educationId);
        education.setUser(user);

        when(educationRepository.findById(educationId)).thenReturn(Optional.of(education));
        when(educationRepository.save(any())).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(dto);

        EducationDto updated = educationService.updateEducation(userId, dto);

        assertEquals(dto, updated);
        verify(educationMapper).updateEducationFromDto(dto, education);
    }

    @Test
    @DisplayName("Should throw exception when education not found")
    void updateEducationShouldThrowWhenEducationNotFound() {
        EducationDto dto = new EducationDto();
        dto.setId(999L);
        dto.setYearFrom(2010);
        dto.setInstitution("University");

        when(educationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> educationService.updateEducation(1L, dto));
    }

    @Test
    @DisplayName("Should throw exception when user does not own education")
    void updateEducationShouldThrowWhenWrongUser() {
        EducationDto dto = new EducationDto();
        dto.setId(100L);
        dto.setYearFrom(2010);
        dto.setInstitution("University");

        User anotherUser = new User();
        anotherUser.setId(99L);

        Education education = new Education();
        education.setId(100L);
        education.setUser(anotherUser);

        when(educationRepository.findById(100L)).thenReturn(Optional.of(education));

        assertThrows(DataValidationException.class, () -> educationService.updateEducation(1L, dto));
    }

    @Test
    @DisplayName("Should get education by ID if exists")
    void getEducationByIdShouldReturnEducationWhenExists() {
        Education education = new Education();
        education.setId(5L);

        EducationDto dto = new EducationDto();

        when(educationRepository.findById(5L)).thenReturn(Optional.of(education));
        when(educationMapper.toEducationDto(education)).thenReturn(dto);

        EducationDto found = educationService.getEducationById(5L);

        assertEquals(dto, found);
    }

    @Test
    @DisplayName("Should throw exception if education by ID not found")
    void getEducationByIdShouldThrowWhenNotFound() {
        when(educationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> educationService.getEducationById(99L));
    }

    @Test
    @DisplayName("Should throw DataValidationException when user not found")
    void addEducationShouldThrowExceptionWhenUserNotFound() {
        long userId = 1L;
        EducationDto inputDto = new EducationDto();
        inputDto.setYearFrom(Year.now().getValue() - 1);
        inputDto.setInstitution("University");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> educationService.addEducation(userId, inputDto));
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository, educationMapper, educationRepository);
    }

    @Test
    @DisplayName("Should throw DataValidationException when yearFrom is in future")
    void addEducationShouldThrowExceptionWhenYearFromInFuture() {
        long userId = 1L;
        EducationDto inputDto = new EducationDto();
        inputDto.setYearFrom(Year.now().getValue() + 1);
        inputDto.setInstitution("University");

        assertThrows(DataValidationException.class, () -> educationService.addEducation(userId, inputDto));
        verifyNoInteractions(userRepository, educationMapper, educationRepository);
    }
}