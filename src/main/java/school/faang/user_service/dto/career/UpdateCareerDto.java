package school.faang.user_service.dto.career;

import java.time.LocalDate;

public record UpdateCareerDto(
        LocalDate from,
        LocalDate to,
        String company,
        String position
) {
}
