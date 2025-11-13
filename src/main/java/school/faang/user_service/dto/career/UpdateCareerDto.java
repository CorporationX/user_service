package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCareerDto(
        @PastOrPresent(message = "Start date must be in the past or present")
        LocalDate from,
        LocalDate to,
        @NotBlank(message = "Company name cannot be blank")
        @Size(min = 1, max = 100, message = "Company name must be between 1 and 100 characters")
        String company,
        @NotBlank(message = "Position cannot be blank")
        @Size(min = 1, max = 100, message = "Position must be between 1 and 100 characters")
        String position
) {
}
