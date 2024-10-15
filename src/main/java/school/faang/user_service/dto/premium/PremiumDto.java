package school.faang.user_service.dto.premium;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumDto {

    @NonNull
    private Long userId;

    @NonNull
    private LocalDateTime startDate;

    @NonNull
    private LocalDateTime endDate;
}
