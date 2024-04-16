package school.faang.user_service.dto.analytics;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchAppearanceEventDto {
    @NotNull
    private Long foundUserProfileId;
    @NotNull
    private Long searcherUserId;
    @NotNull
    private LocalDateTime viewingTime;
}
