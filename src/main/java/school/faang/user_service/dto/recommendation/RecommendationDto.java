package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
@Getter
public class RecommendationDto {
    @Positive(message="id should be positive")
    private Long id;
    @NotNull
    @Positive(message="authorId should be positive")
    private Long authorId;
    @NotNull
    @Positive(message="receiverId should be positive")
    private Long receiverId;
    @NotEmpty(message="content can't be empty")
    private String content;
    private List<SkillOfferDto> skillOffers;
    @NotNull
    private LocalDateTime createdAt;
}