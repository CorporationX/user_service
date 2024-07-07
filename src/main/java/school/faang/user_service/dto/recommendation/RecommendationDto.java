package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {

    private long id;
    private long authorId;
    private long receiverId;
    @NonNull
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
