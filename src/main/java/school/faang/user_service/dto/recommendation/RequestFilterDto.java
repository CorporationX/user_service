package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private Long id;
    private RequestStatus status;
    private String description;
    private Long requesterId;
    private Long receiverId;
}
