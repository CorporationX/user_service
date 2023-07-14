package school.faang.user_service.util.mentorshipRequest.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String message;
    private long currentTime;
}
