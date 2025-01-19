package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    Long id;

    @NotNull(message = "The recommendation must have an authorId")
    Long authorId;

    @NotNull(message = "The recommendation must have an receiverId")
    Long receiverId;

    List<SkillOfferDto> skillOffers;

    @NotBlank(message = "The recommendation should have a content")
    @NotNull(message = "The content cannot be null")
    String content;

    LocalDateTime createdAt;
}
