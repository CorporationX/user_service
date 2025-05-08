package school.faang.user_service.dto.mentor;

import lombok.Data;

@Data
public class RequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatusDto status;
}
