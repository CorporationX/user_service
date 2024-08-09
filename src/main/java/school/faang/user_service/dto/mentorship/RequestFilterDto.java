package school.faang.user_service.dto.mentorship;

import lombok.Data;

@Data
public class RequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private String status;
}
