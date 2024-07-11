package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    @PositiveOrZero
    private long id;
    @NotNull
    private String username;
    @NotNull
    @PositiveOrZero
    private long userId;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
}
