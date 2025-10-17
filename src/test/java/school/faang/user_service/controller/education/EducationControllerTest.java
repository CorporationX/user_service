package school.faang.user_service.controller.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EducationControllerTest {

    private UserContext userContext;
    private EducationService educationService;
    private EducationController controller;

    @BeforeEach
    void setUp() {
        userContext = mock(UserContext.class);
        educationService = mock(EducationService.class);
        controller = new EducationController(userContext, educationService);
    }

    @Test
    void addEducation_valid_callsServiceWithUserId() {
        long userId = 55L;
        when(userContext.getUserId()).thenReturn(userId);

        CreateEducationDto in = new CreateEducationDto(2016, 2020, "   KFU  ", "B", "CS");
        EducationDto expected = new EducationDto(1L, 2016, 2020, "KFU", "B", "CS");
        when(educationService.addEducation(userId, in)).thenReturn(expected);

        EducationDto out = controller.addEducation(in);

        assertEquals(expected, out);
        verify(userContext).getUserId();
        verify(educationService).addEducation(userId, in);
        verifyNoMoreInteractions(userContext, educationService);
    }

    @Test
    void addEducation_throws_whenServiceValidatesAndFails() {
        long userId = 123L;
        when(userContext.getUserId()).thenReturn(userId);

        // case1: yearFrom == null — сервис валидирует и кидает исключение
        CreateEducationDto bad1 = new CreateEducationDto(null, 2020, "Uni", "B", "CS");
        when(educationService.addEducation(eq(userId), eq(bad1)))
                .thenThrow(new DataValidationException("Не указан год начала обучения"));
        assertThrows(DataValidationException.class, () -> controller.addEducation(bad1));
        verify(educationService).addEducation(userId, bad1);

        // case2: institution == null
        CreateEducationDto bad2 = new CreateEducationDto(2018, 2020, null, "B", "CS");
        when(educationService.addEducation(eq(userId), eq(bad2)))
                .thenThrow(new DataValidationException("Обязательное поле"));
        assertThrows(DataValidationException.class, () -> controller.addEducation(bad2));
        verify(educationService).addEducation(userId, bad2);

        // case3: institution blank
        CreateEducationDto bad3 = new CreateEducationDto(2018, 2020, "   ", "B", "CS");
        when(educationService.addEducation(eq(userId), eq(bad3)))
                .thenThrow(new DataValidationException("Название учебного заведения не может быть пустым"));
        assertThrows(DataValidationException.class, () -> controller.addEducation(bad3));
        verify(educationService).addEducation(userId, bad3);

        verify(userContext, times(3)).getUserId();
        verifyNoMoreInteractions(userContext, educationService);
    }

    @Test
    void updateEducation_valid_callsServiceWithUserId() {
        long userId = 77L;
        long educationId = 100L;
        when(userContext.getUserId()).thenReturn(userId);

        UpdateEducationDto dto = new UpdateEducationDto(2010, null, "Uni", null, null);
        EducationDto expected = new EducationDto(100L, 2010, null, "Uni", null, null);
        when(educationService.updateEducation(userId, educationId, dto)).thenReturn(expected);

        EducationDto out = controller.updateEducation(educationId, dto);

        assertEquals(expected, out);
        verify(userContext).getUserId();
        verify(educationService).updateEducation(userId, educationId, dto);
        verifyNoMoreInteractions(userContext, educationService);
    }

    @Test
    void updateEducation_throws_whenServiceValidatesAndFails() {
        long userId = 1L;
        long educationId = 10L;
        when(userContext.getUserId()).thenReturn(userId);

        // case1: yearFrom == null
        UpdateEducationDto bad1 = new UpdateEducationDto(null, null, "Uni", null, null);
        when(educationService.updateEducation(eq(userId), eq(educationId), eq(bad1)))
                .thenThrow(new DataValidationException("Обязательно, укажите дату начала обучения"));
        assertThrows(DataValidationException.class, () -> controller.updateEducation(educationId, bad1));
        verify(educationService).updateEducation(userId, educationId, bad1);

        // case2: institution == null
        UpdateEducationDto bad2 = new UpdateEducationDto(2010, null, null, null, null);
        when(educationService.updateEducation(eq(userId), eq(educationId), eq(bad2)))
                .thenThrow(new DataValidationException("Обязательно, укажите место обучения"));
        assertThrows(DataValidationException.class, () -> controller.updateEducation(educationId, bad2));
        verify(educationService).updateEducation(userId, educationId, bad2);

        // case3: institution blank
        UpdateEducationDto bad3 = new UpdateEducationDto(2010, null, "   ", null, null);
        when(educationService.updateEducation(eq(userId), eq(educationId), eq(bad3)))
                .thenThrow(new DataValidationException("Обязательно, укажите место обучения"));
        assertThrows(DataValidationException.class, () -> controller.updateEducation(educationId, bad3));
        verify(educationService).updateEducation(userId, educationId, bad3);

        verify(userContext, times(3)).getUserId();
        verifyNoMoreInteractions(userContext, educationService);
    }

    @Test
    void getEducationById_returnsServiceResult() {
        long educationId = 5L;
        EducationDto expected = new EducationDto(5L, 2010, 2014, "Uni", "B", "CS");
        when(educationService.getById(educationId)).thenReturn(expected);

        EducationDto out = controller.getEducationById(educationId);

        assertEquals(expected, out);
        verify(educationService).getById(educationId);
        verifyNoMoreInteractions(educationService);
    }
}
