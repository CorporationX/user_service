package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchAppearanceEventDto {
    private long userId;
    private long searcherId;
    private LocalDateTime date;
}
