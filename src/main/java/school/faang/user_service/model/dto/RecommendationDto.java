package school.faang.user_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RecommendationDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    @NotBlank(message = "Recommendation should contain non-empty content")
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
