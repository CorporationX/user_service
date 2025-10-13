package school.faang.user_service.service.workschedule.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalTime;

public class WorkScheduleValidatorTest {
    private WorkScheduleCreateDto workScheduleCreateDto;
    private WorkScheduleDto validWorkScheduleDto;
    private WorkSchedule validWorkSchedule;
    private WorkScheduleDto invalidStartTimeDto;
    private WorkScheduleDto invalidLunchStartDto;
    private WorkScheduleDto invalidLunchEndDto;

    @BeforeEach
    void setUp() {
        workScheduleCreateDto = WorkScheduleCreateDto.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .startLunch(LocalTime.of(13, 0))
                .endLunch(LocalTime.of(14, 0))
                .timezone("Europe/Moscow")
                .build();

        validWorkScheduleDto = WorkScheduleDto.builder()
                .id(1L)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .startLunch(LocalTime.of(13, 0))
                .endLunch(LocalTime.of(14, 0))
                .timezone("Europe/Moscow")
                .build();

        validWorkSchedule = WorkSchedule.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .startLunch(LocalTime.of(13, 0))
                .endLunch(LocalTime.of(14, 0))
                .timezone("Europe/Moscow")
                .build();

        invalidStartTimeDto = WorkScheduleDto.builder()
                .id(2L)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(18, 0))
                .startLunch(LocalTime.of(13, 0))
                .endLunch(LocalTime.of(14, 0))
                .timezone("Europe/Moscow")
                .build();

        invalidLunchStartDto = WorkScheduleDto.builder()
                .id(3L)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .startLunch(LocalTime.of(15, 0))
                .endLunch(LocalTime.of(14, 0))
                .timezone("Europe/Moscow")
                .build();

        invalidLunchEndDto = WorkScheduleDto.builder()
                .id(4L)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(16, 0))
                .startLunch(LocalTime.of(13, 0))
                .endLunch(LocalTime.of(17, 0))
                .timezone("Europe/Moscow")
                .build();
    }

    @Test
    void testAllTimesCorrectWithWorkScheduleCreateDto() {
        Assertions.assertDoesNotThrow(() -> WorkScheduleValidator.validateForCreate(workScheduleCreateDto));
    }

    @Test
    void testAllTimesCorrectWithWorkScheduleDto() {
        Assertions.assertDoesNotThrow(() -> WorkScheduleValidator.validate(validWorkScheduleDto));
    }

    @Test
    void testAllTimesCorrectWithWorkSchedule() {
        Assertions.assertDoesNotThrow(() -> WorkScheduleValidator.validateEntity(validWorkSchedule));
    }

    @Test
    void testStartTimeNotBeforeLunchStart() {
        Assertions.assertThrows(DataValidationException.class, () ->
                WorkScheduleValidator.validate(invalidStartTimeDto));
    }

    @Test
    void testLunchStartTimeNotBeforeLunchEnd() {
        Assertions.assertThrows(DataValidationException.class, () ->
                WorkScheduleValidator.validate(invalidLunchStartDto));
    }

    @Test
    void testLunchEndTimeNotBeforeEnd() {
        Assertions.assertThrows(DataValidationException.class, () ->
                WorkScheduleValidator.validate(invalidLunchEndDto));
    }
}
