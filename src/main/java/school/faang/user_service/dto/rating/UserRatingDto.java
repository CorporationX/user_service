package school.faang.user_service.dto.rating;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class UserRatingDto {
    private final boolean goalRating;
    private final boolean skillRating;
    private final boolean followeeRating;
    private final boolean menteeRating;
    private final boolean premiumRating;
}
