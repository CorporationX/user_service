package school.faang.user_service.validation.career;

import lombok.experimental.UtilityClass;
import school.faang.user_service.entity.Career;
import school.faang.user_service.exception.common.DataValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@UtilityClass
public class CareerValidation {
    public static void validateDateFrom(Career career) {
        if (!career.getDateFrom().isBefore(LocalDate.now())) {
            throw new DataValidationException("Дата начала карьеры не может быть больше текущей даты.");
        }
    }

    public static void validateDateTo(Career career) {
        if (!career.getDateTo().isAfter(career.getDateFrom())) {
            throw new DataValidationException("Дата конца карьеры не может быть меньше или равна дате начала.");
        }
    }

    public static void validateDateRanges(Career career) {
        LocalDate minDate = LocalDate.ofEpochDay(0);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        boolean hasInvalidDates = Stream.of(career.getDateFrom(), career.getDateTo())
                .anyMatch(date -> date.isBefore(minDate) || date.isAfter(currentDate));

        if (hasInvalidDates) {
            throw new DataValidationException("Дата не может быть меньше " +
                    minDate.format(formatter) + " и больше текущей даты.");
        }
    }
}