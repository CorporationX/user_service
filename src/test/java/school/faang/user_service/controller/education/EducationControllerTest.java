package school.faang.user_service.controller.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EducationControllerTest {

    @Mock
    private EducationService educationService;
    @InjectMocks
    private EducationController educationController;

    private EducationDto educationDto;

    @BeforeEach
    void setUp() {
        educationDto = new EducationDto();
        educationDto.setId(1L);
        educationDto.setYearFrom(2024);
        educationDto.setYearTo(2025);
        educationDto.setInstitution("SpbGTU");
        educationDto.setEducationLevel("bakalavriat");
        educationDto.setSpecialization("IT");
    }

    @Test
    void testAddEducation() {
        long userId = 1L;
        when(educationService.addEducation(userId, educationDto)).thenReturn(educationDto);

        ResponseEntity<EducationDto> response = educationController.addEducation(userId, educationDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(educationDto, response.getBody());
        verify(educationService, times(1)).addEducation(userId, educationDto);
    }

    @Test
    void testUpdateEducation() {
        long userId = 1L;
        when(educationService.updateEducation(userId, educationDto)).thenReturn(educationDto);

        ResponseEntity<EducationDto> response = educationController.updateEducation(userId, educationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(educationDto, response.getBody());
        verify(educationService, times(1)).updateEducation(userId, educationDto);
    }

    @Test
    void testGetEducationByEducationId() {
        long educationId = 1L;
        when(educationService.getById(educationId)).thenReturn(educationDto);

        ResponseEntity<EducationDto> response = educationController.getById(educationId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(educationDto, response.getBody());
        verify(educationService, times(1)).getById(educationId);
    }

    @Test
    void testExceptionHandler() {
        DataValidationException exception = new DataValidationException("Incorrect data");

        ResponseEntity<String> response = educationController.handleDataValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect data", response.getBody());
    }
}