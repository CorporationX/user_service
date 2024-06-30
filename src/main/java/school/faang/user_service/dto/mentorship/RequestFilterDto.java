package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestFilterDto {

    private String description;

    private Long requesterId;

    private Long receiverId;

    private RequestStatus status;
}
