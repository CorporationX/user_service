package school.faang.user_service.dto.mentorship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Builder
@AllArgsConstructor
public class RequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
