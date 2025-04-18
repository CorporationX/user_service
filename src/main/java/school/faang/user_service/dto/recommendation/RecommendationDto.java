package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class RecommendationDto {
    @Nullable
    private Long id;
    @NotNull
    @Positive(message = "Id author in recommendation can't be negative")
    private Long authorId;
    @Positive(message = "Id receiver in recommendation can't be negative")
    private Long receiverId;
    @NotNull
    private String content;
    @Nullable
    private List<SkillOfferDto> skillOffers;
    @Nullable
    private LocalDateTime createdAt;
}
