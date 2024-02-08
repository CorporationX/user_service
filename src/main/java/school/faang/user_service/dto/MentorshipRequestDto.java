package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MentorshipRequestDto {
    private long id;
    private String description;
    private long requesterId;
    private long receiverId;

    public MentorshipRequestDto(long requesterId, long receiverId, String description) {
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.description = description;
    }
}
