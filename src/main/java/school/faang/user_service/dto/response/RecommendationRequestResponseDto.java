package school.faang.user_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class RecommendationRequestResponseDto {

    private String requester;
    private String receiver;
    private String message;
    private String status;
    private String rejectionReason;
//    private Recommendation recommendation;
    private List<String> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
