package school.faang.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PremiumDto {

    private long id;
    private long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
