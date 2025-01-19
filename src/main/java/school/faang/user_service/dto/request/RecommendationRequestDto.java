package school.faang.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.dto.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class RecommendationRequestDto {

    private Long id;
    @NotBlank(message = "Message cannot be blank")
    private String message;
    private RequestStatus status;
    private List<Long> skillIds;
    private Long receiverId;
    private Long requesterId;
    private LocalDateTime createdAt;

}
