package school.faang.user_service.dto.event;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class ProfileViewEvent {
    private Long viewedUserId;
    private Long viewingUserId;
    private LocalDateTime viewedAt;
}
