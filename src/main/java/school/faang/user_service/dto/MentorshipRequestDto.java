package school.faang.user_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MentorshipRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
