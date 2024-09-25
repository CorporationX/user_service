package school.faang.user_service.dto.mentorshiprequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {
    private long id;
    private String description;
    private long requesterId;
    private long receiverId;
    private RequestStatus status;
    private LocalDate createdAt;
    private String rejectionReason;
}
