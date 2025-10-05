package school.faang.user_service.dto.career;

import java.time.LocalDate;

public record CreateCareerDto(
        LocalDate from,
        LocalDate to,
        String company,
        String position
) {
}
