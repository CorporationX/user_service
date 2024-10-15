package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.enums.RequestStatus;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private String rejectionReason;
}
