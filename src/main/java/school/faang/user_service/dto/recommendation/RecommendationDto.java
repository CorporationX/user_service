package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Validated
public class RecommendationDto {
    @NotNull
    Long id;
    @NotNull
    Long authorId;
    @NotNull
    Long receiverId;
    @NotEmpty
    String content;
    List<SkillOfferDto> skillOffers;
    LocalDateTime createdAt;
}
