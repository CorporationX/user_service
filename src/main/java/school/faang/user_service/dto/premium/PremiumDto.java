package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Evgenii Malkov
 */
@Data
@AllArgsConstructor
public class PremiumDto {
    @NotNull
    @Positive
    private long id;
    @NotNull
    private String username;
    @NotNull
    @Positive
    private long userId;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
}
