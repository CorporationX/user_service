package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PremiumDto {
    private long id;

    private long userId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public PremiumDto(long id, long userId) {
        this.id = id;
        this.userId = userId;
    }
}
