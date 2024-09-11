package school.faang.user_service.dto_mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {
    private String description;
    private String requesterName;
    private String receiverName;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
