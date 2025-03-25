package school.faang.user_service.dto;

import java.time.LocalDate;

public record CareerDto(
        long id,
        LocalDate from,
        LocalDate to,
        String company,
        String position
) {
}
