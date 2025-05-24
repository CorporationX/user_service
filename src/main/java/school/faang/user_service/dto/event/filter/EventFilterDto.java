package school.faang.user_service.dto.event.filter;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.filter.FilterDto;
import school.faang.user_service.validation.NotEmptyFilter;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
@NotEmptyFilter
public class EventFilterDto implements FilterDto {
    private String title;
    @Future
    private LocalDateTime startDate;
    @Positive
    private Long ownerId;

    @Override
    public boolean hasFilterCriteria() {
        return !(
                (title == null || title.isEmpty()) &&
                        startDate == null &&
                        ownerId == null
        );
    }
}
